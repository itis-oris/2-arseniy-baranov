package ru.kpfu.game.client;
import ru.kpfu.game.exceptions.MessageWriteException;
import ru.kpfu.game.protocol.Message;
import ru.kpfu.game.protocol.MessageProtocol;
import ru.kpfu.game.utils.constants.MessageType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.Socket;

public class GameGUI {
    private Socket socket;
    private GamePanel gamePanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel waitingLabel;
    private JLabel resultLabel;
    private JLabel timeLabel;

    public GameGUI(Socket socket) {
        this.socket = socket;
    }

    public void init() {
        JFrame frame = new JFrame("Star Pong");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        setupWaitingScreen();
        setupGameOverScreen();
        setupGameScreen();

        frame.add(mainPanel);
        frame.setVisible(true);

        frame.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!gamePanel.isShowing()) {
                    return;
                }
                int y = e.getY() - gamePanel.getLocationOnScreen().y;
                y = Math.max(0, Math.min(gamePanel.getHeight() - 100, y));
                try {
                    sendMove(y);
                } catch (MessageWriteException | IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void setupWaitingScreen() {
        JPanel waitingPanel = new JPanel();
        waitingPanel.setBackground(Color.BLACK);
        waitingLabel = new JLabel("Waiting for players...");
        waitingLabel.setForeground(Color.WHITE);
        waitingLabel.setFont(new Font("Arial", Font.BOLD, 24));
        waitingPanel.setLayout(new GridBagLayout());
        waitingPanel.add(waitingLabel);
        mainPanel.add(waitingPanel, "WAITING");
    }

    private void setupGameOverScreen() {
        JPanel gameOverScreen = new JPanel();
        gameOverScreen.setBackground(Color.BLACK);
        gameOverScreen.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setForeground(Color.WHITE);
        gameOverLabel.setFont(new Font("Arial", Font.BOLD, 30));

        resultLabel = new JLabel();
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setFont(new Font("Arial", Font.BOLD, 24));

        timeLabel = new JLabel();
        timeLabel.setForeground(Color.WHITE);
        timeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        gameOverScreen.add(gameOverLabel, gbc);
        gameOverScreen.add(resultLabel, gbc);
        gameOverScreen.add(timeLabel, gbc);

        mainPanel.add(gameOverScreen, "GAMEOVER");
    }

    private void setupGameScreen() {
        gamePanel = new GamePanel();
        mainPanel.add(gamePanel, "GAME");
        Timer timer = new Timer(16, e -> gamePanel.repaint());
        timer.start();
    }

    public void showWaitingScreen() {
        cardLayout.show(mainPanel, "WAITING");
    }

    public void showGameOverScreen(String winner, int score1, int score2) {
        cardLayout.show(mainPanel, "GAMEOVER");
        resultLabel.setText(winner + ": " + Math.max(score1, score2) + " / " + Math.min(score1, score2));
        timeLabel.setText("Time: " + gamePanel.formTimerString());
    }

    public void showGameScreen() {
        cardLayout.show(mainPanel, "GAME");
    }

    public void updateWaitingMessage(String message) {
        waitingLabel.setText(message);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public void sendMove(int y) throws MessageWriteException, IOException {
        Message message = new Message(MessageType.MOVE, String.valueOf(y).getBytes());
        MessageProtocol.sendMessage(socket.getOutputStream(), message);
    }
}
