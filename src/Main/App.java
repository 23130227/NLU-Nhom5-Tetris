package Main;

import model.GameModel;
import view.GameGUI;

public class App {
    public static void main(String[] args) {
        GameModel model = new GameModel();
        model.spawnNewPiece();
        GameGUI gui = new GameGUI(model);
    }
}
