package model;

public class GameModel {
    private int score = 0;
    private int level = 0;
    private GameState state;
    private Tetromino currentPiece, nextPiece, holdPiece;


    public void updateScore(int lineCount) {
        if (lineCount <= 0) return;

        int pointsEarned = 0;
        if (lineCount >= 4) {
            pointsEarned = calculateAdvancedScore(lineCount);

        } else {
            switch (lineCount) {
                case 1:
                    pointsEarned = 100;
                    break;
                case 2:
                    pointsEarned = 300;
                    break;
                case 3:
                    pointsEarned = 500;
                    break;
            }

        }
        this.score += pointsEarned;
        this.level = this.score / 1000;
    }

    private int calculateAdvancedScore(int lineCount) {
        return 1000;
    }

    public void spawnNewPiece() {

    }

    public void holdCurrentPiece() {

    }

    public Tetromino getCurrentPiece() {
        return null;
    }

    public int getScore() {
        return 0;
    }

    public int getLevel() {
        return 0;
    }

    public GameState getState() {
        return null;
    }
}
