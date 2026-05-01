package model;

import java.util.Random;

public class GameModel {
    private int score;
    private int level;
    private GameState state;
    private Tetromino currentPiece, nextPiece, holdPiece;
    private Board board;

    public GameModel() {
        this.board = new Board();
        this.state = GameState.MENU;
    }

    public void updateScore(int lineCount) {

    }

    public void spawnNewPiece() {
        Random rand = new Random();
        int randomId = rand.nextInt(7);
        this.currentPiece = new Tetromino(randomId);
        if (!board.isValidMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            setGameOver();
        }
    }

    public void holdCurrentPiece() {

    }

    public Tetromino getCurrentPiece() {
        return this.currentPiece;
    }

    public int getScore() {
        return 0;
    }

    public int getLevel() {
        return 0;
    }

    public GameState getState() {
        return this.state;
    }
    public Board getBoard() {
        return  this.board;
    }

    public void setGameOver(){
        this.state = GameState.GAME_OVER;
    }

    public void reset() {
        board.reset();
        score = 0;
        level = 1;
        state = GameState.PLAYING;
        spawnNewPiece();
    }
  public static void main(String[] args) {
    GameModel model = new GameModel();

    System.out.println("Trạng thái game: " + model.getState());
    System.out.println("Kích thước Board: " + model.getBoard().getGrid().length + " hàng.");

    model.spawnNewPiece();
    Tetromino piece = model.getCurrentPiece();

    System.out.println("\nĐã sinh gạch thành công!");
    System.out.println("Màu gạch: " + piece.getColor());
    System.out.println("Tọa độ xuất phát:");
    piece.printCoords();
}}


