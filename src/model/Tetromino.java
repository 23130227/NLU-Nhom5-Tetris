package model;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class Tetromino {
    private int[][] matrix;
    private int x, y;
    private Color color;

    public void move(int dx, int dy) {

    }

    public void rotate() {

    }

    public List<Point> getCoordinates(int targetX, int targetY) {
        List<Point> points = new ArrayList<>();

        if (matrix == null) {
            return points;
        }
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] != 0) {
                    points.add(new Point(targetX + col, targetY + row));
                }
            }
        }

        return points;
    }

    public int[][] getMatrix() {
        return null;
    }

    public Color getColor() {
        return null;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
