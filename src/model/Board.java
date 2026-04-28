package model;

import java.util.List;

public class Board {
    private int[][] grid;
    private int width;
    private int height;

    public Board() {
        // Khởi tạo mảng 2 chiều 20 hàng, 10 cột
        height = 20;
        width = 10;
        grid = new int[height][width];
    }
    public boolean isValidMove(Tetromino p, int x, int y) {
        return false;
    }

    public void lockPiece(Tetromino p) {

    }

    public List<Integer> scanFullLines() {
        return null;
    }

    public void clearAndShift(List<Integer> lines) {

    }

    public int[][] getGrid() {
        return grid;
    }
}
