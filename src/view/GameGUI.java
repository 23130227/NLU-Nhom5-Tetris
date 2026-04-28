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

    public GameGUI(GameModel model) {
        this.model = model;
        this.mainFrame = new JFrame("Tetris");
//
//        this.boardPanel = new BoardPanel(model);
//        this.sidePanel = new SidePanel(model);
//
//        mainFrame.setLayout(new BorderLayout());
//        mainFrame.add(boardPanel, BorderLayout.CENTER);
//        mainFrame.add(sidePanel, BorderLayout.EAST);
//
//        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        mainFrame.setResizable(false);
//        mainFrame.pack();
//        mainFrame.setLocationRelativeTo(null);
//        mainFrame.setVisible(true);

        // Panel chứa tiêu đề "Tetris Pro" bên trái
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Times New Roman", Font.ITALIC, 45));
                g2.drawString("Tetris", 20, 80);

                // Trạng thái Power-up (Nếu có logic sau này)
                g2.setColor(Color.YELLOW);
                g2.setFont(new Font("SansSerif", Font.BOLD, 22));
                g2.drawString("Powerup Ready!", 20, 250);
            }
        };
        leftPanel.setPreferredSize(new Dimension(300, 600));
        leftPanel.setBackground(Color.BLACK);

        this.boardPanel = new BoardPanel(model);
        this.sidePanel = new SidePanel(model);

        // Container chính dùng BorderLayout để xếp 3 phần: Title | Board | Info
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

    public void refresh(){
        boardPanel.repaint();
        sidePanel.repaint();
    }

    public void showGameOver(){
        String over = JOptionPane.showInputDialog(mainFrame, "Game Over!\nScore:" + model.getScore() +
                "\nEnter your name:", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }
}
