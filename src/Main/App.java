package Main;

import controller.GameController;
import controller.InputHandler;
import model.GameModel;
import model.GameState;
import view.GameGUI;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class App {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        GameGUI gui = new GameGUI(model);
        GameController controller = new GameController(model, gui);

        InputHandler inputHandler = new InputHandler(controller);
        gui.getMainFrame().addKeyListener(inputHandler);
        gui.getMainFrame().requestFocus();

        gui.getStartBtn().addActionListener(e -> {
            controller.startOrResetGame();
            gui.getMainFrame().requestFocus();
        });

        // ===================================================================
        // [UC-03 - PHÂN PHỐI SỰ KIỆN ĐÓNG CỬA SỔ TẬP TRUNG THEO SƠ ĐỒ TUẦN TỰ]
        // ===================================================================
        gui.getMainFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // 3.1.1. Người chơi bấm nút đóng cửa sổ (dấu X) hoặc tổ hợp Alt+F4
                GameState currentState = model.getState();

                // Nhánh rẽ 1: Nếu đang chơi bình thường (PLAYING) -> Chuyển luồng thay thế 3.3
                if (currentState == GameState.PLAYING || currentState == GameState.PAUSED) {
                    controller.forceCloseRequest();
                }
                // Nhánh rẽ 2: Nếu đang ở các trạng thái an toàn khác (MENU, PAUSED, GAME_OVER) -> Luồng cơ bản 3.1
                else {
                    controller.exitApplication();
                }
            }
        });

        gui.getMainFrame().addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                // 2.4.4. Gain Focus sau khi mất tiêu điểm -> Hệ thống giữ nguyên Pause Menu, chờ tương tác
            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                // 2.4.1. Người chơi click ra ngoài cửa sổ game / Chuyển tab
                // 2.4.2. windowLostFocus() truyền tin báo xuống điều khiển xử lý
                controller.windowLostFocus();
            }
        });
    }
}