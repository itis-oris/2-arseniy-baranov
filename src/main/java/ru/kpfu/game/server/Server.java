package ru.kpfu.game.server;


import ru.kpfu.game.utils.constants.MessageType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class Server {
    private static final int PORT = 12345;
    private List<ClientHandler> clients = new ArrayList<>();
    private GameState gameState;
    private final CountDownLatch latch = new CountDownLatch(2);
    private Timer timer = new Timer();
    private Thread countingSecondsThread;

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("ru.kpfu.game.server.Server started on port " + PORT);
            gameState = new GameState();

            System.out.println("Waiting for clients...");

            Socket clientSocket1 = serverSocket.accept();
            System.out.println("1 ru.kpfu.game.client connected");
            ClientHandler clientHandler = new ClientHandler(clientSocket1, gameState, this, 1);
            clients.add(clientHandler);
            new Thread(clientHandler).start();
            latch.countDown();

            Socket clientSocket2 = serverSocket.accept();
            System.out.println("2 ru.kpfu.game.client connected");
            ClientHandler clientHandler2 = new ClientHandler(clientSocket2, gameState, this, 2);
            clients.add(clientHandler2);
            new Thread(clientHandler2).start();
            latch.countDown();

            startCountdown(5);
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }

    private void startCountdown(int seconds) {
        new Thread(() -> {
            try {
                for (int i = seconds; i > 0; i--) {
                    System.out.println(i);
                    sendToAllClients(MessageType.COUNTDOWN, String.valueOf(i));
                    Thread.sleep(1000);
                }
                sendToAllClients(MessageType.START, "");
                startCounting();
                startGameLoop();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void startCounting() {
        countingSecondsThread = new Thread(() -> {
            try {
                for (int i = 0; i < 10000; i++) {
                    sendToAllClients(MessageType.GAME_TIME, String.valueOf(i));
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        countingSecondsThread.start();
    }

    private void stopCounting() {
        countingSecondsThread.interrupt();
    }

    private void startGameLoop() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                synchronized (gameState) {
                    gameState.update();
                    broadcastGameState();
                    if (gameState.isGameOver()) {
                        stopCounting();
                        sendToAllClients(MessageType.GAMEOVER,"FINISHING," + gameState.getWinner() + "," + gameState.getPlayer1Score() + "," + gameState.getPlayer2Score());
                        stopGameLoop();
                    } else if (gameState.isGoalScored()) {
                        sendToAllClients(MessageType.GOAL, gameState.getGoalMessage() + "," + gameState.getPlayer1Score() + "," + gameState.getPlayer2Score());
                    }
                }
            }
        }, 0, 16); // Обновление каждые 16 мс (~60 FPS)
    }

    private void stopGameLoop() {
        timer.cancel();
    }

    private void broadcastGameState() {
        for (ClientHandler client : clients) {
            client.send(MessageType.BROADCAST_GAMESTATE, gameState.toString());
        }
    }

    private void sendToAllClients(int type, String message) {
        for (ClientHandler client : clients) {
            client.send(type, message);
        }
    }

    public void removeClient(ClientHandler client) {
        clients.remove(client);

        if (clients.size() < 2) {
            System.out.println("Player disconnected. Ending the game.");
            stopCounting();
            stopGameLoop();

            int player1Score = gameState.getPlayer1Score();
            int player2Score = gameState.getPlayer2Score();
            String winner = gameState.getWinner();

            String message = "DISCONNECTED," + winner + "," + player1Score + "," + player2Score;

            sendToAllClients(MessageType.GAMEOVER,message);
        }
    }
}
