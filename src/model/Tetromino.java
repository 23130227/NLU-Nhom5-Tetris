package model;

import java.awt.*;
import java.util.List;

public class Tetromino {
    private int[][] matrix;
    private int x, y;
    private Color color;

    public Tetromino(int type) {
        this.x = 3;
        this.y = 0;
        switch (type) {
            case 0:
                this.matrix = new int[][]{
                        {0, 0, 0, 0},
                        {1, 1, 1, 1},
                        {0, 0, 0, 0},
                        {0, 0, 0, 0}
                };
                this.color = Color.CYAN;
                break;
            case 1:
                this.matrix = new int[][]{
                        {1, 0, 0},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.BLUE;
                break;
            case 2:
                this.matrix = new int[][]{
                        {0, 0, 1},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.ORANGE;
                break;
            case 3:
                this.matrix = new int[][]{
                        {1, 1},
                        {1, 1}
                };
                this.color = Color.YELLOW;
                break;
            case 4:
                this.matrix = new int[][]{
                        {0, 1, 1},
                        {1, 1, 0},
                        {0, 0, 0}
                };
                this.color = Color.GREEN;
                break;
            case 5:
                this.matrix = new int[][]{
                        {0, 1, 0},
                        {1, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.MAGENTA;
                break;
            case 6:
                this.matrix = new int[][]{
                        {1, 1, 0},
                        {0, 1, 1},
                        {0, 0, 0}
                };
                this.color = Color.RED;
                break;
            default:
                this.matrix = new int[][]{
                        {1, 1},
                        {1, 1}
                };
                this.color = Color.YELLOW;
                break;
        }
    }

    public void move(int dx, int dy) {

    }

    public void rotate() {

    }

    public List<Point> getCoordinates(int targetX, int targetY) {
        return null;
    }

    public int[][] getMatrix() {
        return null;
    }

    public Color getColor() {
        return null;
    }
}
