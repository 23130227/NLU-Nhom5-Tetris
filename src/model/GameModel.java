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
     * Cập nhật điểm số dựa trên số dòng vừa ăn được.
     * <p><i>Lưu ý: Hàm này hiện tại đang để trống (placeholder) chờ được implement.</i>
     *
     * @param lineCount số lượng dòng vừa bị xóa đi cùng lúc (thường là 1-4)
     */
    public void updateScore(int lineCount) {
        if (lineCount > 0) {
            // 1. Tăng bộ đếm combo
            comboCount++;

            // 2. Điểm cơ bản (1 hàng = 100, 2 hàng = 300, 3 hàng = 500, 4 hàng = 800)
            int[] scoreTable = {0, 100, 300, 500, 800};
            int baseScore = scoreTable[lineCount];

            // 3. Điểm thưởng Combo
            // Công thức: 50 * số combo * cấp độ (level) hiện tại
            int comboBonus = 0;
            if (comboCount > 0) {
                // Sử dụng this.level mặc định bằng 1 nếu chưa tăng cấp, hoặc getLevel()
                int currentLevel = Math.max(1, this.level);
                comboBonus = 50 * comboCount * currentLevel;
                System.out.println("Combo x" + comboCount + "! Thưởng: " + comboBonus); // In ra console để test
            }

            // 4. Cộng tổng điểm
            this.score += (baseScore + comboBonus);

            // 5. Tăng level mỗi khi đạt 1000 điểm
            this.level = (this.score / 1000) + 1;
        }
    }
    /**
     * Đặt lại bộ đếm combo về ban đầu.
     * Được gọi khi người chơi thả một khối mà không ăn được hàng nào.
     */
    public void resetCombo() {
        this.comboCount = -1;
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
        // randomId từ 0 đến 6 tương ứng với 7 loại khối Tetromino (I, J, L, O, S, T, Z)
        int randomId = rand.nextInt(7);
        this.currentPiece = new Tetromino(randomId);

        // Kiểm tra xem vị trí sinh ra có bị đụng gạch cũ không
        if (!board.isValidMove(currentPiece, currentPiece.getX(), currentPiece.getY())) {
            setGameOver();
        }
        // Khi một khối gạch mới hoàn toàn xuất hiện, mở lại quyền sử dụng tính năng Hold
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
     * Đặt lại toàn bộ dữ liệu game về trạng thái ban đầu để bắt đầu một ván mới.
     * Xóa bảng, reset điểm/level, đổi trạng thái sang PLAYING và sinh khối gạch đầu tiên.
     */
    public void reset() {
        board.reset();
        score = 0;
        level = 1;
        comboCount = -1; // Thêm dòng này để reset combo khi chơi lại
        state = GameState.PLAYING;
        spawnNewPiece();
    }
    /**
     * Hàm ghi điểm cao nhất cảu người chơi vào file.
     */
    public void saveCurrentScoreToFile() {
        java.util.List<Integer> scores = loadScoresFromFile();
        scores.add(this.score); // Lấy trực tiếp biến score có sẵn của GameModel

        // Sắp xếp giảm dần (Điểm cao đứng trước)
        java.util.Collections.sort(scores, java.util.Collections.reverseOrder());

        // Cắt bớt nếu vượt quá top 10 người chơi
        if (scores.size() > MAX_TOP_PLAYERS) {
            scores = scores.subList(0, MAX_TOP_PLAYERS);
        }


    }

    // Hàm đọc danh sách điểm từ file txt lên hệ thống
    public java.util.List<Integer> loadScoresFromFile() {
        java.util.List<Integer> scores = new java.util.ArrayList<>();
        java.io.File file = new java.io.File(HIGHSCORE_FILE);

        if (!file.exists()) {
            return scores;
        }

        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    scores.add(Integer.parseInt(line.trim()));
                }
            }
        } catch (java.io.IOException | NumberFormatException e) {
            System.err.println("Lỗi khi đọc file Highscore: " + e.getMessage());
        }
        return scores;
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

}