package view;

import model.GameModel;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    private static final int PIXELS_SIZE = 30;
    private GameModel model;

    public BoardPanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(300, 600));
        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Vẽ nền Gradient xám cho sân chơi giống ảnh mẫu
        GradientPaint playBG = new GradientPaint(0, 0, Color.GRAY, getWidth(), getHeight(), Color.DARK_GRAY);
        g2.setPaint(playBG);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // 2. Vẽ viền trắng cực đậm bao quanh
        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(4f)); // Độ dày 4 pixel
        g2.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

        if (model == null || model.getBoard() == null) return;

        drawGrid(g2);
        drawCurrentPiece(g2);
    }

    private void drawGrid(Graphics2D g2) {
        int[][] grid = model.getBoard().getGrid();
        for(int row = 0; row < grid.length; row++) {
            for(int col = 0; col < grid[row].length; col++) {
                if(grid[row][col] != 0) {
                    Color color = model.getCurrentPiece().getColor();
                    drawSquare(g2,col*PIXELS_SIZE,row*PIXELS_SIZE,color);
                }
            }
        }
    }

    private void drawSquare(Graphics2D g2, int x, int y, Color color) {
        int margin = 2;
        // Vẽ màu chính
        g2.setColor(color);
        g2.fillRect(x + margin, y + margin, PIXELS_SIZE - margin*2, PIXELS_SIZE - margin*2);

        // Vẽ viền sáng (Top & Left) để tạo hiệu ứng 3D
        g2.setColor(color.brighter());
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(x, y, x + PIXELS_SIZE, y); // Cạnh trên
        g2.drawLine(x, y, x, y + PIXELS_SIZE); // Cạnh trái

        // Vẽ viền tối (Bottom & Right)
        g2.setColor(color.darker());
        g2.drawLine(x, y + PIXELS_SIZE, x + PIXELS_SIZE, y + PIXELS_SIZE); // Cạnh dưới
        g2.drawLine(x + PIXELS_SIZE, y, x + PIXELS_SIZE, y + PIXELS_SIZE); // Cạnh phải
    }

    private void drawCurrentPiece(Graphics2D g2) {
        Tetromino piece = model.getCurrentPiece();
        if(piece != null) {
            Color color = piece.getColor();

            java.util.List<Point> points = piece.getCoordinates();

            for (Point p : points) {
                drawSquare(g2, p.x * PIXELS_SIZE, p.y * PIXELS_SIZE, color);
            }
        }
    }
}
