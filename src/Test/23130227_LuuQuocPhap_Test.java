package controller;

import model.GameModel;
import model.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import view.GameGUI;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử vòng đời Game (UC-01, UC-02, UC-03)")
public class GameControllerTest {

    private GameModel model;
    private GameController controller;
    private GameGUI gui;

    @BeforeEach
    public void setUp() {
        // Khởi tạo môi trường kiểm thử trước mỗi test case
        model = new GameModel();
        gui = new GameGUI(model); // Tạo thực thể GUI để kết nối MVC
        controller = new GameController(model, gui);
    }

    // ===================================================================
    // KIỂM THỬ UC-01: KHỞI TẠO / BẮT ĐẦU GAME
    // ===================================================================
    @Nested
    @DisplayName("UC-01: Khởi tạo và Bắt đầu Game")
    class StartGameTest {

        @Test
        @DisplayName("Luồng cơ bản 1.1: Khởi tạo ván mới từ màn hình MENU")
        public void testStartGameFromMenu() {
            // Hiện trạng ban đầu: Game ở trạng thái MENU
            assertEquals(GameState.MENU, model.getState());

            // Thực thi hành động bấm nút "Bắt đầu"
            controller.startOrResetGame();

            // Kiểm tra các hậu điều kiện (Postconditions)
            assertEquals(0, model.getScore(), "[1.1.2] Score phải được reset về 0");
            assertEquals(1, model.getLevel(), "[1.1.3] Level phải được đặt về 1");
            assertNotNull(model.getCurrentPiece(), "[1.1.5] Phải sinh khối gạch hiện tại");
            assertNotNull(model.getNextPiece(), "[1.1.5] Phải sinh trước khối gạch tiếp theo");
            assertEquals(GameState.PLAYING, model.getState(), "[1.1.7] Trạng thái game phải chuyển sang PLAYING");
        }

        @Test
        @DisplayName("Luồng thay thế 1.3: Bấm nút bắt đầu khi game đang chạy (PLAYING)")
        public void testStartGameWhilePlaying() {
            // Ép hệ thống vào trạng thái đang chơi ván 1
            controller.startOrResetGame();
            model.updateScore(2); // Ăn 2 hàng để tăng score lên 300
            int currentScore = model.getScore();
            
            assertEquals(GameState.PLAYING, model.getState());
            assertTrue(currentScore > 0);

            // Vô tình kích hoạt lại lệnh bắt đầu game lần nữa
            controller.startOrResetGame();

            // Kiểm tra luồng 1.3.2 & 1.3.3: Hệ thống phải bỏ qua, giữ nguyên tiến trình cũ
            assertEquals(currentScore, model.getScore(), "[1.3.2] Điểm số cũ phải giữ nguyên, không bị reset");
            assertEquals(GameState.PLAYING, model.getState(), "[1.3.3] Duy trì trạng thái PLAYING hiện tại");
        }
    }

    // ===================================================================
    // KIỂM THỬ UC-02: TẠM DỪNG / TIẾP TỤC GAME
    // ===================================================================
    @Nested
    @DisplayName("UC-02: Tạm dừng và Tiếp tục Game")
    class PauseResumeGameTest {

        @BeforeEach
        public void startVandau() {
            // Đưa game vào trạng thái PLAYING trước khi test tính năng pause
            controller.startOrResetGame();
        }

        @Test
        @DisplayName("Luồng cơ bản 2.1: Tạm dừng và Chơi tiếp chủ động")
        public void testPauseAndResumeActive() {
            // 2.1.1. Người chơi kích hoạt tạm dừng game
            controller.pauseGame();

            // Kiểm tra trạng thái dừng
            assertEquals(GameState.PAUSED, model.getState(), "[2.1.2] Trạng thái dữ liệu hệ thống phải chuyển sang PAUSED");

            // 2.1.5. Người chơi chọn Tiếp tục (Resume) từ Pause Menu
            controller.resumeGame();

            // Kiểm tra trạng thái phục hồi
            assertEquals(GameState.PLAYING, model.getState(), "[2.1.8] Trạng thái game phải quay về PLAYING");
        }

        @Test
        @DisplayName("Luồng thay thế 2.2: Chọn 'Chơi lại' (Restart) từ Pause Menu")
        public void testRestartFromPauseMenu() {
            model.updateScore(1); // Tạo ra điểm số giả lập 100 điểm
            controller.pauseGame(); // Tạm dừng game
            
            // Người chơi chọn nút Restart trên Menu
            controller.restartGame();

            // Kiểm tra luồng kết nối sang UC-01
            assertEquals(0, model.getScore(), "[2.2.3] Kết thúc PAUSED, Điểm số ván cũ phải xóa sạch về 0");
            assertEquals(GameState.PLAYING, model.getState(), "[2.2.3] Chuyển trạng thái hệ thống về PLAYING ván mới");
        }

        @Test
        @DisplayName("Luồng thay thế 2.3: Chọn 'Thoát' (Exit) về Menu chính từ Pause Menu")
        public void testExitToMainMenuFromPause() {
            controller.pauseGame(); // Tạm dừng game

            // Người chơi chọn nút Thoát về Menu chính
            controller.exitToMainMenu();

            // Kiểm tra luồng hủy ván chơi đưa về màn hình chờ
            assertEquals(0, model.getScore(), "[2.3.3] Dữ liệu điểm ván cũ phải xóa sạch");
            assertEquals(GameState.MENU, model.getState(), "[2.3.3] Trạng thái dữ liệu hệ thống chuyển về MENU");
        }

        @Test
        @DisplayName("Luồng thay thế 2.4: Tự động tạm dừng khi mất tiêu điểm cửa sổ (Lose Focus)")
        public void testAutoPauseOnWindowLostFocus() {
            assertEquals(GameState.PLAYING, model.getState());

            // Giả lập sự kiện người chơi Alt+Tab hoặc click chuột ra ngoài cửa sổ
            controller.windowLostFocus();

            // Kiểm tra tính năng bảo vệ tiến trình tự động của game
            assertEquals(GameState.PAUSED, model.getState(), "[2.4.2] Hệ thống bắt được sự kiện tự động kích hoạt PAUSED");
        }
    }
}
