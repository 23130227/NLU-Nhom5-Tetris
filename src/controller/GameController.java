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

    // Timer điều khiển vòng lặp chính của game
    private Timer gameTimer;

    // Model quản lý dữ liệu và trạng thái game
    private GameModel model;

    // View hiển thị giao diện game
    private GameGUI view;

    // Biến kiểm tra cho phép người chơi nhấn soft drop
    private boolean canSoftDrop = true;

    // Constructor khởi tạo Controller
    public GameController(GameModel model, GameGUI view) {
        this.model = model;
        this.view = view;
    }

    /*
     * =========================
     * UC-01: BẮT ĐẦU GAME
     * =========================
     */

    // Khởi động Game Timer và bắt đầu vòng lặp game
    public void startGame() {

        // Tạo timer với delay 500ms
        gameTimer = new Timer(500, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                // Mỗi lần timer chạy sẽ cập nhật game
                gameLoop();
            }
        });

        // Bắt đầu timer
        gameTimer.start();
    }

    // Xử lý vòng lặp chính của game
    public void gameLoop() {

        // Kiểm tra trạng thái game over
        if (model.getState() == GameState.GAME_OVER) {

            // Dừng game
            pauseGame();

            // Hiển thị thông báo game over
            view.showGameOver();

            return;
        }

        // Lấy viên gạch hiện tại
        Tetromino current = model.getCurrentPiece();

        // Lấy bàn chơi
        Board board = model.getBoard();

        // Kiểm tra có thể di chuyển xuống không
        if (board.isValidMove(current, current.getX(), current.getY() + 1)) {

            // Di chuyển xuống 1 ô
            current.move(0, 1);

        } else {

            // Khóa viên gạch vào bàn chơi
            board.lockPiece(current);

            // Kiểm tra các hàng đầy
            java.util.List<Integer> fullLines = board.scanFullLines();

            // Nếu tồn tại hàng đầy
            if (!fullLines.isEmpty()) {

                // Xóa hàng và dồn xuống
                board.clearAndShift(fullLines);
            }

            // Sinh viên gạch mới
            model.spawnNewPiece();

            // Tắt soft drop tạm thời
            canSoftDrop = false;
        }

        // Cập nhật giao diện
        view.refresh();
    }

    /*
     * =========================
     * UC-02: TẠM DỪNG GAME
     * =========================
     */

    // Tạm dừng game bằng cách dừng timer
    public void pauseGame() {

        // Kiểm tra timer đang chạy
        if (gameTimer != null && gameTimer.isRunning()) {

            // Dừng timer
            gameTimer.stop();
        }
    }

    /*
     * =========================
     * UC-01: RESET / START GAME
     * =========================
     */

    // Bắt đầu lại game hoặc reset game
    public void startOrResetGame() {

        // Chỉ reset khi ở MENU hoặc GAME OVER
        if (model.getState() == GameState.GAME_OVER
                || model.getState() == GameState.MENU) {

            // Đặt lại dữ liệu game
            model.reset();

            // Cho phép soft drop
            canSoftDrop = true;

            // Nếu timer tồn tại thì dừng timer cũ
            if (gameTimer != null) {
                gameTimer.stop();
            }

            // Khởi động game mới
            startGame();

            // Làm mới giao diện
            view.refresh();
        }
    }

    /*
     * =========================
     * UC: DI CHUYỂN SANG TRÁI
     * =========================
     */

    public void moveLeft() {

        // Lấy viên gạch hiện tại
        Tetromino current = model.getCurrentPiece();

        // Kiểm tra có thể di chuyển sang trái không
        if (model.getBoard().isValidMove(
                current,
                current.getX() - 1,
                current.getY())) {

            // Di chuyển sang trái
            current.move(-1, 0);

            // Cập nhật giao diện
            view.refresh();
        }
    }

    /*
     * =========================
     * UC: DI CHUYỂN SANG PHẢI
     * =========================
     */

    public void moveRight() {

        // Lấy viên gạch hiện tại
        Tetromino current = model.getCurrentPiece();

        // Kiểm tra có thể di chuyển sang phải không
        if (model.getBoard().isValidMove(
                current,
                current.getX() + 1,
                current.getY())) {

            // Di chuyển sang phải
            current.move(1, 0);

            // Cập nhật giao diện
            view.refresh();
        }
    }

    /*
     * =========================
     * UC: SOFT DROP
     * =========================
     */

    public void moveDown() {

        // Nếu được phép soft drop
        if (canSoftDrop) {

            // Gọi gameLoop để rơi xuống 1 ô
            gameLoop();
        }
    }

    // Reset trạng thái soft drop
    public void resetSoftDrop() {

        canSoftDrop = true;
    }

    /*
     * =========================
     * UC: XOAY KHỐI GẠCH
     * =========================
     */

    public void rotatePiece() {

        // Lấy viên gạch hiện tại
        Tetromino current = model.getCurrentPiece();

        // Xoay viên gạch
        current.rotate();

        // Kiểm tra sau khi xoay có hợp lệ không
        if (!model.getBoard().isValidMove(
                current,
                current.getX(),
                current.getY())) {

            // Nếu không hợp lệ thì xoay ngược lại
            current.rotate();
            current.rotate();
            current.rotate();
        }

        // Cập nhật giao diện
        view.refresh();
    }
}