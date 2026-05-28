package view;

import model.GameModel;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;

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
    private GameModel model;
    private static final int TILE_SIZE = 20;

    public  SidePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(280, 600));

        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f));

        // ===== 1. STATUS BOX =====
        g2.setColor(Color.DARK_GRAY);           // màu nền
        g2.fillRoundRect(10, 10, 260, 200, 20, 20);
        g2.setColor(Color.white);
        g2.drawRoundRect(10, 10, 260, 200, 20 , 20);
        drawStatus(g2);

        // ===== 2. HOLD BOX =====
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(10, 230, 260, 150, 20, 20);
        g2.setColor(Color.white);
        g2.drawRoundRect(10, 230, 260, 150, 20 , 20);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("HOLD", 95, 260);
        drawHoldPiece(g2);

        // ===== 3. NEXT BOX =====
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(10, 400, 260, 200, 20, 20);
        g2.setColor(Color.WHITE);
        g2.drawRoundRect(10, 400, 260, 200, 20 , 20);
        g2.setFont(new Font("Arial", Font.BOLD, 20));
        g2.drawString("NEXT", 95, 430);
        drawNextPiece(g2);

    }

    private void drawNextPiece(Graphics2D g2d) {
        drawPreviewBox(g2d, 95, 480, model.getNextPiece());
    }

    private void drawPreviewBox(Graphics2D g2d, int x, int y, Tetromino nextPiece) {
        g2d.setColor(Color.DARK_GRAY);

        if (nextPiece != null) {
            int[][] matrix = nextPiece.getMatrix();
            g2d.setColor(nextPiece.getColor());

            for(int row = 0; row < matrix.length; row++) {
                for(int col = 0; col < matrix[row].length; col++) {
                    if(matrix[row][col] == 1) {
                        int px = x+col*TILE_SIZE;
                        int py = y+row*TILE_SIZE;

                        int margin = 2;
                        g2d.fillRect(px + margin, py + margin, TILE_SIZE - margin*2, TILE_SIZE - margin*2);

                        // Vẽ viền sáng (Top & Left) để tạo hiệu ứng 3D
                        g2d.setColor(nextPiece.getColor().brighter());
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawLine(px, py, px + TILE_SIZE, py); // Cạnh trên
                        g2d.drawLine(px, py, px, py + TILE_SIZE); // Cạnh trái

                        // Vẽ viền tối (Bottom & Right)
                        g2d.setColor(nextPiece.getColor().darker());
                        g2d.drawLine(px, py + TILE_SIZE, px + TILE_SIZE, py + TILE_SIZE); // Cạnh dưới
                        g2d.drawLine(px + TILE_SIZE, py, px + TILE_SIZE, py + TILE_SIZE);
                    }
                }
            }
        }

    }
    private void drawHoldPiece(Graphics2D g2d) {
        drawPreviewBox(g2d, 95, 300, model.getHoldPiece());
    }

    private void drawStatus(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));

        g2d.drawString("SCORE", 20, 50);
        g2d.setFont(new Font("Arial", Font.PLAIN, 25));
        g2d.drawString(String.valueOf(model.getScore()), 20, 80);

        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString("LEVEL", 20, 150);
        g2d.setFont(new Font("Arial", Font.PLAIN, 25));
        g2d.drawString(String.valueOf(model.getLevel()), 20, 180);
    }


}