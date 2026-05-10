package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp {@code Tetromino} đại diện cho một khối Tetris (I, J, L, O, S, T, Z).
 *
 * <p>Mỗi Tetromino gồm:
 * <ul>
 *   <li>{@code matrix}: ma trận 2D (0/1) mô tả hình dạng khối</li>
 *   <li>{@code x, y}: vị trí (tọa độ) của khối trên board (góc trên-trái của ma trận)</li>
 *   <li>{@code color}: màu hiển thị của khối</li>
 *   <li>{@code type}: loại khối (0..6)</li>
 * </ul>
 *
 * <p>Quy ước type:
 * <ul>
 *   <li>0: I (CYAN)</li>
 *   <li>1: J (BLUE)</li>
 *   <li>2: L (ORANGE)</li>
 *   <li>3: O (YELLOW)</li>
 *   <li>4: S (GREEN)</li>
 *   <li>5: T (MAGENTA)</li>
 *   <li>6: Z (RED)</li>
 * </ul>
 */
public class Tetromino {
    /** Ma trận biểu diễn hình dạng khối (0: rỗng, 1: có block). */
    private int[][] matrix;

    /** Vị trí hiện tại của khối trên board (góc trên-trái của ma trận). */
    private int x, y;

    /** Màu hiển thị của khối. */
    private Color color;

    /** Loại khối (0..6). */
    private int type;

    /**
     * Khởi tạo một Tetromino theo {@code type}. Mặc định spawn ở (x=3, y=0).
     *
     * @param type loại khối (0..6)
     */
    public Tetromino(int type) {
        this.type = type;

        // Vị trí spawn mặc định (thường để khối xuất hiện gần giữa phía trên board)
        this.x = 3;
        this.y = 0;

        // Gán ma trận và màu tương ứng với từng loại Tetromino
        switch (type) {
            case 0: // I
                this.matrix = new int[][]{
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                };
                this.color = Color.CYAN;
                break;

            case 1: // J
                this.matrix = new int[][]{
                        {1, 0, 0},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.BLUE;
                break;

            case 2: // L
                this.matrix = new int[][]{
                        {0, 0, 1},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.ORANGE;
                break;

            case 3: // O
                this.matrix = new int[][]{
                        {1, 1},
                        {1, 1}
                };
                this.color = Color.YELLOW;
                break;

            case 4: // S
                this.matrix = new int[][]{
                        {0, 1, 1},
                        {1, 1, 0},
                        {0, 0, 0}
                };
                this.color = Color.GREEN;
                break;

            case 5: // T
                this.matrix = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.MAGENTA;
                break;

            case 6: // Z
                this.matrix = new int[][]{
                        {1, 1, 0},
                        {0, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.RED;
                break;

            default:
                // Fallback: nếu type không hợp lệ thì dùng O
                this.matrix = new int[][]{
                        {1, 1},
                        {1, 1}
                };
                this.color = Color.YELLOW;
                break;
        }
    }

    /**
     * Di chuyển khối theo vector (dx, dy).
     *
     * <p>Lưu ý: Hàm này chỉ cập nhật tọa độ, không kiểm tra va chạm/biên.
     *
     * @param dx dịch theo trục X
     * @param dy dịch theo trục Y
     */
    public void move(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Xoay khối theo chiều kim đồng hồ 90 độ.
     *
     * <p>Thuật toán: tạo ma trận mới và gán {@code rotatedMatrix[j][size - 1 - i] = matrix[i][j]}.
     *
     * <p>Lưu ý: cách xoay này giả định ma trận là hình vuông (N x N).
     * Với khối O là 2x2 vẫn hoạt động. Với I là 4x4 hoạt động.
     */
    public void rotate() {
        int size = matrix.length;
        int[][] rotatedMatrix = new int[size][size];

        // Xoay clockwise 90°
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotatedMatrix[j][size - 1 - i] = matrix[i][j];
            }
        }
        this.matrix = rotatedMatrix;
    }

    /**
     * In ma trận của khối ra console để debug.
     */
    public void printMatrix() {
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Lấy danh sách tọa độ (các ô có giá trị != 0) của Tetromino nếu đặt tại (targetX, targetY).
     *
     * <p>Mỗi ô (row, col) trong ma trận sẽ map ra tọa độ board:
     * {@code (targetX + col, targetY + row)}.
     *
     * @param targetX tọa độ X muốn "đặt thử" khối
     * @param targetY tọa độ Y muốn "đặt thử" khối
     * @return danh sách {@link Point} đại diện cho các ô đang được chiếm trên board
     */
    public List<Point> getCoordinates(int targetX, int targetY) {
        List<Point> points = new ArrayList<>();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                // Ô khác 0 nghĩa là có block của Tetromino tại vị trí đó
                if (matrix[row][col] != 0) {
                    points.add(new Point(targetX + col, targetY + row));
                }
            }
        }
        return points;
    }

    /**
     * Lấy danh sách tọa độ của khối tại vị trí hiện tại (x, y).
     *
     * @return danh sách {@link Point} hiện tại của Tetromino
     */
    public List<Point> getCoordinates() {
        return getCoordinates(x, y);
    }

    /**
     * In danh sách tọa độ các ô mà khối đang chiếm ra console (debug).
     */
    public void printCoords() {
        for (Point p : getCoordinates()) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }

    /**
     * @return ma trận hình dạng hiện tại của Tetromino
     */
    public int[][] getMatrix() {
        return this.matrix;
    }

    /**
     * @return màu của Tetromino
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return tọa độ X hiện tại (góc trên-trái của ma trận)
     */
    public int getX() {
        return x;
    }

    /**
     * @return tọa độ Y hiện tại (góc trên-trái của ma trận)
     */
    public int getY() {
        return y;
    }

    /**
     * @return type của khối (0..6)
     */
    public int getType() {
        return type;
    }

    /**
     * Hàm main chỉ để test nhanh: in ma trận, in tọa độ, move và rotate rồi in lại.
     */
    public static void main(String[] args) {
        Tetromino t = new Tetromino(0);

        System.out.println("Ban đầu:");
        t.printMatrix();

        System.out.println("Tọa độ ban đầu:");
        t.printCoords();

        t.move(1, 2);
        System.out.println("\nSau khi move (1,2):");
        t.printCoords();

        t.rotate();
        System.out.println("\nSau khi rotate:");
        t.printMatrix();
        t.printCoords();
    }
}