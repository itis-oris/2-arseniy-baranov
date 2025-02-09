package ru.kpfu.game.server;

public class GameState {
    private int puckX = 400, puckY = 300;
    private double puckSpeedX = 5, puckSpeedY = 5;
    private int player1Y = 250, player2Y = 250;
    private final int WIDTH = 800, HEIGHT = 600;

    private int player1Score = 0;
    private int player2Score = 0;
    private String goalMessage = "";
    private boolean isGoalScored = false;

    private int maxScore = 5;
    private boolean isGameOver = false;
    private String winner;

    public void update() {
        if (isGoalScored) {
            return;
        }

        puckX += puckSpeedX;
        puckY += puckSpeedY;

        if (puckY <= 0 || puckY >= HEIGHT - 50) {
            puckSpeedY = -puckSpeedY;
        }

        checkForWinnigOnePoint();
        checkForBounce();
    }

    private void checkForWinnigOnePoint() {
        if (puckX <= 0) {
            player2Score++;
            goalMessage = "Player 2" + " забил гол!";
            isGoalScored = true;
            resetPuckAfterDelay(3, true);
        } else if (puckX >= WIDTH - 20) {
            player1Score++;
            goalMessage = "Player 1" + " забил гол!";
            isGoalScored = true;
            resetPuckAfterDelay(3, false);
        }
        checkForWin();
    }

    private void checkForWin() {
        if (player1Score == maxScore) {
            isGameOver = true;
            winner = "Player 1";
        } else if (player2Score == maxScore) {
            isGameOver = true;
            winner = "Player 2";
        }
    }

    private void checkForBounce() {
        if (puckX <= 60 && puckY + 10 >= player1Y && puckY <= player1Y + 100) {
            puckSpeedX = -(1.005 * puckSpeedX);
            puckX = 60;
        }
        if (puckX >= WIDTH - 80 && puckY + 10 >= player2Y && puckY <= player2Y + 100) {
            puckSpeedX += 5;
            puckSpeedX = -(1.005 * puckSpeedX);
            puckX = WIDTH - 80;
        }
    }

    public void resetPuckAfterDelay(int seconds, boolean towardsPlayer1) {
        new Thread(() -> {
            try {
                Thread.sleep(seconds * 1000);
                resetPuck();
                puckSpeedX = 5;
                puckSpeedX = towardsPlayer1 ? puckSpeedX : -puckSpeedX;
                isGoalScored = false;
                goalMessage = "";
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void resetPuck() {
        puckX = 400;
        puckY = 300;
        puckSpeedX = -puckSpeedX;
    }

    public synchronized void updatePlayerPosition(int playerId, int playerY) {
        if (playerId == 1) {
            player1Y = playerY;
        } else if (playerId == 2) {
            player2Y = playerY;
        }
    }

    @Override
    public synchronized String toString() {
        return puckX + "," + puckY + "," + player1Y + "," + player2Y;
    }

    public synchronized void setPlayerScores(int player1Score, int player2Score) {
        this.player1Score = player1Score;
        this.player2Score = player2Score;
    }

    public synchronized int getPlayer1Score() {
        return player1Score;
    }

    public synchronized int getPlayer2Score() {
        return player2Score;
    }

    public boolean isGoalScored() {
        return isGoalScored;
    }

    public String getGoalMessage() {
        return goalMessage;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public String getWinner() {
        return winner;
    }
}