package controller;

import model.Board;
import model.GameModel;
import model.GameState;
import model.Tetromino;
import view.GameGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * {@code GameController} đóng vai trò là Bộ Điều Khiển (Controller) trong mô hình MVC.
 *
 * <p>Nhiệm vụ chính:
 * <ul>
 * <li>Quản lý vòng lặp chính của game thông qua {@link Timer}.</li>
 * <li>Cập nhật trạng thái dữ liệu bên trong {@link GameModel}.</li>
 * <li>Yêu cầu giao diện {@link GameGUI} vẽ lại (refresh) sau mỗi thay đổi.</li>
 * <li>Xử lý các hành động di chuyển, xoay khối từ người chơi.</li>
 * </ul>
 */
public class GameController {

    /** Bộ đếm thời gian quản lý tốc độ rơi tự động của khối. */
    private Timer gameTimer;

    /** Tham chiếu đến Model để xử lý dữ liệu (bảng, khối hiện tại, điểm số...). */
    private GameModel model;

    /** Tham chiếu đến View để hiển thị đồ họa. */
    private GameGUI view;

    /** * Cờ kiểm soát soft drop (rơi nhanh).
     * Giúp tránh việc khối mới vừa sinh ra đã rơi vèo xuống nếu người chơi giữ rịt phím DOWN.
     */
    private boolean canSoftDrop = true;

    /**
     * Khởi tạo GameController kết nối Model và View.
     *
     * @param model dữ liệu game
     * @param view  giao diện game
     */
    public GameController(GameModel model, GameGUI view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Bắt đầu chạy game.
     * * <p>Tạo một {@link Timer} kích hoạt mỗi 500ms (tốc độ rơi mặc định).
     * Mỗi lần timer tick, nó sẽ gọi hàm {@link #gameLoop()} để xử lý logic rơi.
     */
    public void startGame() {
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
        gameTimer.start();
    }

    /**
     * Vòng lặp chính (nhịp tim) của game, được gọi tự động bởi timer hoặc khi người chơi bấm rơi nhanh.
     * * <p>Quy trình xử lý:
     * <ol>
     * <li>Kiểm tra Game Over. Nếu over thì dừng timer và hiện thông báo.</li>
     * <li>Kiểm tra xem khối có thể rơi xuống 1 ô không.</li>
     * <li>Nếu có: Di chuyển khối xuống.</li>
     * <li>Nếu không (chạm đáy/chạm gạch):
     * <ul>
     * <li>Khóa khối lại trên bảng (lock).</li>
     * <li>Quét và xóa các dòng đã đầy.</li>
     * <li>Sinh khối mới và tạm vô hiệu hóa soft drop.</li>
     * </ul>
     * </li>
     * <li>Cập nhật lại giao diện.</li>
     * </ol>
     */
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

    /**
     * Tạm dừng game bằng cách dừng {@link Timer}.
     */
    public void pauseGame() {
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }
    }

    /**
     * Khởi động game từ Menu hoặc Chơi lại từ đầu khi đã Game Over.
     * * <p>Hàm này sẽ reset toàn bộ dữ liệu (điểm, bảng) về trạng thái ban đầu,
     * reset cờ soft drop, dừng timer cũ (nếu có) và bắt đầu lại.
     */
    public void startOrResetGame() {
        if (model.getState() == GameState.GAME_OVER || model.getState() == GameState.MENU) {
            model.reset();
            canSoftDrop = true;

            if (gameTimer != null) {
                gameTimer.stop();
            }

            startGame();
            view.refresh();
        }
    }

    // --- CÁC HÀM XỬ LÝ PHÍM BẤM ---

    /**
     * Di chuyển khối sang trái.
     * Chỉ thực hiện nếu {@link Board} xác nhận vị trí mới là hợp lệ.
     */
    public void moveLeft() {
        Tetromino current = model.getCurrentPiece();
        // Hỏi Board xem sang trái (x - 1) có đụng tường không?
        if (model.getBoard().isValidMove(current, current.getX() - 1, current.getY())) {
            current.move(-1, 0);
            view.refresh();
        }
    }

    /**
     * Di chuyển khối sang phải.
     * Chỉ thực hiện nếu {@link Board} xác nhận vị trí mới là hợp lệ.
     */
    public void moveRight() {
        Tetromino current = model.getCurrentPiece();
        // Hỏi Board xem sang phải (x + 1) có đụng tường không?
        if (model.getBoard().isValidMove(current, current.getX() + 1, current.getY())) {
            current.move(1, 0);
            view.refresh();
        }
    }

    /**
     * Tăng tốc độ rơi của khối (Soft Drop).
     * * <p>Chỉ hoạt động nếu cờ {@code canSoftDrop} đang bật.
     * Gọi trực tiếp {@link #gameLoop()} để ép khối rơi xuống 1 ô ngay lập tức.
     */
    public void moveDown() {
        // Tái sử dụng luôn hàm gameLoop() vì nó đã chứa sẵn logic rơi xuống 1 ô!
        if (canSoftDrop) {
            gameLoop();
        }
    }

    /**
     * Đặt lại trạng thái cho phép rơi nhanh.
     * Thường được gọi khi người chơi nhả phím DOWN.
     */
    public void resetSoftDrop() {
        canSoftDrop = true;
    }

    /**
     * Xoay khối hiện tại góc 90 độ.
     * * <p>Sử dụng cơ chế "Xoay thử - Kiểm tra - Hoàn tác":
     * Thực hiện xoay trước, nếu vị trí mới không hợp lệ (kẹt tường/gạch)
     * thì xoay thêm 3 lần nữa (270 độ) để trả khối về hình dáng ban đầu.
     */
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