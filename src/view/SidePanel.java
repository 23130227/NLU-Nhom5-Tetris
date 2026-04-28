package view;

import model.GameModel;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private GameModel model;
    private static final int TILE_SIZE = 20;

    public  SidePanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(280, 600));
        setBackground(new Color(40,40,40));
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        Graphics2D g2d = (Graphics2D) g;
//
//        // Bật khử răng cưa cho chữ mượt hơn
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//        drawStatus(g2d);
//        drawHoldPiece(g2d);
//        drawNextPiece(g2d);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Vẽ khung thông tin (Score, Level)
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(10, 10, 260, 280);

        g2.setFont(new Font("SansSerif", Font.BOLD, 28));
        g2.drawString("Level : " + model.getLevel(), 40, 80);
        g2.drawString("Lines : " + "0", 40, 150); // Có thể thêm biến lines vào Model sau
        g2.drawString("Score : " + model.getScore(), 40, 220);

        // 2. Vẽ khung "Next"
        g2.drawRect(10, 310, 260, 250);
        g2.setFont(new Font("Arial", Font.BOLD, 30));
        g2.drawString("Next", 100, 355);

        drawPreview(g2, 85, 380, model.getNextPiece());

    }

    private void drawPreview(Graphics2D g2, int startX, int startY, Tetromino piece) {
        if (piece == null) return;
        int[][] matrix = piece.getMatrix();
        g2.setColor(piece.getColor());
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[r].length; c++) {
                if (matrix[r][c] != 0) {
                    int x = startX + c * TILE_SIZE;
                    int y = startY + r * TILE_SIZE;
                    g2.fillRect(x, y, TILE_SIZE - 2, TILE_SIZE - 2);
                    g2.setColor(Color.WHITE);
                    g2.drawRect(x, y, TILE_SIZE - 2, TILE_SIZE - 2);
                    g2.setColor(piece.getColor());
                }
            }
        }
    }

    private void drawNextPiece(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("NEXT", 20, 400);

        drawPreviewBox(g2d, 20, 410, model.getNextPiece());
    }

    private void drawPreviewBox(Graphics2D g2d, int x, int y, Tetromino nextPiece) {
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, y, TILE_SIZE*4, TILE_SIZE*4);

        if (nextPiece != null) {
            int[][] matrix = nextPiece.getMatrix();
            g2d.setColor(nextPiece.getColor());

            for(int row = 0; row < matrix.length; row++) {
                for(int col = 0; col < matrix[row].length; col++) {
                    if(matrix[row][col] == 1) {
                        int px = x+col*TILE_SIZE;
                        int py = y+row*TILE_SIZE;
//                        g2d.fillRect(x, y, TILE_SIZE, TILE_SIZE);
//                        g2d.setColor(Color.BLACK);
//                        g2d.drawRect(x, y, TILE_SIZE, TILE_SIZE);
//                        g2d.setColor(nextPiece.getColor());

                        int margin = 2;
                        // Vẽ màu chính
//                        g2d.setColor(nextPiece.getColor());
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
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("HOLD", 20, 250);

        drawPreviewBox(g2d, 20, 260, model.getHoldPiece());
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
