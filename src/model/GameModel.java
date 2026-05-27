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

    /** Bảng chơi chính chứa các khối gạch đã rơi xuống đáy. */
    private Board board;

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
            // Cách tính: 1 hàng = 100, 2 hàng = 300, 3 hàng = 500, 4 hàng = 800
            int[] scoreTable = {0, 100, 300, 500, 800};
            this.score += scoreTable[lineCount];

            // Tăng level mỗi khi đạt 1000 điểm
            this.level = (this.score / 1000) + 1;
        }
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
    }

    /**
     * Xử lý tính năng giữ khối gạch hiện tại (Hold).
     * <p><i>Lưu ý: Hàm này hiện tại đang để trống (placeholder) chờ được implement logic tráo đổi.</i>
     */
    public void holdCurrentPiece() {

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
        return 0;
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
        state = GameState.PLAYING;
        spawnNewPiece();
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