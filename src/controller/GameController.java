package controller;

import model.Board;
import model.GameModel;
import model.GameState;
import model.Tetromino;
import view.GameGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GameController {
    private Timer gameTimer;
    private GameModel model;
    private GameGUI view;
    private boolean canSoftDrop = true;

    public GameController(GameModel model, GameGUI view) {
        this.model = model;
        this.view = view;
    }

    public void startGame() {
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
        gameTimer.start();
    }

    public void gameLoop() {
        if (model.getState() == GameState.GAME_OVER) {
            pauseGame();
            view.showGameOver();
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
            }

            model.spawnNewPiece();
            canSoftDrop = false;
        }
        view.refresh();
    }

    public void pauseGame() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }
    // --- CÁC HÀM XỬ LÝ PHÍM BẤM ---

    public void moveLeft() {
        Tetromino current = model.getCurrentPiece();
        // Hỏi Board xem sang trái (x - 1) có đụng tường không?
        if (model.getBoard().isValidMove(current, current.getX() - 1, current.getY())) {
            current.move(-1, 0);
            view.refresh();
        }
    }

    public void moveRight() {
        Tetromino current = model.getCurrentPiece();
        // Hỏi Board xem sang phải (x + 1) có đụng tường không?
        if (model.getBoard().isValidMove(current, current.getX() + 1, current.getY())) {
            current.move(1, 0);
            view.refresh();
        }
    }

    public void moveDown() {
        // Tái sử dụng luôn hàm gameLoop() vì nó đã chứa sẵn logic rơi xuống 1 ô!
        if (canSoftDrop) {
            gameLoop();
        }
    }

    public void resetSoftDrop() {
        canSoftDrop = true;
    }

    public void rotatePiece() {
        Tetromino current = model.getCurrentPiece();
        // Cứ xoay bừa đi đã...
        current.rotate();

        // ...rồi hỏi Board xem xoay xong có bị kẹt vào tường/gạch khác không?
        if (!model.getBoard().isValidMove(current, current.getX(), current.getY())) {
            // BỊ KẸT RỒI! Phải xoay ngược lại.
            // Vì hàm rotate của bạn xoay 90 độ, nên xoay thêm 3 lần nữa (270 độ) sẽ về chỗ cũ!
            current.rotate();
            current.rotate();
            current.rotate();
        }
        view.refresh();
    }

}
