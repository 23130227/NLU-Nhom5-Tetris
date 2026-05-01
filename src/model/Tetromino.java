package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Tetromino {
    private int[][] matrix;
    private int x, y;
    private Color color;
    private int type;

    public Tetromino(int type) {
        this.type = type;
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
        this.x += dx;
        this.y += dy;
    }

    public void rotate() {
        int size = matrix.length;
        int[][] rotatedMatrix = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                rotatedMatrix[j][size - 1 - i] = matrix[i][j];
            }
        }
        this.matrix = rotatedMatrix;
    }

    public void printMatrix() {
        for (int[] row : matrix) {
            for (int cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    public List<Point> getCoordinates(int targetX, int targetY) {
        List<Point> points = new ArrayList<>();
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (matrix[row][col] != 0) {
                    points.add(new Point(targetX + col, targetY + row));
                }
            }
        }
        return points;
    }

    public List<Point> getCoordinates() {
        return getCoordinates(x, y);
    }

    public void printCoords() {
        for (Point p : getCoordinates()) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }

    public int[][] getMatrix() {
        return this.matrix;
    }

    public Color getColor() {
        return this.color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getType() {return type;}

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
