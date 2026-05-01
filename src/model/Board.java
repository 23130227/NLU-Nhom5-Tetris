package model;

import java.util.List;

public class Board {
    private int[][] grid;
    private int width;
    private int height;

    public Board(int[][] grid, int width, int height) {
        this.grid = new int[height][width];
        this.width = 10;
        this.height = 20;
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
        return null;
    }
}
