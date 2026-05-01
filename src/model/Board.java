package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Board {
    private int[][] grid;
    private int width;
    private int height;

    public Board() {
        this.width = 10;
        this.height = 20;
        this.grid = new int[height][width];
    }

    public boolean isValidMove(Tetromino p, int x, int y) {

        List<Point> points = p.getCoordinates(x, y);

        for (Point pt : points) {
            if (pt.x < 0 || pt.x >= width || pt.y >= height) {
                return false;
            }
            if (pt.y >= 0 && grid[pt.y][pt.x] != 0) {
                return false;
            }
        }
        return true;
    }

    public void lockPiece(Tetromino p) {
        List<Point> points = p.getCoordinates();
        for (Point pt : points) {
            if (pt.y >= 0 && pt.y < height && pt.x >= 0 && pt.x < width) {
                grid[pt.y][pt.x] = p.getType() + 1;
            }
        }
    }

    public List<Integer> scanFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int row = height - 1; row >= 0; row--) {
            boolean isFull = true;
            for (int col = 0; col < width; col++) {
                if (grid[row][col] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullLines.add(row);
            }
        }
        return fullLines;
    }

    public void clearAndShift(List<Integer> lines) {

    }

    public int[][] getGrid() {
        return this.grid;
    }
}
