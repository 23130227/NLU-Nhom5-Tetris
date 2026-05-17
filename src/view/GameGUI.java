package view;

import model.GameModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * {@code GameGUI} là lớp giao diện tổng thể (Main Window) của game Tetris.
 *
 * <p>Nhiệm vụ chính:
 * <ul>
 * <li>Tạo và quản lý cửa sổ ứng dụng chính ({@link JFrame}).</li>
 * <li>Khởi tạo và sắp xếp bố cục (layout) cho các thành phần giao diện con như
 * bảng chơi ({@link BoardPanel}) và bảng thông tin bên trái/phải.</li>
 * <li>Cung cấp các hàm để cập nhật lại giao diện (refresh) và hiển thị thông báo (Game Over).</li>
 * </ul>
 */
public class GameGUI {

    /** Cửa sổ chính của ứng dụng. */
    private JFrame mainFrame;

    /** Khu vực hiển thị bảng chơi chính (nơi gạch rơi). */
    private BoardPanel boardPanel;

    /** Khu vực hiển thị thông tin phụ (điểm số, khối tiếp theo). Hiện đang tạm ẩn. */
    private SidePanel sidePanel;

    /** Tham chiếu đến Model để lấy dữ liệu hiển thị (như điểm số). */
    private GameModel model;

    /** Nút bấm để bắt đầu trò chơi. */
    private JButton startBtn;

    /**
     * Khởi tạo Giao diện người dùng (GUI) cho game.
     * Thiết lập các Panel, trang trí chữ, đổ bóng và sắp xếp chúng vào cửa sổ chính.
     *
     * @param model dữ liệu game để truyền cho các panel con hiển thị
     */
    public GameGUI(GameModel model) {
        this.model = model;
        this.mainFrame = new JFrame("Tetris");

        // Panel chứa tiêu đề "Tetris Pro" bên trái
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(260, 600));
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BorderLayout());

        // Panel phía trên của cột trái, ghi đè hàm paintComponent để vẽ text Custom
        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // ===== TITLE =====
                g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
                g2.setColor(new Color(0, 200, 255)); // xanh neon
                g2.drawString("TETRIS", 20, 80);

                // Shadow nhẹ (Đổ bóng cho chữ TETRIS)
                g2.setColor(new Color(0, 200, 255, 80));
                g2.drawString("TETRIS", 22, 82);

                // ===== SUBTITLE =====
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("Modern Edition", 22, 110);

                // ===== POWERUP =====
                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
                g2.setColor(new Color(255, 200, 0));
                g2.drawString("POWER-UP", 20, 250);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.setColor(Color.WHITE);
                g2.drawString("Ready", 20, 280);
            }
        };
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(260, 300));

        // Khởi tạo nút Bắt đầu
        startBtn = new JButton("Bắt đầu");
        startBtn.setFocusPainted(false); // Bỏ viền bao quanh chữ khi click
        startBtn.setFocusable(false);    // Bỏ focus để không ăn phím Space của người chơi khi đang xếp gạch
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startBtn.setBackground(new Color(70, 70, 70));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Panel phía dưới của cột trái chứa nút Bắt đầu
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(startBtn);

        // Lắp ráp phần trên và dưới vào cột trái
        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Khởi tạo bảng chơi ở giữa
        this.boardPanel = new BoardPanel(model);

        // TODO: Mở comment dòng này khi class SidePanel được hoàn thiện
//        this.sidePanel = new SidePanel(model);

        // Container chính dùng BorderLayout để xếp 3 phần: Title (West) | Board (Center) | Info (East)
        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));
        mainContainer.setBackground(Color.BLACK);
        mainContainer.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Thêm các panel con vào Container chính
        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(boardPanel, BorderLayout.CENTER);

        // TODO: Mở comment dòng này khi class SidePanel được hoàn thiện
//        mainContainer.add(sidePanel, BorderLayout.EAST);

        // Cấu hình Cửa sổ chính
        mainFrame.add(mainContainer);
        mainFrame.getContentPane().setBackground(Color.BLACK);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Thoát chương trình khi đóng cửa sổ
        mainFrame.setResizable(false);                            // Không cho phép thay đổi kích thước cửa sổ
        mainFrame.pack();                                         // Tự động điều chỉnh kích thước cho vừa các thành phần bên trong
        mainFrame.setLocationRelativeTo(null);                    // Hiển thị ở giữa màn hình
        mainFrame.setVisible(true);                               // Hiển thị cửa sổ
    }

    /**
     * Cập nhật lại giao diện đồ họa.
     * Hàm này thường được gọi từ GameController sau mỗi lần gạch rơi hoặc di chuyển
     * để vẽ lại trạng thái mới nhất của trò chơi.
     */
    public void refresh(){
        boardPanel.repaint(); // Yêu cầu BoardPanel vẽ lại

        // TODO: Mở comment dòng này khi class SidePanel được hoàn thiện
//        sidePanel.repaint();
    }

    /**
     * Hiển thị hộp thoại thông báo khi trò chơi kết thúc (Game Over).
     * Yêu cầu người chơi nhập tên để lưu điểm số.
     */
    public void showGameOver(){
        String over = JOptionPane.showInputDialog(mainFrame, "Game Over!\nScore:" + model.getScore() +
                "\nEnter your name:", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Lấy tham chiếu đến cửa sổ chính của game.
     *
     * @return cửa sổ {@link JFrame} chính
     */
    public JFrame getMainFrame() {
        return mainFrame;
    }

    /**
     * Lấy tham chiếu đến nút "Bắt đầu" để Controller có thể gắn sự kiện (ActionListener).
     *
     * @return nút {@link JButton} khởi động game
     */
    public JButton getStartBtn() {
        return startBtn;
    }
}