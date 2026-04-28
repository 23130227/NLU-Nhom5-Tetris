package model;

import java.awt.*;
import java.util.List;

public class Tetromino {
    private int[][] matrix;
    private int x, y;
    private Color color;

    public Tetromino(TetrominoType type) {
        this.matrix = type.matrix;
        this.color = type.color;
        // Vị trí mặc định: Giữa phía trên board
        this.x = 3;
        this.y = 0;
    }

    public void move(int dx, int dy) {

    }

    public void rotate() {

    }

    public List<Point> getCoordinates(int targetX, int targetY) {
        return null;
    }

    public int[][] getMatrix() {
        return matrix;
    }

    public Color getColor() {
        return color;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
