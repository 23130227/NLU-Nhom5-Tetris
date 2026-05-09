package controller;

import model.Board;
import model.GameModel;
import model.GameState;
import model.Tetromino;
import view.GameGUI;

import java.util.Timer;

public class GameController {
    private Timer gameTimer;
    private GameModel model;
    private GameGUI view;
    private boolean canSoftDrop;

    public void gameLoop() {
        if (model.getState() == GameState.GAME_OVER) {
            pauseGame();
            view.showGameOver(); // Bây giờ sẽ hiển thị đúng số điểm thực tế
            return;
        }

        Tetromino current = model.getCurrentPiece();
        Board board = model.getBoard();

        if (board.isValidMove(current, current.getX(), current.getY() + 1)) {
            current.move(0, 1);
        } else {
            board.lockPiece(current);

            java.util.List<Integer> fullLines = board.scanFullLines();
            if (!fullLines.isEmpty()) {
                board.clearAndShift(fullLines);
                // GỌI CẬP NHẬT ĐIỂM TẠI ĐÂY
                model.updateScore(fullLines.size());
            }

            model.spawnNewPiece();
            canSoftDrop = false;
        }
        view.refresh();
    }
    public void startGame(){

    }


    public void pauseGame(){

    }
}
