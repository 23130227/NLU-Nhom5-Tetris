package controller;

import model.GameState;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {

    private GameController controller;
    /**
     * Thời điểm lần nhấn phím DOWN gần nhất.
     */
    private long lastDownPressTime = 0;

    /**
     * Khoảng thời gian tối đa giữa 2 lần nhấn DOWN
     * để được xem là Hard Drop (ms).
     */
    private static final long DOUBLE_TAP_DELAY = 250;

    public InputHandler(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        GameState currentState = controller.getModel().getState();

        // [XỬ LÝ RIÊNG BIỆT KHÔNG BỊ CHẶN BỞI BƯỚC 2.1.3]
        if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) {
            // 2.1.1. Nếu đang chơi bấm P/ESC -> Kích hoạt tạm dừng
            if (currentState == GameState.PLAYING) {
                controller.pauseGame();
            }
            // 2.1.5. Nếu đang tạm dừng bấm lại P/ESC -> Kích hoạt chơi tiếp (Resume)
            else if (currentState == GameState.PAUSED) {
                controller.resumeGame();
            }
            return;
        }

        // Các phím điều khiển khối gạch rơi tự động hoặc tương tác Menu
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                controller.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                controller.moveRight();
                break;
            case KeyEvent.VK_DOWN:
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastDownPressTime <= DOUBLE_TAP_DELAY) {
                    controller.hardDrop();
                    lastDownPressTime = 0;
                } else {
                    controller.moveDown();
                    lastDownPressTime = currentTime;
                }
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_SPACE:
                controller.rotatePiece();
                break;
            case KeyEvent.VK_SHIFT:
                controller.handleHoldPiece();
                break;
            case KeyEvent.VK_M:
                controller.toggleMusic();
                break;
            case KeyEvent.VK_R:
                // Hệ thống tự kiểm tra inside hàm nếu là PAUSED thì mới thực thi
                controller.restartGame();
                break;

            case KeyEvent.VK_E:
                // Hệ thống tự kiểm tra inside hàm nếu là PAUSED thì mới thực thi
                controller.exitToMainMenu();
                break;

            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            controller.resetSoftDrop();
        }
    }
}