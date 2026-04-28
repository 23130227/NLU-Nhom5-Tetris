package model;

public class GameModel {
    private int score;
    private int level;
    private GameState state;
    private Board board;
    private Tetromino currentPiece, nextPiece, holdPiece;

    public GameModel() {
        this.board = new Board();
        this.score = 0;
        this.level = 1;

        // Khởi tạo miếng ghép đầu tiên để test
        this.currentPiece = new Tetromino(TetrominoType.T);
        this.nextPiece = new Tetromino(TetrominoType.L);
    }
    public void updateScore(int lineCount) {

    }

    public void spawnNewPiece() {

    }

    public void holdCurrentPiece() {

    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getNextPiece() { return nextPiece; }

    public Tetromino getHoldPiece() { return holdPiece; }

    public int getScore() {
        return 0;
    }

    public int getLevel() {
        return 0;
    }

    public GameState getState() {
        return null;
    }

    public Board getBoard() { return board;}
}
