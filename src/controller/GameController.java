package controller;

import model.GameModel;
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
        model.getCurrentPiece().move(0, 1);
        view.refresh();
    }

    public void pauseGame(){
        if(gameTimer != null && gameTimer.isRunning()){
            gameTimer.stop();
        }
    }
}
