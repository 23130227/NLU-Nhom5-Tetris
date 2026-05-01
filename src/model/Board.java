package model;

import java.awt.*;
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

    }

    public List<Integer> scanFullLines() {
        return null;
    }

    public void clearAndShift(List<Integer> lines) {

    }

    public int[][] getGrid() {
        return this.grid;
    }
}
