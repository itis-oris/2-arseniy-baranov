package ru.kpfu.game.client.frames;

import javax.swing.*;
import java.awt.*;

public class GameMenu extends JFrame {
    private Runnable onPlayAction;

    public GameMenu(Runnable onPlayAction) {
        this.onPlayAction = onPlayAction;

        // Настройки окна
        setTitle("Star Pong");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        getContentPane().setBackground(Color.BLACK);


        JLabel titleLabel = new JLabel("Star Pong", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBackground(Color.BLACK);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 200, 50, 200));

        // Кнопка "Играть"
        JButton playButton = createCustomButton("Играть");
        playButton.addActionListener(e -> {
            dispose();
            onPlayAction.run();
        });


        // Кнопка "Выход"
        JButton exitButton = createCustomButton("Выход");
        exitButton.addActionListener(e -> System.exit(0));
        
        buttonPanel.add(playButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.CENTER);
    }

    private JButton createCustomButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
