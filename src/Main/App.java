package Main;

import controller.GameController;
import controller.InputHandler;
import model.GameModel;
import view.GameGUI;

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

        // [UC-02 - Luồng thay thế 2.4]: Bắt sự kiện hệ thống cửa sổ mất tiêu điểm (Lose Focus)
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