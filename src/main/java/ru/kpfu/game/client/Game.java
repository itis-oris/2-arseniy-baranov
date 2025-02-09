package ru.kpfu.game.client;

import ru.kpfu.game.client.frames.GameMenu;

import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameMenu menu = new GameMenu(() -> new Client().start());
            menu.setVisible(true);
        });
    }
}
