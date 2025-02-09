package ru.kpfu.game.client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

class GamePanel extends JPanel {
    private String currentGoalMessage = "";
    private int puckX, puckY, player1Y, player2Y;
    private int countdownValue = -1;
    private boolean showCountdown = false;
    private boolean countdownActive = false;
    private int player1Score = 0;
    private int player2Score = 0;
    private int totalSeconds = 0;
    //    private Image backgroundImage;
    private ImageIcon animatedBackground;

    public GamePanel() {

        URL imgUrl = getClass().getClassLoader().getResource("background.gif");
        if (imgUrl == null) {
            throw new RuntimeException("GIF (or simple image) not found!");
        }
        animatedBackground = new ImageIcon(imgUrl);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (animatedBackground != null) {
            g.drawImage(animatedBackground.getImage(), 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }

        g.setColor(Color.WHITE);
        drawStar(g, puckX, puckY, 10, 20);
//        g.fillOval(puckX, puckY, 20, 20); // Шайба

        g.fillRect(50, player1Y, 10, 100); // Игрок 1
        g.fillRect(getWidth() - 60, player2Y, 10, 100); // Игрок 2
        g.setFont(new Font("Arial", Font.BOLD, 24));

        g.drawString("Player 1: " + player1Score, 50, 50);
        g.drawString("Player 2: " + player2Score, getWidth() - 200, 50);

        g.drawString(formTimerString(), getWidth() / 2, 50);

        if (!currentGoalMessage.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString(currentGoalMessage, getWidth() / 2 - 150, getHeight() / 2 - 50);
        }

        if (showCountdown) {
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString("Новый тайм через: " + countdownValue, getWidth() / 2 - 200, getHeight() / 2 + 50);
        }
    }

    private void drawStar(Graphics g, int x, int y, int innerRadius, int outerRadius) {
        Graphics2D g2d = (Graphics2D) g;
        int points = 7; // Количество концов звезды
        double angle = Math.PI / points;

        int[] xPoints = new int[points * 2];
        int[] yPoints = new int[points * 2];

        for (int i = 0; i < points * 2; i++) {
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = x + (int) (radius * Math.cos(i * angle));
            yPoints[i] = y + (int) (radius * Math.sin(i * angle));
        }

        g2d.fillPolygon(xPoints, yPoints, points * 2);
    }

    public synchronized void updateState(String gameStateString) {
        String[] parts = gameStateString.split(",");
        puckX = Integer.parseInt(parts[0]);
        puckY = Integer.parseInt(parts[1]);

        player1Y = Math.max(0, Math.min(getHeight() - 100, Integer.parseInt(parts[2])));
        player2Y = Math.max(0, Math.min(getHeight() - 100, Integer.parseInt(parts[3])));
        repaint();
    }

    private void startCountdown() {
        if (countdownActive) {
            return;
        }
        countdownActive = true;
        countdownValue = 3; //TODO: В гейм стейт отображение подписи о голе держиттся тоже 3 секунды, если менять здесь, то и там надо изменить.сделать чтобы было взаимосвязано
        showCountdown = true;

        Timer timer = new Timer(1000, e -> {
            countdownValue--;
            if (countdownValue <= 0) {
                ((Timer) e.getSource()).stop();
                showCountdown = false;
                currentGoalMessage = "";
                countdownActive = false;
            }
            repaint();
        });
        timer.start();
    }

    public void updateScore(int player1Score, int player2Score) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public void handleGoal(String goalMessage) {
        currentGoalMessage = goalMessage;
        if (!countdownActive) {
            startCountdown();
        }
        repaint();
    }

    public String formTimerString() {
        if (totalSeconds < 0) {
            return "00:00"; // Или можно выбросить исключение
        }
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        return String.format("%02d:%02d", minutes, seconds);
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }
}
