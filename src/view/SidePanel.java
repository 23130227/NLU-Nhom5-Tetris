package view;

import javax.swing.*;

/**
 * {@code SidePanel} là lớp giao diện phụ chịu trách nhiệm hiển thị các thông tin bên lề của trò chơi Tetris.
 *
 * <p>Mặc dù hiện tại lớp này đang rỗng, dự kiến trong tương lai nó sẽ được sử dụng để:
 * <ul>
 * <li>Hiển thị khối gạch tiếp theo (Next Piece) để người chơi có chiến thuật xếp gạch.</li>
 * <li>Hiển thị điểm số hiện tại (Score), cấp độ (Level) và số dòng đã xóa (Lines).</li>
 * <li>Hiển thị khối gạch đang được cất giữ (Hold Piece) nếu game có tính năng này.</li>
 * </ul>
 *
 * <p>Lớp này kế thừa {@link JPanel} và dự kiến sẽ được gắn vào cột bên phải (BorderLayout.EAST) của {@link GameGUI}.
 */
public class SidePanel extends JPanel {

    // TODO: Khai báo các thành phần UI (JLabel để hiện điểm, JPanel nhỏ để vẽ Next Piece...)
    // TODO: Viết hàm khởi tạo (Constructor) nhận vào GameModel để lấy dữ liệu.
    // TODO: Ghi đè hàm paintComponent nếu muốn tự vẽ gạch giống như BoardPanel.

}