package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code Board} đại diện cho bảng chơi (lưới) của game Tetris.
 *
 * <p>Nhiệm vụ chính:
 * <ul>
 * <li>Lưu trữ trạng thái các khối gạch đã rơi xuống đáy (khóa lại).</li>
 * <li>Kiểm tra va chạm (đụng tường, đụng gạch cũ) khi khối di chuyển hoặc xoay.</li>
 * <li>Quét và xóa các hàng đã xếp đầy gạch, sau đó dồn các hàng phía trên xuống.</li>
 * </ul>
 */
public class Board {

    /** * Lưới 2 chiều lưu trữ trạng thái bảng chơi.
     * Giá trị 0 nghĩa là ô trống, giá trị > 0 tương ứng với loại khối (màu sắc) đã bị khóa.
     */
    private int[][] grid;

    /** Chiều rộng của bảng (số cột). Mặc định chuẩn Tetris là 10. */
    private int width;

    /** Chiều cao của bảng (số hàng). Mặc định chuẩn Tetris là 20. */
    private int height;
    /***/
    private List<Integer> clearingLines = new ArrayList<>();
    /**
     * Khởi tạo bảng chơi mới với kích thước chuẩn 10 cột x 20 hàng.
     */
    public Board() {
        this.width = 10;
        this.height = 20;
        this.grid = new int[height][width];
    }

    /**
     * Kiểm tra xem việc đặt/di chuyển khối tới vị trí (x, y) có hợp lệ không.
     *
     * <p>Một vị trí là hợp lệ nếu:
     * <ul>
     * <li>Tất cả các phần tử (ô vuông) của khối không vượt ra ngoài biên trái, phải, hoặc dưới của bảng.</li>
     * <li>Không bị trùng lấp lên các ô gạch đã bị khóa trước đó trong {@code grid}.</li>
     * </ul>
     *
     * @param p Khối Tetromino cần kiểm tra
     * @param x Tọa độ x (cột) dự kiến
     * @param y Tọa độ y (hàng) dự kiến
     * @return {@code true} nếu di chuyển hợp lệ, ngược lại trả về {@code false}
     */
    public boolean isValidMove(Tetromino p, int x, int y) {

        List<Point> points = p.getCoordinates(x, y);

        for (Point pt : points) {
            // Kiểm tra chạm biên (trái, phải, đáy)
            if (pt.x < 0 || pt.x >= width || pt.y >= height) {
                return false;
            }
            // Kiểm tra va chạm với các khối đã bị khóa (bỏ qua các phần tử có y < 0 đang ở trên nóc bảng)
            if (pt.y >= 0 && grid[pt.y][pt.x] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Khóa khối hiện tại vào bảng lưới khi nó không thể rơi xuống thêm được nữa.
     *
     * <p>Lưu giá trị loại của khối (type + 1) vào mảng {@code grid} để phục vụ cho
     * việc hiển thị màu sắc và kiểm tra va chạm sau này.
     *
     * @param p Khối Tetromino vừa chạm đáy hoặc chạm gạch
     */
    public void lockPiece(Tetromino p) {
        List<Point> points = p.getCoordinates();
        for (Point pt : points) {
            // Chỉ ghi nhận các ô nằm hoàn toàn trong bảng (tránh lỗi IndexOutOfBounds)
            if (pt.y >= 0 && pt.y < height && pt.x >= 0 && pt.x < width) {
                grid[pt.y][pt.x] = p.getType() + 1;
            }
        }
    }

    /**
     * Quét toàn bộ bảng (từ dưới lên trên) để tìm ra các hàng đã được lấp đầy gạch.
     *
     * @return Danh sách chứa chỉ số của các hàng (row) đã đầy. Trả về list rỗng nếu không có hàng nào đầy.
     */
    public List<Integer> scanFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        // Quét từ đáy bảng lên trên
        for (int row = height - 1; row >= 0; row--) {
            boolean isFull = true;
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == 0) {
                    isFull = false;
                    break; // Có một ô trống tức là hàng này chưa đầy
                }
            }
            if (isFull) {
                fullLines.add(row);
            }
        }
        return fullLines;
    }

    /**
     * Xóa các hàng đã đầy và dồn các hàng phía trên xuống.
     *
     * <p>Thuật toán sử dụng kỹ thuật 2 con trỏ (readRow và writeRow) đi từ đáy lên:
     * Dòng nào không bị xóa sẽ được copy xuống vị trí ghi hiện tại.
     * Cuối cùng, gán giá trị 0 (trống) cho các dòng thừa ở trên cùng.
     *
     * @param lines Danh sách chỉ số các hàng cần xóa (lấy từ hàm {@link #scanFullLines()})
     */
    public void clearAndShift(List<Integer> lines) {
        if (lines.isEmpty()) return;

        int writeRow = height - 1; // Con trỏ ghi (bắt đầu từ đáy)

        // Đọc từng hàng từ đáy lên
        for (int readRow = height - 1; readRow >= 0; readRow--) {
            // Nếu hàng này KHÔNG nằm trong danh sách cần xóa, copy nó vào vị trí writeRow
            if (!lines.contains(readRow)) {
                for (int col = 0; col < width; col++) {
                    grid[writeRow][col] = grid[readRow][col];
                }
                writeRow--; // Di chuyển con trỏ ghi lên một dòng
            }
        }

        // Lấp đầy các dòng trống trên cùng bằng giá trị 0 (ô trống)
        while (writeRow >= 0) {
            for (int col = 0; col < width; col++) {
                grid[writeRow][col] = 0;
            }
            writeRow--;
        }
    }

    /**
     * Lấy mảng 2 chiều đại diện cho bảng hiện tại (phục vụ cho View vẽ giao diện).
     *
     * @return Lưới grid lưu trạng thái gạch
     */
    public int[][] getGrid() {
        return this.grid;
    }

    /**
     * Đặt lại bảng chơi về trạng thái trống rỗng (khi người chơi bắt đầu game mới).
     */
    public void reset() {
        this.grid = new int[height][width];
    }

    public List<Integer> getClearingLines() {
        return clearingLines;
    }

    public void setClearingLines(List<Integer> clearingLines) {
        this.clearingLines = clearingLines;
    }
}