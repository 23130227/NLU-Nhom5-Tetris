package controller;

import model.GameState;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * {@code InputHandler} là lớp bắt sự kiện bàn phím (keyboard listener) cho game.
 *
 * <p>Lớp này kế thừa {@link KeyAdapter} để chỉ cần override những hàm cần thiết
 * (thay vì phải implement toàn bộ {@link java.awt.event.KeyListener}).
 *
 * <p>Nhiệm vụ chính:
 * <ul>
 *   <li>Nhận phím người chơi bấm</li>
 *   <li>Mapping phím → gọi các hành động tương ứng trong {@link GameController}</li>
 * </ul>
 */
public class InputHandler extends KeyAdapter {

    /** Tham chiếu tới controller để điều khiển logic game (di chuyển/xoay/soft drop...). */
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

    /**
     * Khởi tạo InputHandler với {@link GameController} tương ứng.
     *
     * @param controller controller điều khiển game sẽ được gọi khi có sự kiện bàn phím
     */
    public InputHandler(GameController controller) {
        this.controller = controller;
    }

    /**
     * Được gọi khi người chơi nhấn một phím.
     *
     * <p>Quy ước phím:
     * <ul>
     *   <li>LEFT  : di chuyển khối sang trái</li>
     *   <li>RIGHT : di chuyển khối sang phải</li>
     *   <li>DOWN  : di chuyển khối xuống (soft drop)</li>
     *   <li>UP    : xoay khối</li>
     *   <li>SPACE : xoay khối (hiện tại map giống UP)</li>
     * </ul>
     *
     * <p>Lưu ý: lớp này chỉ gửi lệnh sang controller, không tự kiểm tra va chạm/hợp lệ.
     *
     * @param e sự kiện bàn phím
     */
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
                controller.moveDown();
                break;
            case KeyEvent.VK_UP:
                controller.rotatePiece();
                break;

            case KeyEvent.VK_SPACE:
                controller.rotatePiece();
                break;

                // kich hoat tinh nang doi gach
            case KeyEvent.VK_SHIFT:
                controller.handleHoldPiece();
                break;
            case KeyEvent.VK_M:
                controller.toggleMusic();
                break;
            case KeyEvent.VK_H:
                controller.hardDrop();
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
                // Các phím khác: bỏ qua
                break;
        }
    }

    /**
     * Được gọi khi người chơi nhả phím.
     *
     * <p>Hiện tại chỉ xử lý trường hợp nhả phím DOWN để kết thúc chế độ soft drop
     * (trả tốc độ rơi về bình thường) thông qua {@code controller.resetSoftDrop()}.
     *
     * @param e sự kiện bàn phím
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            controller.resetSoftDrop();
        }
    }
}