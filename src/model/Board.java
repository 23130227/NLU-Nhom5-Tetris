package model;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private int[][] grid;
    private int width;
    private int height;

    public boolean isValidMove(Tetromino p, int x, int y) {
        return false;
    }

    public void lockPiece(Tetromino p) {

    }

    public List<Integer> scanFullLines() {
        List<Integer> fullLines = new ArrayList<>();
        for (int y = height - 1; y >= 0; y--) {
            boolean isFull = true;
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == 0) {
                    isFull = false;
                    break;
                }
            }
            if (isFull) {
                fullLines.add(y);
            }
        }

        return fullLines;
    }

    public void clearAndShift(List<Integer> lines) {
        if (lines == null || lines.isEmpty()) return;
        int shiftDown = 0;
        for (int y = height - 1; y >= 0; y--){
            if (lines.contains(y)) {
                shiftDown++;
            } else if (shiftDown > 0) {
                for (int x = 0; x < width; x++) {
                    grid[y + shiftDown][x] = grid[y][x]; // Dòng trên đè xuống dòng dưới
                }
            }
        }


        for (int y = 0; y < shiftDown; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = 0;
            }
        }
    }

    public int[][] getGrid() {
        return grid;
    }
}
