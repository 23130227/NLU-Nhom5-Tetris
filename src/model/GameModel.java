package model;

import java.util.Random;

/**
 * {@code GameModel} đóng vai trò là Lớp Dữ liệu (Model) trong mô hình MVC.
 *
 * <p>Nhiệm vụ chính:
 * <ul>
 * <li>Lưu trữ toàn bộ trạng thái hiện tại của trò chơi (điểm, cấp độ, trạng thái menu/playing).</li>
 * <li>Quản lý bảng chơi {@link Board} và các khối gạch (khối đang rơi, khối tiếp theo, khối đang giữ).</li>
 * <li>Cung cấp dữ liệu cho View hiển thị và nhận lệnh cập nhật từ Controller.</li>
 * </ul>
 */
public class GameModel {

    /** Điểm số hiện tại của người chơi. */
    private int score;

    /** Cấp độ độ khó hiện tại (level càng cao khối rơi càng nhanh). */
    private int level;

    /** Trạng thái hiện tại của game (Menu, Đang chơi, Thua...). */
    private GameState state;

    /** Khối gạch đang rơi xuống (hiện tại). */
    private Tetromino currentPiece;

    /** Khối gạch tiếp theo sẽ xuất hiện (dùng để hiển thị phần Next Piece). */
    private Tetromino nextPiece;

    /** Khối gạch đang được cất giữ (tính năng Hold). */
    private Tetromino holdPiece;

    /** Kiểm soát: Mỗi lượt rơi chỉ được đổi gạch (Hold) đúng 1 lần */
    private boolean canHold = true;

    /** Bảng chơi chính chứa các khối gạch đã rơi xuống đáy. */
    private Board board;
    /** Bộ đếm combo hiện tại. -1 nghĩa là chưa có chuỗi nào. */
    private int comboCount = -1;

    /** Lưu điểm top10 ngưởi chơi và lưu vào file
     *
     */
     private static final String HIGHSCORE_FILE = "highscore.txt";
     private static final int MAX_TOP_PLAYERS = 10;


     /**
     * Khởi tạo GameModel mới.
     * Mặc định khi mới tạo ra, game sẽ ở trạng thái MENU và khởi tạo một bảng chơi trống.
     */
    public GameModel() {
        this.board = new Board();
        this.state = GameState.MENU;
    }

    /**
     * [UC-01 - Bước 1.1.2] Đặt lại điểm số và combo về trạng thái ban đầu
     */
    public void resetScore() {
        this.score = 0;
        this.comboCount = -1;
    }

    /**
     * [UC-01 - Bước 1.1.3] Thiết lập cấp độ chơi về Level 1
     */
    public void setLevel(int level) {
        this.level = level;

        // [UC-01 - Bước 1.1.4] Model tự động gửi thông điệp dọn sạch ma trận lưới sang Board
        this.board.reset();

        // Dọn dẹp bổ sung tài nguyên gạch cũ
        this.nextPiece = null;
        this.holdPiece = null;
        this.canHold = true;
    }

    /**
     * [UC-01 - Bước 1.1.7] Cập nhật trạng thái hoạt động toàn cục của Game
     */
    public void setGameState(GameState state) {
        this.state = state;
    }

    public void updateScore(int lineCount) {
        // [UC-05 - Bước 5.1.8] Hệ thống tính toán số điểm cơ bản được cộng thêm
        if (lineCount > 0) {
            int[] scoreTable = {0, 100, 300, 500, 800};
            int baseScore = scoreTable[lineCount];
            int comboBonus = 0;

            // [UC-05 - Bước 5.1.5] Hệ thống tiến hành kiểm tra số lượng hàng vừa xóa để cập nhật trạng thái chuỗi Combo
            if (lineCount >= 2) {
                // [UC-05 - Bước 5.1.6]Tăng biến đếm chuỗi Combo thêm 1 đơn vị
                this.comboCount++;

                // [UC-05 - Bước 5.1.9]Hệ thống tính toán số điểm thưởng Combo gia tăng
                int currentLevel = Math.max(1, this.level);
                comboBonus = 50 * this.comboCount * currentLevel;
            } else {
                // [UC-05 - Bước 5.1.7]Nhận diện chuỗi ăn điểm bị đứt và tự động đặt biến đếm Combo về lại giá trị 0
                resetCombo();
            }

            // [UC-05 - Bước 5.1.10] Hệ thống cộng dồn tổng số điểm mới vào tổng điểm hiện tại (Score)
            this.score += (baseScore + comboBonus);

            // [UC-05 - Bước 5.2.1, 5.2.2] Kiểm tra đạt ngưỡng thăng cấp và tăng cấp độ (Level) lên 1
            this.level = (this.score / 1000) + 1;
        }
    }
    /**
     * Đặt lại bộ đếm combo về ban đầu.
     * Được gọi khi người chơi thả một khối mà không ăn được hàng nào.
     */
    public void resetCombo() {
        this.comboCount = 0;
    }

    /**
     * Lấy số combo hiện tại (phục vụ cho việc hiển thị lên SidePanel sau này).
     */
    public int getComboCount() {
        return this.comboCount;
    }

    /**
     * Sinh ra một khối gạch ngẫu nhiên mới và đặt nó làm khối hiện tại đang rơi.
     *
     * <p>Xử lý Game Over: Ngay khi khối vừa sinh ra ở vị trí xuất phát,
     * nếu nó va chạm ngay lập tức (không phải là vị trí hợp lệ), nghĩa là bảng đã đầy
     * tới nóc -> Chuyển trạng thái sang GAME_OVER.
     */
    public void spawnNewPiece() {
        Random rand = new Random();

        // [UC-06 - Bước 6.2.2]Hệ thống sinh ngẫu nhiên khối gạch dự phòng khi vừa bắt đầu game
        if (this.nextPiece == null) {
            this.nextPiece = new Tetromino(rand.nextInt(7));
        }

        // [UC-06 - Bước 6.1.1]Hệ thống đẩy khối gạch dự phòng ra làm khối gạch hiện tại
        this.currentPiece = this.nextPiece;

        // [UC-06 - Bước 6.1.2]Kích hoạt hàm tạo ngẫu nhiên khối Tetromino mới
        int randomId = rand.nextInt(7);
        // [UC-06 - 6.1.3] Khối gạch mới được lưu trữ vào biến dữ liệu dự phòng

        this.nextPiece = new Tetromino(randomId);

        if (!board.isValidMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            setGameOver();
        }
        this.canHold = true;
    }

    /**
     * Xử lý tính năng giữ khối gạch hiện tại (Hold).
     * <p><i>Lưu ý: Hàm này hiện tại đang để trống (placeholder) chờ được implement logic tráo đổi.</i>
     */
    public void holdCurrentPiece() {
        /** Nếu lượt này đã đổi gạch rồi thì không cho phép đổi nữa */
        if (!canHold) {
            return;
        }

        if (holdPiece == null) {
            /** Trường hợp 1: Ô Hold đang trống */
            holdPiece = currentPiece;
            /** Sinh luôn khối gạch tiếp theo để người chơi đá tiếp */
            spawnNewPiece();
        } else {
            /** Trường hợp 2: Đã có gạch trong ô Hold, tiến hành hoán đổi (Swap) */
            Tetromino temp = currentPiece;
            currentPiece = holdPiece;
            holdPiece = temp;

            /** Đặt lại tọa độ xuất phát cho khối gạch vừa lấy từ ô Hold ra ở đỉnh bàn cờ */
            /** Thường là ở giữa chiều rộng của Board (ví dụ: x = 3 hoặc 4, y = 0) */
            currentPiece.setX(4);
            currentPiece.setY(0);
        }

        /** Khóa tính năng Hold lại, chỉ mở ra khi khối gạch này được hạ cánh và sinh khối mới */
        canHold = false;
    }

    /** Hàm getter để sau này lớp View (SidePanel) lấy khối gạch ra vẽ lên UI */
    public Tetromino getHeldPiece() {
        return holdPiece;
    }

    /**
     * Lấy khối gạch đang rơi hiện tại.
     *
     * @return khối gạch {@link Tetromino} đang rơi
     */
    public Tetromino getCurrentPiece() {
        return this.currentPiece;
    }

    /**
     *
     */
    public Tetromino getNextPiece() {
        return this.nextPiece;
    }

    /**
     *
     */
    public Tetromino getHoldPiece() {
        return this.holdPiece;
    }
    /**
     * Lấy điểm số hiện tại của người chơi.
     * <p><i>Lưu ý: Hiện tại đang hardcode trả về 0, cần cập nhật trả về biến score thực tế.</i>
     *
     * @return điểm số
     */
    public int getScore() {
        return this.score;
    }

    /**
     * Lấy cấp độ (level) hiện tại của người chơi.
     * <p><i>Lưu ý: Hiện tại đang hardcode trả về 0, cần cập nhật trả về biến level thực tế.</i>
     *
     * @return cấp độ
     */
    public int getLevel() {
        return this.level;
    }

    /**
     * Lấy trạng thái hiện tại của game.
     *
     * @return trạng thái {@link GameState}
     */
    public GameState getState() {
        return this.state;
    }

    /**
     * Lấy tham chiếu đến bảng chơi.
     *
     * @return đối tượng {@link Board} quản lý lưới gạch
     */
    public Board getBoard() {
        return this.board;
    }

    /**
     * Chuyển trạng thái của game sang GAME_OVER (Kết thúc trò chơi).
     */
    public void setGameOver(){
        this.state = GameState.GAME_OVER;
    }
    /**
     * Hàm ghi điểm cao nhất cảu người chơi vào file.
     */
    public void saveCurrentScoreToFile() {
    }
    public void saveHighscore(String over, int finalScore) {
        if (over == null || over.trim().isEmpty()) {
            over = "Player";
        }

        java.util.List<String> records = loadScoresFromFile();
        records.add(over.trim() + ":" + finalScore);

        // Sắp xếp danh sách giảm dần theo điểm số
        java.util.Collections.sort(records, new java.util.Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int score1 = Integer.parseInt(o1.split(":")[1]);
                int score2 = Integer.parseInt(o2.split(":")[1]);
                return Integer.compare(score2, score1);
            }
        });

        // Cắt bớt nếu vượt quá số lượng tối đa của nhóm (Top 10)
        if (records.size() > 10) {
            records = records.subList(0, 10);
        }

        // Ghi ngược dữ liệu xuống file txt
        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(new java.io.FileWriter("highscore.txt"))) {
            for (String record : records) {
                writer.write(record + "\n");
            }
        } catch (java.io.IOException e) {
            System.err.println("Lỗi khi ghi file Highscore: " + e.getMessage());
        }
    }

    // Hàm đọc danh sách điểm từ file txt lên hệ thống
    public java.util.List<String> loadScoresFromFile() {
        java.util.List<String> records = new java.util.ArrayList<>();
        java.io.File file = new java.io.File("highscore.txt");

        if (!file.exists()) {
            return records;
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty() && line.contains(":")) {
                    records.add(line.trim());
                }
            }
        } catch (java.io.IOException e) {
            System.err.println("Lỗi khi đọc file Highscore: " + e.getMessage());
        }
        return records;
    }
    // NÂNG CẤP TÍNH NĂNG BÓNG GẠCH
    /**
     * Thuật toán tìm tọa độ Y thấp nhất mà khối gạch hiện tại có thể rơi xuống (vị trí của bóng gạch).
     * Hàm này duyệt từ vị trí Y hiện tại, tăng dần cho tới khi va chạm.
     * * @return tọa độ Y sâu nhất hợp lệ dưới đáy bàn cờ
     */
    public int getGhostY() {
        if (currentPiece == null) {
            return 0;
        }

        // Bắt đầu từ tọa độ Y hiện tại của khối gạch đang rơi
        int ghostY = currentPiece.getY();

        // Vòng lặp thử đi xuống: Nếu ô tiếp theo (ghostY + 1) vẫn trống và hợp lệ thì đi xuống tiếp
        while (board.isValidMove(currentPiece, currentPiece.getX(), ghostY + 1)) {
            ghostY++;
        }

        // Trả về tọa độ Y sâu nhất tìm được để lớp View sử dụng để vẽ bóng
        return ghostY;
    }
    /**
     * Hàm main dùng để test (kiểm thử) hoạt động của GameModel.
     * Chạy độc lập không cần giao diện.
     *
     * @param args tham số dòng lệnh
     */
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
    }

    /**
     * [UC-03 - Bước 3.2.2]: Hủy tiến trình ván đấu cũ, dọn sạch lưới và đưa điểm/level về mặc định
     */
    public void clearCurrentGameSession() {
        this.resetScore();
        this.setLevel(1);
    }

    /**
     * [UC-03 - Bước 3.1.3]: Ghi nhận, kiểm tra dữ liệu kỷ lục và lưu Highscore xuống tệp txt
     */
    public void checkAndSaveHighScore() {
        // Tái sử dụng hàm saveHighscore sẵn có của bạn với tên mặc định là "Player"
        this.saveHighscore("Player", this.score);
    }
}