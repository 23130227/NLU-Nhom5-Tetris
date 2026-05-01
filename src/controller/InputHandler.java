package controller;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputHandler extends KeyAdapter {

    private GameController controller;

    public InputHandler(GameController controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();

        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                controller.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                controller.moveRight();
                break;
            case KeyEvent.VK_DOWN:
                controller.moveDown();
                break;
            case KeyEvent.VK_UP:
                controller.rotatePiece();
                break;
            case KeyEvent.VK_SPACE:
                controller.rotatePiece();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            controller.resetSoftDrop();
        }
    }
}