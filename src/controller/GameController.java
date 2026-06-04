package controller;

import audio.SoundManager;
import model.Board;
import model.GameModel;
import model.GameState;
import model.Tetromino;
import view.GameGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("aaa");
                board.setClearingLines(fullLines);
                view.refresh();
                pauseGame();
                Timer blinkTimer = new Timer(100, null);

                blinkTimer.addActionListener(new ActionListener() {
                    int count = 0;
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        view.refresh(); // repaint liên tục

                        count++;

                        if (count >= 6) { // nhấp nháy 6 lần
                            board.clearAndShift(fullLines);
                            board.setClearingLines(new ArrayList<>());
                            model.updateScore(fullLines.size());
                            model.spawnNewPiece();
                            view.refresh();
                            startGame();
                            blinkTimer.stop();
                        }
                    }
                });
                System.out.println("Timer Started");
                blinkTimer.start();
                if (fullLines.size() == 1) {
                    soundManager.playSFX("src/audio/single.wav");
                } else if (fullLines.size() >= 2) {
                    soundManager.playSFX("src/audio/combo.wav");
                }
                return;
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
        // Chỉ xử lý xoay khi game đang chơi bình thường
        if (current == null || model.getState() != GameState.PLAYING) {
            return;
        }

        // Cứ xoay bừa đi đã...
        current.rotate();

        // Lấy tọa độ gốc trước khi xoay để làm mốc thử nghiệm dịch chuyển
        int originalX = current.getX();
        int originalY = current.getY();

        // Kiểm tra xem vị trí mặc định tại chỗ sau khi xoay có hợp lệ không?
        if (model.getBoard().isValidMove(current, originalX, originalY)) {
            // Vị trí trống trải, xoay thành công ngay tại chỗ, cập nhật UI và kết thúc luôn
            view.refresh();
            return;
        }

        // THUẬT TOÁN WALL KICK (Giải quyết Pain Point kẹt tường/gạch)
        // Định nghĩa các khoảng dịch chuyển thử nghiệm (Mảng Offsets: {Dịch X, Dịch Y})
        int[][] kickOffsets = {
                {-1, 0},  // Thử đẩy khối sang trái 1 ô (Cứu nguy khi kẹt sát tường bên phải)
                {1, 0},   // Thử đẩy khối sang phải 1 ô (Cứu nguy khi kẹt sát tường bên trái)
                {-2, 0},  // Thử đẩy khối sang trái 2 ô (Đặc biệt cần thiết cho khối dài chữ I)
                {2, 0},   // Thử đẩy khối sang phải 2 ô (Cho khối chữ I kẹt tường trái)
                {0, -1},  // Thử nhấc khối lên trên 1 ô (Cứu nguy khi xoay sát đống gạch cũ ở đáy)
                {-1, -1}, // Thử dịch trái 1 ô và nhấc lên 1 ô
                {1, -1}   // Thử dịch phải 1 ô và nhấc lên 1 ô
        };

        boolean kickSuccess = false;

        // Duyệt qua từng phương án dịch biên xem phương án nào thỏa mãn lưới Board trống
        for (int[] offset : kickOffsets) {
            int testX = originalX + offset[0];
            int testY = originalY + offset[1];

            // ...rồi hỏi Board xem xoay xong có bị kẹt vào tường/gạch khác không?
            if (model.getBoard().isValidMove(current, testX, testY)) {
                // Tìm thấy vị trí trống cứu vãn hợp lệ! Áp dụng tọa độ mới cho khối gạch
                current.setX(testX);
                current.setY(testY);
                kickSuccess = true;
                break; // Thoát vòng lặp ngay khi tìm được phương án hợp lệ đầu tiên
            }
        }

        // HOÀN TÁC (Undo): Nếu đã thử hết mọi cách đẩy tường mà vẫn kẹt, bắt buộc phải hủy xoay
        if (!kickSuccess) {
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

    public void hardDrop() {
        Tetromino current = model.getCurrentPiece();
        Board board = model.getBoard();
        while(board.isValidMove(current, current.getX(), current.getY() + 1)) {
            current.move(0, 1);
        }
        board.lockPiece(current);

        List<Integer> fullLines = board.scanFullLines();
        if (!fullLines.isEmpty()) {
            board.setClearingLines(fullLines);
            pauseGame();
            Timer blinkTimer = new Timer(100, null);

            blinkTimer.addActionListener(new ActionListener() {

                int count = 0;

                @Override
                public void actionPerformed(ActionEvent e) {

                    view.refresh();

                    count++;

                    if(count >= 6) {

                        board.clearAndShift(fullLines);
                        board.setClearingLines(new ArrayList<>());

                        model.updateScore(fullLines.size());
                        model.spawnNewPiece();

                        view.refresh();
                        startGame();
                        ((Timer)e.getSource()).stop();
                    }
                }
            });

            blinkTimer.start();
            if (fullLines.size() == 1) {
                soundManager.playSFX("src/audio/single.wav");
            } else if (fullLines.size() >= 2) {
                soundManager.playSFX("src/audio/combo.wav");
            }
            return;
        } else{
            // Nếu không có dòng nào bị xóa, reset combo về -1 (chưa có chuỗi nào)
            model.resetCombo();
        }
        model.spawnNewPiece();
        view.refresh();
    }
}