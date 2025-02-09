package ru.kpfu.game.client;

import ru.kpfu.game.client.frames.GameMenu;
import ru.kpfu.game.exceptions.MessageWriteException;
import ru.kpfu.game.protocol.Message;
import ru.kpfu.game.protocol.MessageProtocol;
import ru.kpfu.game.utils.constants.MessageType;
import java.io.IOException;
import java.net.Socket;

public class Client {
    private Socket socket;
    private GameGUI gameGUI;

    public void start() {
        try {
            socket = new Socket("localhost", 12345);
            gameGUI = new GameGUI(socket);
            gameGUI.init();
            new Thread(new ServerListener(socket, gameGUI)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}