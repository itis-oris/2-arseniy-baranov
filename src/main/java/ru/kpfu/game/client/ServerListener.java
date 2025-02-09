package ru.kpfu.game.client;

import ru.kpfu.game.exceptions.MessageReadException;
import ru.kpfu.game.protocol.Message;
import ru.kpfu.game.protocol.MessageProtocol;
import ru.kpfu.game.utils.constants.MessageType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

class ServerListener implements Runnable {
    private Socket socket;
    private GameGUI gameGUI;

    public ServerListener(Socket socket, GameGUI gameGUI) {
        this.socket = socket;
        this.gameGUI = gameGUI;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = MessageProtocol.readMessage(socket.getInputStream());
                if (message != null) {
                    switch (message.getType()) {
                        case MessageType.WAIT:
                            gameGUI.updateWaitingMessage("knjkbj");
                            break;
                        case MessageType.COUNTDOWN:
                            gameGUI.updateWaitingMessage(new String(message.getData()));
                            break;
                        case MessageType.START:
                            gameGUI.showGameScreen();
                            break;
                        case MessageType.GOAL:
                            String[] parts = new String(message.getData()).split(",");
                            String goalMessage = parts[0];
                            int score1 = Integer.parseInt(parts[1]);
                            int score2 = Integer.parseInt(parts[2]);
                            gameGUI.getGamePanel().handleGoal(goalMessage);
                            gameGUI.getGamePanel().updateScore(score1, score2);
                            break;
                        case MessageType.BROADCAST_GAMESTATE:
                            gameGUI.getGamePanel().updateState(new String(message.getData()));
                            break;
                        case MessageType.GAMEOVER:
                            String[] messagePart = new String(message.getData()).split(",");
                            String reason = messagePart[0];
                            String winner = messagePart[1];
                            int score11 = Integer.parseInt(messagePart[2]);
                            int score22 = Integer.parseInt(messagePart[3]);
                            String winner2 = reason.equals("DISCONNECTED") ? "Opponent disconnected" :
                                    winner;
                            gameGUI.showGameOverScreen(winner2, score11, score22);
                            break;
                        case MessageType.GAME_TIME:
                            gameGUI.getGamePanel().setTotalSeconds(Integer.parseInt(new String(message.getData())));
                            break;
                    }
                }
            }
        } catch (IOException | MessageReadException e) {
            e.printStackTrace();
        }
    }
}
