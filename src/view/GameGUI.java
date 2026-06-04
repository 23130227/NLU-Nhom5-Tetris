package view;

import model.GameModel;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GameGUI {

    private JFrame mainFrame;
    private BoardPanel boardPanel;
    private SidePanel sidePanel;
    private GameModel model;
    private JButton startBtn;

    // Thuộc tính theo dõi hiển thị màn hình lớp phủ Tạm dừng
    private boolean isPauseMenuVisible = false;

    public GameGUI(GameModel model) {
        this.model = model;
        this.mainFrame = new JFrame("Tetris");

        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(260, 600));
        leftPanel.setBackground(Color.BLACK);
        leftPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setFont(new Font("Segoe UI", Font.BOLD, 48));
                g2.setColor(new Color(0, 200, 255));
                g2.drawString("TETRIS", 20, 80);

                g2.setColor(new Color(0, 200, 255, 80));
                g2.drawString("TETRIS", 22, 82);

                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("Modern Edition", 22, 110);

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

        startBtn = new JButton("Bắt đầu");
        startBtn.setFocusPainted(false);
        startBtn.setFocusable(false);
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        startBtn.setBackground(new Color(70, 70, 70));
        startBtn.setForeground(Color.WHITE);
        startBtn.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.add(startBtn);

        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(bottomPanel, BorderLayout.SOUTH);

        this.boardPanel = new BoardPanel(model);
        this.sidePanel = new SidePanel(model);

        JPanel mainContainer = new JPanel(new BorderLayout(30, 0));
        mainContainer.setBackground(Color.BLACK);
        mainContainer.setBorder(new EmptyBorder(40, 40, 40, 40));

        mainContainer.add(leftPanel, BorderLayout.WEST);
        mainContainer.add(boardPanel, BorderLayout.CENTER);
        mainContainer.add(sidePanel, BorderLayout.EAST);

        mainFrame.add(mainContainer);
        mainFrame.getContentPane().setBackground(Color.BLACK);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setResizable(false);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    /**
     * [UC-02 - Bước 2.1.4 / 2.4.3]: Kích hoạt hiển thị Menu tạm dừng
     */
    public void showPauseMenu() {
        this.isPauseMenuVisible = true;
        // Thầy cô yêu cầu giao diện vẽ lớp phủ chứa thông tin chữ và 3 lựa chọn
        refresh();
    }

    /**
     * [UC-02 - Bước 2.1.6 / 2.2.2 / 2.3.2]: Gỡ bỏ màn hình lớp phủ tạm dừng
     */
    public void hidePauseMenu() {
        this.isPauseMenuVisible = false;
        refresh();
    }

    /**
     * [UC-02 - Bước 2.3.3]: Hiển thị lại màn hình Menu chính (Main Menu) xóa sạch trận đấu
     */
    public void showMainMenu() {
        this.isPauseMenuVisible = false;
        JOptionPane.showMessageDialog(mainFrame, "Đã thoát ván đấu. Trở về màn hình MENU chính!");
        refresh();
    }

    public void updateLevelUI(int level) {
        if (sidePanel != null) {
            sidePanel.repaint();
        }
    }

    public void refresh(){
        boardPanel.repaint();
        sidePanel.repaint();
    }

    public void showGameOver(){
        JOptionPane.showMessageDialog(mainFrame, "Game Over!\nScore: " + model.getScore());
    }

    public JFrame getMainFrame() { return mainFrame; }
    public JButton getStartBtn() { return startBtn; }
    public boolean isPauseMenuVisible() { return isPauseMenuVisible; }
}