package controller;

import model.Board;
import model.GameModel;
import model.Tetromino;
import view.GameGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class GameController {
    private Timer gameTimer;
    private GameModel model;
    private GameGUI view;

    public GameController(GameModel model, GameGUI view){
        this.model = model;
        this.view = view;
    }
    public void startGame(){
        gameTimer = new Timer(500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameLoop();
            }
        });
        gameTimer.start();
    }

    public void gameLoop(){
        Tetromino current = model.getCurrentPiece();
        Board board = model.getBoard();

        if (board.isValidMove(current, current.getX(), current.getY() + 1)) {

            current.move(0, 1);
        } else {
            board.lockPiece(current);
            model.spawnNewPiece();
        }
        view.refresh();
    }

    public void pauseGame(){
        if(gameTimer != null && gameTimer.isRunning()){
            gameTimer.stop();
        }
    }
}
