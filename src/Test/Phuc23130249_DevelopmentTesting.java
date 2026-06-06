package Test;

import model.GameModel;
import model.Tetromino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Phuc23130249_DevelopmentTesting {

    private GameModel model;

    @BeforeEach
    public void setUp() {
        // [Thiết lập môi trường] Khởi tạo game ảo sạch sẽ trước mỗi ca kiểm thử
        model = new GameModel();
        model.setLevel(1);
        model.resetScore();
    }

    // ==========================================
    // PHẦN 1: TEST CHO UC-05 (TÍNH ĐIỂM & COMBO)
    // ==========================================

    @Test
    public void testUC05_ScoreAndComboActivation() {
        // [UC-05 - Luồng cơ bản] Lần đầu ăn 2 hàng: Điểm cơ bản 300, Combo tăng từ -1 lên 0.
        model.updateScore(2);

        assertEquals(300, model.getScore(), "Điểm tổng phải là 300 cho lần ăn 2 hàng đầu tiên");
        assertEquals(0, model.getComboCount(), "Biến đếm Combo phải tăng lên mốc 0");
    }

    @Test
    public void testUC05_ScoreAndComboChain() {
        // [UC-05 - Luồng cơ bản] Lần 1 ăn 2 hàng: Score = 300, Combo = 0
        model.updateScore(2);

        // [UC-05 - Luồng cơ bản] Lần 2 tiếp tục ăn 2 hàng: Điểm cơ bản = 300, Combo tăng lên 1, Thưởng = 50.
        model.updateScore(2);

        assertEquals(650, model.getScore(), "Tổng điểm khi duy trì chuỗi combo 2 lần chưa chính xác");
        assertEquals(1, model.getComboCount(), "Biến đếm Combo phải tăng lên mốc 1 khi duy trì chuỗi");
    }

    @Test
    public void testUC05_ComboResetOnSingleLine() {
        // Kích hoạt chuỗi liên hoàn
        model.updateScore(2);
        model.updateScore(2);

        // [UC-05 - Luồng thay thế] Ăn 1 hàng đơn lẻ để ngắt chuỗi Combo
        model.updateScore(1);

        // Khi ăn 1 hàng, hệ thống phải đưa comboCount về 0
        assertEquals(0, model.getComboCount(), "Chuỗi Combo chưa bị reset về 0 khi ăn 1 hàng đơn lẻ");
    }

    @Test
    public void testUC05_LevelUpThreshold() {
        // [UC-05 - Luồng cơ bản] Tích lũy điểm chưa vượt ngưỡng 1000
        model.updateScore(4);
        assertEquals(1, model.getLevel(), "Level phải giữ nguyên là 1 khi điểm < 1000");

        // [UC-05 - Luồng thay thế] Vượt ngưỡng 1000 điểm -> Hệ thống tự động tăng Level
        model.updateScore(2);
        assertEquals(2, model.getLevel(), "Hệ thống phải tự động tăng Level 2 khi vượt mốc 1000 điểm");
    }

    // ==========================================
    // PHẦN 2: TEST CHO UC-06 (XEM GẠCH TIẾP THEO)
    // ==========================================

    @Test
    public void testUC06_InitialPieceSpawn() {
        // [UC-06 - Luồng thay thế 6.2 - Bước 6.2.2] Sinh ngẫu nhiên cùng lúc khối gạch rơi đầu tiên và khối dự phòng
        model.spawnNewPiece();

        // Kiểm tra hệ thống sinh ra cùng lúc cả viên đang rơi và viên dự phòng
        assertNotNull(model.getCurrentPiece(), "Khối gạch hiện tại không được phép null khi mới bắt đầu");
        assertNotNull(model.getNextPiece(), "Khối gạch dự phòng chưa được khởi tạo thành công");
    }

    @Test
    public void testUC06_NextPieceShiftDuringGameplay() {
        // Khởi tạo trạng thái ban đầu
        model.spawnNewPiece();
        Tetromino initialNextPiece = model.getNextPiece();

        // [UC-06 - Luồng cơ bản 6.1 - Bước 6.1.1 & 6.1.2] Gạch chạm đáy, đẩy viên dự phòng ra chơi tiếp và sinh viên mới
        model.spawnNewPiece();

        // Viên dự phòng cũ bây giờ phải được gán thành viên hiện tại đang rơi
        assertEquals(initialNextPiece, model.getCurrentPiece(), "Khối gạch dự phòng không được đẩy ra làm khối hiện tại");
        assertNotNull(model.getNextPiece(), "Hệ thống không sinh ra khối dự phòng mới cho lượt kế tiếp");
    }
}