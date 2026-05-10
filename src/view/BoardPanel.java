package view;

import model.GameModel;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {

    // Kích thước mỗi ô vuông trong game
    private static final int PIXELS_SIZE = 30;

    // Model chứa dữ liệu trò chơi
    private GameModel model;

    /*
     * =========================
     * KHỞI TẠO PANEL GAME
     * =========================
     */

    public BoardPanel(GameModel model) {

        // Gán model
        this.model = model;

        // Thiết lập kích thước panel
        setPreferredSize(new Dimension(300, 600));

        // Thiết lập màu nền mặc định
        setBackground(Color.BLACK);
    }

    /*
     * =========================
     * VẼ GIAO DIỆN GAME
     * =========================
     */

    @Override
    public void paintComponent(Graphics g) {

        // Gọi hàm vẽ mặc định của JPanel
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        // Bật anti-aliasing để hình ảnh mượt hơn
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );

        /*
         * ===== VẼ NỀN SÂN CHƠI =====
         */

        // Tạo nền gradient xám
        GradientPaint playBG = new GradientPaint(
                0,
                0,
                Color.GRAY,
                getWidth(),
                getHeight(),
                Color.DARK_GRAY
        );

        g2.setPaint(playBG);

        // Vẽ nền cho panel
        g2.fillRect(0, 0, getWidth(), getHeight());

        /*
         * ===== VẼ VIỀN SÂN CHƠI =====
         */

        g2.setColor(Color.WHITE);

        // Độ dày viền
        g2.setStroke(new BasicStroke(4f));

        // Vẽ khung bao quanh sân chơi
        g2.drawRect(2, 2, getWidth() - 4, getHeight() - 4);

        // Kiểm tra model và board tồn tại
        if (model == null || model.getBoard() == null) return;

        /*
         * ===== VẼ DỮ LIỆU GAME =====
         */

        // Vẽ các block đã cố định
        drawGrid(g2);

        // Vẽ block đang điều khiển
        drawCurrentPiece(g2);
    }

    /*
     * =========================
     * VẼ CÁC BLOCK TRÊN LƯỚI
     * =========================
     */

    private void drawGrid(Graphics2D g2) {

        // Lấy ma trận bàn chơi
        int[][] grid = model.getBoard().getGrid();

        // Duyệt từng dòng
        for (int row = 0; row < grid.length; row++) {

            // Duyệt từng cột
            for (int col = 0; col < grid[row].length; col++) {

                int value = grid[row][col];

                // Nếu ô khác 0 thì có block
                if (value != 0) {

                    // Lấy màu tương ứng với block
                    Color originalColor = getColorByID(value - 1);

                    // Vẽ block
                    drawSquare(
                            g2,
                            col * PIXELS_SIZE,
                            row * PIXELS_SIZE,
                            originalColor
                    );
                }
            }
        }
    }

    /*
     * =========================
     * LẤY MÀU THEO ID BLOCK
     * =========================
     */

    private Color getColorByID(int id) {

        switch (id) {

            case 0:
                return Color.CYAN;

            case 1:
                return Color.BLUE;

            case 2:
                return Color.ORANGE;

            case 3:
                return Color.YELLOW;

            case 4:
                return Color.GREEN;

            case 5:
                return Color.MAGENTA;

            case 6:
                return Color.RED;

            default:
                return Color.GRAY;
        }
    }

    /*
     * =========================
     * VẼ MỘT Ô VUÔNG BLOCK
     * =========================
     */

    private void drawSquare(Graphics2D g2, int x, int y, Color color) {

        int margin = 2;

        /*
         * ===== VẼ MÀU CHÍNH =====
         */

        g2.setColor(color);

        g2.fillRect(
                x + margin,
                y + margin,
                PIXELS_SIZE - margin * 2,
                PIXELS_SIZE - margin * 2
        );

        /*
         * ===== VẼ HIỆU ỨNG 3D =====
         */

        // Viền sáng
        g2.setColor(color.brighter());

        g2.setStroke(new BasicStroke(2));

        // Cạnh trên
        g2.drawLine(x, y, x + PIXELS_SIZE, y);

        // Cạnh trái
        g2.drawLine(x, y, x, y + PIXELS_SIZE);

        // Viền tối
        g2.setColor(color.darker());

        // Cạnh dưới
        g2.drawLine(
                x,
                y + PIXELS_SIZE,
                x + PIXELS_SIZE,
                y + PIXELS_SIZE
        );

        // Cạnh phải
        g2.drawLine(
                x + PIXELS_SIZE,
                y,
                x + PIXELS_SIZE,
                y + PIXELS_SIZE
        );
    }

    /*
     * =========================
     * VẼ KHỐI GẠCH ĐANG DI CHUYỂN
     * =========================
     */

    private void drawCurrentPiece(Graphics2D g2) {

        // Lấy Tetromino hiện tại
        Tetromino piece = model.getCurrentPiece();

        if (piece != null) {

            // Lấy màu block
            Color color = piece.getColor();

            // Lấy danh sách tọa độ các ô vuông
            java.util.List<Point> points = piece.getCoordinates();

            // Vẽ từng ô vuông
            for (Point p : points) {

                drawSquare(
                        g2,
                        p.x * PIXELS_SIZE,
                        p.y * PIXELS_SIZE,
                        color
                );
            }
        }
    }
}