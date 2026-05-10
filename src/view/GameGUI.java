package view;

import model.GameModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameGUI {

    // Cửa sổ chính của game
    private JFrame mainFrame;

    // Panel hiển thị bàn chơi
    private BoardPanel boardPanel;

    // Panel thông tin bên cạnh (nếu sử dụng)
    private SidePanel sidePanel;

    // Model chứa dữ liệu game
    private GameModel model;

    // Nút bắt đầu game
    private JButton startBtn;

    /*
     * =========================
     * KHỞI TẠO GIAO DIỆN GAME
     * =========================
     */

    public GameGUI(GameModel model) {

        // Gán model
        this.model = model;

        // Tạo cửa sổ game
        this.mainFrame = new JFrame("Tetris");

        /*
         * =========================
         * PANEL BÊN TRÁI
         * =========================
         */

        // Panel chứa tiêu đề và nút chức năng
        JPanel leftPanel = new JPanel();

        leftPanel.setPreferredSize(new Dimension(260, 600));
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BorderLayout());

        /*
         * =========================
         * PANEL HIỂN THỊ TITLE
         * =========================
         */

        JPanel topPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {

                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g;

                // Bật anti-aliasing để giao diện mượt hơn
                g2.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON
                );

                /*
                 * ===== VẼ TIÊU ĐỀ GAME =====
                 */

                g2.setFont(new Font("Segoe UI", Font.BOLD, 48));

                // Màu neon
                g2.setColor(new Color(0, 200, 255));

                g2.drawString("TETRIS", 20, 80);

                /*
                 * ===== HIỆU ỨNG SHADOW =====
                 */

                g2.setColor(new Color(0, 200, 255, 80));

                g2.drawString("TETRIS", 22, 82);

                /*
                 * ===== SUBTITLE =====
                 */

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));

                g2.setColor(Color.LIGHT_GRAY);

                g2.drawString("Modern Edition", 22, 110);

                /*
                 * ===== THÔNG TIN POWER-UP =====
                 */

                g2.setFont(new Font("Segoe UI", Font.BOLD, 18));

                g2.setColor(new Color(255, 200, 0));

                g2.drawString("POWER-UP", 20, 250);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));

                g2.setColor(Color.WHITE);

                g2.drawString("Ready", 20, 280);
            }
        };

        // Cho phép panel trong suốt
        topPanel.setOpaque(false);

        topPanel.setPreferredSize(new Dimension(260, 300));

        /*
         * =========================
         * NÚT BẮT ĐẦU GAME
         * =========================
         */

        startBtn = new JButton("Bắt đầu");

        // Tắt hiệu ứng focus
        startBtn.setFocusPainted(false);
        startBtn.setFocusable(false);

        // Thiết lập font chữ
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // Màu nền nút
        startBtn.setBackground(new Color(70, 70, 70));

        // Màu chữ
        startBtn.setForeground(Color.WHITE);

        // Padding cho nút
        startBtn.setBorder(
                BorderFactory.createEmptyBorder(20, 50, 20, 50)
        );

        /*
         * =========================
         * PANEL CHỨA BUTTON
         * =========================
         */

        JPanel bottomPanel = new JPanel();

        bottomPanel.setOpaque(false);

        bottomPanel.add(startBtn);

        /*
         * =========================
         * THÊM COMPONENT VÀO LEFT PANEL
         * =========================
         */

        leftPanel.add(topPanel, BorderLayout.NORTH);

        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        /*
         * =========================
         * KHỞI TẠO BOARD PANEL
         * =========================
         */

        this.boardPanel = new BoardPanel(model);

        // Panel hiển thị thông tin bên phải
//        this.sidePanel = new SidePanel(model);

        /*
         * =========================
         * CONTAINER CHÍNH
         * =========================
         */

        // Layout chính gồm:
        // Left Panel | Board | Side Panel
        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));

        mainContainer.setBackground(Color.BLACK);

        mainContainer.setBorder(
                new EmptyBorder(40, 40, 40, 40)
        );

        // Thêm các panel vào container
        mainContainer.add(leftPanel, BorderLayout.WEST);

        mainContainer.add(boardPanel, BorderLayout.CENTER);

//        mainContainer.add(sidePanel, BorderLayout.EAST);

        /*
         * =========================
         * CẤU HÌNH CỬA SỔ GAME
         * =========================
         */

        mainFrame.add(mainContainer);

        mainFrame.getContentPane().setBackground(Color.BLACK);

        // Đóng chương trình khi tắt cửa sổ
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Không cho thay đổi kích thước
        mainFrame.setResizable(false);

        // Tự động tính kích thước
        mainFrame.pack();

        // Hiển thị giữa màn hình
        mainFrame.setLocationRelativeTo(null);

        // Hiển thị cửa sổ
        mainFrame.setVisible(true);
    }

    /*
     * =========================
     * CẬP NHẬT GIAO DIỆN
     * =========================
     */

    public void refresh() {

        // Vẽ lại bàn chơi
        boardPanel.repaint();

//        sidePanel.repaint();
    }

    /*
     * =========================
     * HIỂN THỊ GAME OVER
     * =========================
     */

    public void showGameOver() {

        // Hiển thị hộp thoại nhập tên người chơi
        String over = JOptionPane.showInputDialog(
                mainFrame,
                "Game Over!\nScore:" + model.getScore() +
                        "\nEnter your name:",
                "Game Over",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /*
     * =========================
     * GETTER
     * =========================
     */

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JButton getStartBtn() {
        return startBtn;
    }
}