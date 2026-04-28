import model.GameModel;
import view.GameGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Khởi tạo Model
            GameModel model = new GameModel();

            // 2. Khởi tạo GUI và truyền model vào
            GameGUI gui = new GameGUI(model);

            // 3. Gọi refresh để vẽ lần đầu


            Timer timer = new Timer(500, e -> {
                model.getCurrentPiece().move(0, 1); // Cho rơi xuống
                // model.getCurrentPiece().rotate(); // Hoặc cho xoay liên tục để test
                gui.refresh();
            });
            timer.start();

            System.out.println("Giao diện đã khởi chạy thành công!");
        });
    }
}
