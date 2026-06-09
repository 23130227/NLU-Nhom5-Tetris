package Test;

import model.GameModel;
import model.Tetromino;
import controller.GameController;
import view.GameGUI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lớp JUnit Test phục vụ cho mục tiêu Development Testing (Unit Testing)
 * Doãn Trần Đình Khang - MSSV: 23130143 thực hiện cho phần nâng cấp của mình.
 */
public class Khang23130143_DevelopmentTesting {

    private GameModel model;
    private GameController controller;
    private GameGUI view;

    @BeforeEach
    public void setUp() {
        // Khởi tạo môi trường sạch cho mỗi ca kiểm thử
        model = new GameModel();
        view = new GameGUI(model);
        controller = new GameController(model, view);

        // Kích hoạt trạng thái chơi bình thường thông qua hàm hợp lệ của đồ án
        controller.startOrResetGame();
    }

    // ==========================================
    // PHẦN 1: TEST CHO UC-04.5 (GIỮ GẠCH & DỰ ĐOÁN BÓNG)
    // ==========================================

    @Test
    public void testUC045_InitialHoldPieceIsEmpty() {
        System.out.println("\n[RUNNING TEST] -> testUC045_InitialHoldPieceIsEmpty");
        System.out.println("   [*] Kiểm tra điều kiện tiên quyết: Kho lưu trữ Hold ban đầu...");

        // Kiểm tra kho chứa Hold ban đầu phải trống
        assertNull(model.getHeldPiece(), "Kho lưu trữ gạch Hold ban đầu phải bằng null");

        System.out.println("   [=> SUCCESS] Kho chứa Hold trống hoàn toàn (null). Đủ điều kiện tiên quyết!");
    }

    @Test
    public void testUC045_FirstHoldSuccess() {
        System.out.println("\n[RUNNING TEST] -> testUC045_FirstHoldSuccess");

        Tetromino originalPiece = model.getCurrentPiece(); //
        assertNotNull(originalPiece, "Khối gạch hiện tại không được null khi game bắt đầu");
        System.out.println("   [*] Khối gạch đang rơi trên sân chơi ban đầu có loại ID: " + originalPiece.getType());

        System.out.println("   [*] Thực hiện gọi hàm cất giữ gạch: model.holdCurrentPiece()...");
        // Gọi hàm xử lý Hold dữ liệu từ Model
        model.holdCurrentPiece();

        // Kiểm tra khối gạch cũ đã được cất vào kho thành công
        assertEquals(originalPiece, model.getHeldPiece(), "Khối gạch đang rơi chưa được đưa vào kho chứa HOLD");

        System.out.println("   [=> SUCCESS] Khối gạch ID " + model.getHeldPiece().getType() + " đã nằm gọn trong kho lưu trữ HOLD!");
    }

    @Test
    public void testUC045_GhostPieceCalculation() {
        System.out.println("\n[RUNNING TEST] -> testUC045_GhostPieceCalculation");

        Tetromino current = model.getCurrentPiece(); //
        assertNotNull(current, "Khối gạch hiện tại không được null");

        int currentY = current.getY(); //
        int ghostY = model.getGhostY(); //

        System.out.println("   [*] Tọa độ Y hiện tại của khối gạch thật: " + currentY);
        System.out.println("   [*] Thuật toán dự đoán bóng tính được tọa độ đáy giả lập (Ghost Y): " + ghostY);

        // Theo quy luật Tetris, tọa độ bóng ở đáy phải luôn >= tọa độ khối thật đang rơi
        assertTrue(ghostY >= currentY, "Tọa độ GhostY tính toán phải lớn hơn hoặc bằng tọa độ Y hiện tại");
        assertTrue(ghostY < 20, "Tọa độ đáy giả lập không được vượt quá chiều cao lưới ván đấu (20 hàng)");

        System.out.println("   [=> SUCCESS] Thuật toán getGhostY() hoạt động chuẩn xác! Khoảng cách rơi an toàn.");
    }

    // ==========================================
    // PHẦN 2: TEST CHO UC-04.6 (XOAY THÔNG MINH - WALL KICK)
    // ==========================================

    @Test
    public void testUC046_WallKickOnRightBoundary() {
        System.out.println("\n[RUNNING TEST] -> testUC046_WallKickOnRightBoundary");

        Tetromino current = model.getCurrentPiece(); //
        assertNotNull(current, "Khối gạch hiện tại không được phép null");
        System.out.println("   [*] Tọa độ X xuất phát mặc định của gạch: " + current.getX()); //

        // Tính toán khoảng cách cần dịch chuyển để ép khối gạch sát vách phải (X = 8)
        int dx = 8 - current.getX(); //
        int dy = 5 - current.getY(); //

        System.out.println("   [*] Dịch chuyển khối gạch ép sát vách tường phải (Đặt thử X = 8)...");
        // Sử dụng phương thức move() nguyên bản của đồ án
        current.move(dx, dy);
        System.out.println("   [*] Tọa độ X hiện tại trước khi xoay: " + current.getX()); //

        System.out.println("   [*] Người chơi bấm xoay -> Kích hoạt điều phối đẩy biên: controller.rotatePiece()...");
        // Kích hoạt lệnh điều phối xoay thông minh từ bộ điều khiển
        controller.rotatePiece();

        System.out.println("   [*] Tọa độ X thực tế sau khi hệ thống xử lý xoay: " + current.getX()); //

        // Trạng thái đúng: Tọa độ X phải nhỏ hơn 8 do hệ thống tự động Wall Kick đẩy lùi vào trong
        assertTrue(current.getX() < 8, "Thuật toán Wall Kick phải tự động đẩy khối gạch lùi vào lòng lưới khi sát vách phải");

        System.out.println("   [=> SUCCESS] Wall Kick phát hiện cấn vách, tự động nảy lùi khối gạch thành công!");
    }
}