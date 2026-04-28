package model;

public class GameModel {
    private int score;
    private int level;
    private GameState state;
    private Tetromino currentPiece, nextPiece, holdPiece;

    public void updateScore(int lineCount) {

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
