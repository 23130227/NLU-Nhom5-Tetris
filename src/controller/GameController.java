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
    private boolean isClearingAnimationActive = false;

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
        if (gameTimer == null) {
            gameTimer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    gameLoop();
                }
            });
        }
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
        if (isClearingAnimationActive) return;

        if (model.getState() == GameState.GAME_OVER) {
            if (gameTimer != null && gameTimer.isRunning()) {
                gameTimer.stop();
            }
            view.showGameOver();

            model.setGameState(GameState.MENU);
            startOrResetGame();
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
                isClearingAnimationActive = true;
                board.setClearingLines(fullLines);
                view.refresh();

                if (gameTimer != null) {
                    gameTimer.stop();
                }

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
                            model.updateScore(fullLines.size()); // Điểm số và Level được cập nhật tại đây

                            // ===================================================================
                            // TỰ ĐỘNG TĂNG TỐC ĐỘ RƠI THEO LEVEL
                            // Công thức: Cứ tăng 1 Level thì giảm 50ms chờ (Gạch rơi nhanh hơn).
                            // Giới hạn tối thiểu (Math.max) là 100ms để tránh gạch rơi quá nhanh không kịp nhìn.
                            // ===================================================================
                            int newDelay = Math.max(100, 1000 - (model.getLevel() - 1) * 50);
                            if (gameTimer != null) {
                                gameTimer.setDelay(newDelay);
                            }

                            model.spawnNewPiece();
                            view.refresh();

                            model.setGameState(GameState.PLAYING);
                            isClearingAnimationActive = false;
                            if (gameTimer != null) {
                                gameTimer.start();
                            }

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
        if (isClearingAnimationActive) return;
        if (model.getState() == GameState.PLAYING) {
            // 2.1.2 / 2.4.3. stop() -> Dừng đếm thời gian của Game Timer
            if (gameTimer != null && gameTimer.isRunning()) {
                gameTimer.stop();
            }
            // 2.1.2 / 2.4.3. setGameState(GameState.PAUSED) -> Chuyển trạng thái sang Paused
            model.setGameState(GameState.PAUSED);
            // 2.1.4 / 2.4.3. showPauseMenu() -> Hiển thị lớp phủ Menu Tạm dừng
            view.showPauseMenu();
        }
    }

    /**
     * [UC-02 - Luồng 2.4]: Xử lý khi cửa sổ Game mất tiêu điểm (Lose Focus)
     */
    public void windowLostFocus() {
        // 2.4.2. windowLostFocus() kích hoạt -> Tự động gọi sang logic tạm dừng hệ thống
        if (model.getState() == GameState.PLAYING) {
            pauseGame();
        }
    }

    /**
     * [UC-02 - Luồng 2.1.5]: Người chơi chọn Tiếp tục chơi (Resume)
     */
    public void resumeGame() {
        if (model.getState() == GameState.PAUSED) {
            // 2.1.6. hidePauseMenu() -> Gỡ bỏ lớp phủ Menu Tạm dừng trên giao diện
            view.hidePauseMenu();
            // 2.1.7. start() -> Kích hoạt lại vòng lặp Game Timer chạy tiếp tục
            if (gameTimer != null) {
                gameTimer.start();
            }
            // 2.1.8. setGameState(GameState.PLAYING) -> Đưa trạng thái về PLAYING
            model.setGameState(GameState.PLAYING);
            // refresh() -> Vẽ lại màn hình game chính
            view.refresh();
        }
    }

    /**
     * [UC-02 - Luồng 2.2]: Người chơi chọn nút lệnh "Chơi lại" (Restart) từ Pause Menu
     */
    public void restartGame() {
        if (model.getState() == GameState.PAUSED) {
            // 2.2.2. hidePauseMenu() -> Ẩn thực thể giao diện menu tạm dừng
            view.hidePauseMenu();

            // 2.2.3. Kết thúc trạng thái PAUSED [TỐI ƯU: Gọi trực tiếp hàm xử lý của UC-01]
            startOrResetGame();
        }
    }

    /**
     * [UC-02 / UC-03 - Luồng thay thế 3.2]: Chọn "Thoát" về Menu từ Menu Tạm dừng (PAUSED)
     */
    public void exitToMainMenu() {
        if (model.getState() == GameState.PAUSED) {
            // 3.2.1. hidePauseMenu() -> Đóng lớp phủ Menu Tạm dừng trên giao diện UI
            view.hidePauseMenu();

            // 3.2.2. clearCurrentGameSession() -> Gọi Model hủy tiến trình ván đấu cũ
            model.clearCurrentGameSession();
            view.updateLevelUI(1); // Đồng bộ giao diện thông tin cấp độ

            // 3.2.3. setGameState(GameState.MENU) -> Đưa trạng thái hệ thống về MENU
            model.setGameState(GameState.MENU);

            // 3.2.3. showMainMenu() -> Hiển thị lại giao diện Menu chờ chính
            view.showMainMenu();
        }
    }

    /**
     * [UC-03 - Luồng thay thế 3.3]: Người chơi bấm nút đóng ứng dụng khi game đang chạy (PLAYING)
     */
    public void forceCloseRequest() {
        // 3.3.2. stop() -> Tạm dừng vòng lặp đếm nhịp rơi tự động của gạch
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        // 3.3.2. showConfirmationDialog() -> Hiển thị hộp thoại xác nhận thoát ván
        boolean userChoice = view.showConfirmationDialog("Bạn có chắc chắn muốn thoát và đóng trò chơi không? Tiến trình ván đấu sẽ bị mất.");

        // ===== BẮT ĐẦU KHUNG ĐIỀU KIỂM TRA RẼ NHÁNH XÁC NHẬN THOÁT (ALT) =====
        if (!userChoice) {
            // [[3.3.3. Người chơi chọn "Không" (Cancel)]] -> Kích hoạt luồng hủy lệnh thoát
            cancelExit();
        } else {
            // [[3.3.4. Người chơi chọn "Có" (OK)]] -> Kích hoạt luồng xác nhận thoát hoàn toàn
            confirmExit();
        }
    }

    /**
     * [UC-03 - Bước 3.3.3]: Hủy lệnh đóng cửa sổ, khôi phục lại Game Timer và quay lại ván chơi hiện tại
     */
    public void cancelExit() {
        if (model.getState() == GameState.PLAYING) {
            if (gameTimer != null) {
                gameTimer.start();
            }
        }
        view.refresh(); // Trở lại trạng thái PLAYING ban đầu
    }

    /**
     * [UC-03 - Bước 3.3.4]: Xác nhận lệnh đóng, chuyển hướng dòng điều khiển xuống luồng shutdown hệ thống
     */
    public void confirmExit() {
        exitApplication();
    }

    /**
     * [UC-03 - Luồng cơ bản 3.1 & Luồng 3.3.4]: Thực thi dọn dẹp hệ thống và đóng hoàn toàn ứng dụng
     */
    public void exitApplication() {
        // 3.1.2. stop() -> Đảm bảo chắc chắn Game Timer đã dừng hẳn ngầm trong bộ nhớ
        if (gameTimer != null) {
            gameTimer.stop();
        }

        // 3.1.3. checkAndSaveHighScore() -> Gọi phương thức kiểm tra và lưu kỷ lục điểm cao của Model
        model.checkAndSaveHighScore();

        // [3.1.4. Giải phóng tài nguyên hệ thống (Bộ nhớ, hình ảnh, âm thanh)]

        // 3.1.5. dispose() -> Đóng cửa sổ giao diện chính đồ họa của ứng dụng
        if (view.getMainFrame() != null) {
            view.getMainFrame().dispose();
        }

        // 3.1.6. System.exit(0) -> Kết thúc hoàn toàn tiến trình ứng dụng
        System.exit(0);
    }

    public void startOrResetGame() {

        // ===== BẮT ĐẦU KHUNG KIỂM TRA RẼ NHÁNH (ALT) =====

        // [[1.3.1. model.state == GameState.PLAYING]]
        if (model.getState() == GameState.PLAYING) {
            // 1.3.2. Bỏ qua thao tác (Không làm gì cả)
            // 1.3.3. Duy trì trạng thái PLAYING hiện tại
            return;
        }
        // [[1.1.0. model.state != GameState.PLAYING]] (MENU hoặc GAME_OVER hoặc PAUSED chuyển sang)
        else {
            isClearingAnimationActive = false;
            // 1.1.2. resetScore() -> Yêu cầu Model đặt lại điểm số về 0
            model.resetScore();

            // 1.1.3. setLevel(1) -> Đặt cấp độ về 1 (Phía trong Model sẽ tự chạy bước 1.1.4: board.reset())
            model.setLevel(1);

            // updateLevelUI(1) -> Báo View hiển thị số "1" lên vùng thông tin Level trên UI
            view.updateLevelUI(1);

            // 1.1.5. spawnNewPiece() -> Sinh ngẫu nhiên khối gạch hiện tại và khối tiếp theo
            model.spawnNewPiece();

            // startGame() -> Khởi tạo cấu trúc Timer (Hàm nội bộ theo Class Diagram)
            startGame();

            if (gameTimer != null) {
                // 1.1.6. setDelayForLevel1() -> Thiết lập nhịp delay mặc định của Level 1 (500ms)
                gameTimer.setDelay(1000);
                // start() -> Kích hoạt Timer chạy vòng lặp rơi tự động
                gameTimer.start();
            }

            // 1.1.7. Cập nhật trạng thái hệ thống: state = GameState.PLAYING
            model.setGameState(GameState.PLAYING);

            // refresh() -> Yêu cầu giao diện vẽ lại toàn bộ khung cảnh trò chơi mới
            view.refresh();
        }
    }

    // --- CÁC HÀM XỬ LÝ PHÍM BẤM BẢO VỆ TIẾN TRÌNH THEO BƯỚC 2.1.3 ---

    /**
     * Di chuyển khối sang trái.
     * Chỉ thực hiện nếu {@link Board} xác nhận vị trí mới là hợp lệ.
     */
    public void moveLeft() {
        // 2.1.3. Vô hiệu hóa phím di chuyển nếu trạng thái game đang bị tạm dừng hoặc ở menu
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
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
        // 2.1.3. Vô hiệu hóa phím di chuyển nếu trạng thái game đang bị tạm dừng hoặc ở menu
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
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
        // 2.1.3. Vô hiệu hóa phím di chuyển nếu trạng thái game đang bị tạm dừng hoặc ở menu
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
        if (canSoftDrop) {
            gameLoop();
        }
    }

    /** Xử lý tính năng Hold: Đổi khối hiện tại với khối đang giữ trong ô Hold.
     * * <p>Chỉ cho phép đổi nếu game đang ở trạng thái PLAYING và cờ Hold chưa bị khóa.
     * Sau khi đổi thành công, yêu cầu giao diện vẽ lại ngay lập tức.
     */
    public void handleHoldPiece() {
        // 2.1.3. Vô hiệu hóa phím di chuyển nếu trạng thái game đang bị tạm dừng hoặc ở menu
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
        model.holdCurrentPiece();
        view.refresh();
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
        // 2.1.3. Vô hiệu hóa phím di chuyển nếu trạng thái game đang bị tạm dừng hoặc ở menu
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
        Tetromino current = model.getCurrentPiece();
        // Chỉ xử lý xoay khi game đang chơi bình thường
        if (current == null) return;

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

    public GameModel getModel() {
        return this.model;
    }
    public void hardDrop() {
        if (isClearingAnimationActive || model.getState() != GameState.PLAYING) return;
        Tetromino current = model.getCurrentPiece();
        Board board = model.getBoard();
        while(board.isValidMove(current, current.getX(), current.getY() + 1)) {
            current.move(0, 1);
        }
        board.lockPiece(current);

        List<Integer> fullLines = board.scanFullLines();
        if (!fullLines.isEmpty()) {
            isClearingAnimationActive = true;
            board.setClearingLines(fullLines);
            if (gameTimer != null) {
                gameTimer.stop();
            }

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
                        model.updateScore(fullLines.size()); // Điểm số và Level được cập nhật tại đây

                        // ===================================================================
                        // TỰ ĐỘNG TĂNG TỐC ĐỘ RƠI THEO LEVEL (ĐỒNG BỘ HARD DROP)
                        // ===================================================================
                        int newDelay = Math.max(100, 1000 - (model.getLevel() - 1) * 50);
                        if (gameTimer != null) {
                            gameTimer.setDelay(newDelay);
                        }

                        model.spawnNewPiece();

                        model.setGameState(GameState.PLAYING);
                        isClearingAnimationActive = false;
                        if (gameTimer != null) {
                            gameTimer.start();
                        }

                        view.refresh();
                        blinkTimer.stop();
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