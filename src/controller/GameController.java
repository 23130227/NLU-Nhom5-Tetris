package controller;

import audio.SoundManager;
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

    /** */
    private SoundManager soundManager;
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
        this.soundManager = new SoundManager();
    }

    /**
     * Bắt đầu chạy game.
     * * <p>Tạo một {@link Timer} kích hoạt mỗi 500ms (tốc độ rơi mặc định).
     * Mỗi lần timer tick, nó sẽ gọi hàm {@link #gameLoop()} để xử lý logic rơi.
     */
    public void startGame() {
        // Khởi tạo nhịp đập mặc định ban đầu
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
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
                model.updateScore(fullLines.size());
                if (fullLines.size() >= 2) {
                    soundManager.playSFX("src/audio/combo.wav");
                }

            } else{
                // Nếu không có dòng nào bị xóa, reset combo về mặc định
                model.resetCombo();
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
     * [UC-01] Hàm điều phối chính kích hoạt từ nút "Bắt đầu" (1.1.1) hoặc nút "Chơi lại" (1.2.1)
     */
    public void startOrResetGame() {

        // ===== BẮT ĐẦU KHUNG KIỂM TRA RẼ NHÁNH (ALT) =====

        // [[1.3.1. model.state == GameState.PLAYING]]
        if (model.getState() == GameState.PLAYING) {
            // 1.3.2. Bỏ qua thao tác (Không làm gì cả)
            // 1.3.3. Duy trì trạng thái PLAYING hiện tại
            return;
        }

        // [[1.1.0. model.state != GameState.PLAYING]] (MENU hoặc GAME_OVER)
        else {
            // 1.1.2. resetScore() -> Yêu cầu Model đặt lại điểm số về 0
            model.resetScore();

            // 1.1.3. setLevel(1) -> Yêu cầu Model đặt cấp độ về 1 (Đồng thời kích hoạt 1.1.4 phía trong)
            model.setLevel(1);

            // updateLevelUI(1) -> Controller chủ động bảo View cập nhật số "1" lên màn hình UI
            view.updateLevelUI(1);

            // 1.1.5. spawnNewPiece() -> Sinh ngẫu nhiên khối gạch hiện tại và khối tiếp theo
            model.spawnNewPiece();

            // startGame() -> Gọi hàm nội bộ để chuẩn bị luồng gạch rơi theo Class Diagram
            startGame();

            // 1.1.6. setDelayForLevel1() -> Thiết lập nhịp delay mặc định của Level 1 (500ms)
            if (gameTimer != null) {
                gameTimer.setDelay(500);
                // start() -> Khởi động Timer bắt đầu vòng lặp rơi tự động
                gameTimer.start();
            }

            // 1.1.7. Cập nhật trạng thái hệ thống: state = GameState.PLAYING
            model.setGameState(GameState.PLAYING);

            // refresh() -> Cập nhật/vẽ lại toàn bộ giao diện đồ họa trò chơi
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

    /** Xử lý tính năng Hold: Đổi khối hiện tại với khối đang giữ trong ô Hold.
     * * <p>Chỉ cho phép đổi nếu game đang ở trạng thái PLAYING và cờ Hold chưa bị khóa.
     * Sau khi đổi thành công, yêu cầu giao diện vẽ lại ngay lập tức.
     */
    public void handleHoldPiece() {
        // Chỉ cho phép đổi gạch khi game đang ở trạng thái chơi (PLAYING)
        if (model.getState() == GameState.PLAYING) {
            model.holdCurrentPiece();

            // Sau khi đổi gạch thành công, yêu cầu giao diện vẽ lại ngay lập tức
            view.refresh();
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
    /**
     * Bật hoặc tắt nhạc nền trong game.
     *
     * <p>Phương thức hoạt động theo cơ chế Toggle:
     * <ul>
     *     <li>Nếu nhạc chưa phát → bắt đầu phát.</li>
     *     <li>Nếu nhạc đang phát → dừng phát.</li>
     * </ul>
     *
     * <p>Hàm này thường được gọi khi người chơi nhấn phím M.
     */
    public void toggleMusic() {
        if (!soundManager.isMusicPlaying()) {
            soundManager.play("src/audio/Tetris.wav");
        } else {
            soundManager.stopMusic();
        }
    }

}