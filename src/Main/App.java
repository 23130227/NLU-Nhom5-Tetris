package Main;

import controller.GameController;
import controller.InputHandler;
import model.GameModel;
import view.GameGUI;

public class App {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        model.spawnNewPiece();
        GameGUI gui = new GameGUI(model);
        GameController controller = new GameController(model, gui);

        InputHandler inputHandler = new InputHandler(controller);
        gui.getMainFrame().addKeyListener(inputHandler);
        gui.getMainFrame().requestFocus();

        controller.startGame();
    }
}
