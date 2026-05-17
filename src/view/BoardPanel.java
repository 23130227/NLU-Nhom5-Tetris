package view;

import model.GameModel;
import model.Tetromino;

import javax.swing.*;
import java.awt.*;

/**
 * {@code BoardPanel} là lớp View chịu trách nhiệm vẽ (render) màn hình chính của game Tetris.
 *
 * <p>Lớp này kế thừa {@link JPanel} và sử dụng {@link Graphics2D} để vẽ đồ họa.
 * <p>Nhiệm vụ chính:
 * <ul>
 * <li>Vẽ phông nền và viền cho khu vực chơi (sân chơi).</li>
 * <li>Vẽ các khối gạch đã bị khóa (nằm dưới đáy) thông qua dữ liệu từ Board.</li>
 * <li>Vẽ khối gạch đang rơi hiện tại (current piece).</li>
 * <li>Tạo hiệu ứng 3D (nổi) cho các ô vuông gạch.</li>
 * </ul>
 */
public class BoardPanel extends JPanel {

    /** Kích thước (chiều rộng và chiều cao) của một ô vuông gạch tính bằng pixel. */
    private static final int PIXELS_SIZE = 30;

    /** Tham chiếu đến Model để lấy dữ liệu tọa độ và trạng thái bảng. */
    private GameModel model;

    /**
     * Khởi tạo Panel hiển thị bảng chơi.
     * Cài đặt kích thước mặc định là 300x600 pixel (tương ứng lưới 10x20 ô vuông, mỗi ô 30px).
     *
     * @param model dữ liệu game để đồng bộ hóa đồ họa
     */
    public BoardPanel(GameModel model) {
        this.model = model;
        setPreferredSize(new Dimension(300, 600));
        setBackground(Color.BLACK);
    }

    /**
     * Phương thức cốt lõi được hệ thống Swing gọi tự động mỗi khi cần vẽ lại giao diện.
     * Hàm này thực hiện vẽ nền, vẽ bảng gạch cũ và khối gạch mới.
     *
     * @param g đối tượng đồ họa {@link Graphics} của Java
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        // Bật tính năng khử răng cưa để hình ảnh vẽ ra mịn màng hơn
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

    /**
     * Quét lưới (grid) từ Model và vẽ tất cả các khối gạch đã được khóa ở đáy bảng.
     *
     * @param g2 đối tượng {@link Graphics2D} dùng để vẽ đồ họa 2D
     */
    private void drawGrid(Graphics2D g2) {
        int[][] grid = model.getBoard().getGrid();
        for(int row = 0; row < grid.length; row++) {
            for(int col = 0; col < grid[row].length; col++) {
                int value = grid[row][col];
                // Nếu ô này có chứa gạch (giá trị > 0)
                if(value != 0) {
                    // Lấy lại màu gốc dựa vào giá trị lưu trong bảng (nhớ trừ đi 1)
                    Color originalColor = getColorByID(value - 1);
                    drawSquare(g2, col * PIXELS_SIZE, row * PIXELS_SIZE, originalColor);
                }
            }
        }
    }

    /**
     * Hàm tiện ích: Trả về đối tượng {@link Color} dựa trên ID của loại gạch.
     * Giúp đồng bộ màu sắc giữa lúc gạch đang rơi và lúc gạch đã khóa.
     *
     * @param id mã ID của loại khối Tetromino (từ 0 đến 6)
     * @return màu sắc tương ứng
     */
    private Color getColorByID(int id) {
        switch (id) {
            case 0: return Color.CYAN;      // I-piece
            case 1: return Color.BLUE;      // J-piece
            case 2: return Color.ORANGE;    // L-piece
            case 3: return Color.YELLOW;    // O-piece
            case 4: return Color.GREEN;     // S-piece
            case 5: return Color.MAGENTA;   // T-piece
            case 6: return Color.RED;       // Z-piece
            default: return Color.GRAY;
        }
    }

    /**
     * Vẽ một ô vuông gạch duy nhất tại tọa độ chỉ định với hiệu ứng 3D.
     * Hiệu ứng 3D được tạo ra bằng cách làm sáng viền trên/trái và làm tối viền dưới/phải.
     *
     * @param g2    đối tượng vẽ đồ họa
     * @param x     tọa độ X (pixel) trên panel
     * @param y     tọa độ Y (pixel) trên panel
     * @param color màu chủ đạo của khối gạch
     */
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

    /**
     * Lấy khối gạch đang điều khiển hiện tại từ Model và vẽ nó lên panel.
     *
     * @param g2 đối tượng đồ họa {@link Graphics2D}
     */
    private void drawCurrentPiece(Graphics2D g2) {
        Tetromino piece = model.getCurrentPiece();
        if(piece != null) {
            Color color = piece.getColor();

            java.util.List<Point> points = piece.getCoordinates();

            for (Point p : points) {
                // Chỉ vẽ nếu khối đó có phần hiển thị nằm trong vùng vẽ hợp lệ của sân chơi
                drawSquare(g2, p.x * PIXELS_SIZE, p.y * PIXELS_SIZE, color);
            }
        }
    }
}