package model;

import java.awt.*;

public enum TetrominoType {
    I(new int[][]{{0,0,0,0}, {1,1,1,1}, {0,0,0,0}, {0,0,0,0}}, new Color(0, 255, 255)),
    J(new int[][]{{1,0,0}, {1,1,1}, {0,0,0}}, new Color(0, 0, 255)),
    L(new int[][]{{0,0,1}, {1,1,1}, {0,0,0}}, new Color(255, 165, 0)),
    O(new int[][]{{1,1}, {1,1}}, new Color(255, 255, 0)),
    S(new int[][]{{0,1,1}, {1,1,0}, {0,0,0}}, new Color(0, 255, 0)),
    T(new int[][]{{0,1,0}, {1,1,1}, {0,0,0}}, new Color(128, 0, 128)),
    Z(new int[][]{{1,1,0}, {0,1,1}, {0,0,0}}, new Color(255, 0, 0));

    public final int[][] matrix;
    public final Color color;

    TetrominoType(int[][] matrix, Color color) {
        this.matrix = matrix;
        this.color = color;
    }
}
