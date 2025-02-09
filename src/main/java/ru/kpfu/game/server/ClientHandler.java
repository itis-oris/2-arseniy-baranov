package ru.kpfu.game.server;

import ru.kpfu.game.exceptions.MessageReadException;
import ru.kpfu.game.exceptions.MessageWriteException;
import ru.kpfu.game.protocol.Message;
import ru.kpfu.game.protocol.MessageProtocol;
import ru.kpfu.game.utils.GameMessageProvider;
import ru.kpfu.game.utils.constants.MessageType;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

class ClientHandler implements Runnable {
    private Socket socket;
    private GameState gameState;
    private Server server;
    private PrintWriter out;
    private int playerId;

    public ClientHandler(Socket socket, GameState gameState, Server server, int playerId) {
        this.socket = socket;
        this.gameState = gameState;
        this.server = server;
        this.playerId = playerId;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Message message = MessageProtocol.readMessage(socket.getInputStream());
                if (message != null) {
                    switch (message.getType()) {
                        case MessageType.MOVE:
                            int posY = Integer.parseInt(new String(message.getData()));
                            gameState.updatePlayerPosition(playerId, posY);
                            break;
                    }
                }
            }
        } catch (IOException | MessageReadException e) {
            e.printStackTrace();
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void send(int type, String message) {
        Message m = GameMessageProvider.createMessage(type, message.getBytes());
        try {
            MessageProtocol.sendMessage(socket.getOutputStream(), m);
        } catch (MessageWriteException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
