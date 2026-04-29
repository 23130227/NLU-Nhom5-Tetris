package model;

import java.util.List;
import java.awt.Point;

public class Board {
    private int[][] grid;
    private int width = 10;
    private int height = 20;

    public Board() {
        grid = new int[height][width];
    }

    public boolean isValidMove(Tetromino p, int x, int y) {
        List<Point> points = p.getCoordinates(x, y);

        for (Point point : points) {
            // UC2.1: Kiểm tra va chạm biên (Lấn sang trái, phải hoặc lọt thỏm dưới đáy)
            if (point.x < 0 || point.x >= width || point.y >= height) {
                return false;
            }

            // UC2.2: Kiểm tra va chạm với gạch cũ trên bàn chơi
            if (point.y >= 0 && grid[point.y][point.x] != 0) {
                return false;
            }
        }
        return true;
    }

    // UC2.3 (Khóa khối gạch)
    public void lockPiece(Tetromino p) {
        int currentX = p.getX();
        int currentY = p.getY();

        List<Point> points = p.getCoordinates(currentX, currentY);

        for (Point point : points) {
            if (point.y >= 0 && point.y < height && point.x >= 0 && point.x < width) {
                grid[point.y][point.x] = p.getColor().getRGB();
            }
        }
    }


    // UC2.4 (Xoay đá tường - Wall Kick)
    public Point getWallKickOffset(Tetromino rotatedPiece, int currentX, int currentY, int[][] offsetsToTest) {
        if (offsetsToTest == null || offsetsToTest.length == 0) {
            return null;
        }

        for (int[] offset : offsetsToTest) {
            int testX = currentX + offset[0];
            int testY = currentY + offset[1];

            if (isValidMove(rotatedPiece, testX, testY)) {
                return new Point(offset[0], offset[1]);
            }
        }
        return null;
    }

    public List<Integer> scanFullLines() {
        return null;
    }

    public void clearAndShift(List<Integer> lines) {

    }

    public int[][] getGrid() {
        return null;
    }
}
