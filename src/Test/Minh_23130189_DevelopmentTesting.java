package Test;

import controller.GameController;
import model.GameModel;
import model.Tetromino;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import view.GameGUI;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Development Testing cho UC-04.4 Hard Drop
 *
 * Thực hiện bởi:
 * Đoàn Quang Minh - MSSV: 23130189
 */
public class Minh_23130189_DevelopmentTesting {

    private GameModel model;
    private GameController controller;
    private GameGUI view;

    @BeforeEach
    public void setUp() {

        model = new GameModel();
        view = new GameGUI(model);
        controller = new GameController(model, view);

        controller.startOrResetGame();
    }

    /**
     * TC01:
     * Hard Drop phải đưa khối gạch xuống thấp hơn vị trí ban đầu.
     */
    @Test
    public void testUC044_HardDropMovesPieceDownward() {

        System.out.println("\n[RUNNING TEST] -> testUC044_HardDropMovesPieceDownward");

        Tetromino piece = model.getCurrentPiece();

        assertNotNull(piece);

        int beforeY = piece.getY();

        System.out.println("   [*] Y trước Hard Drop = " + beforeY);

        controller.hardDrop();

        int afterY = piece.getY();

        System.out.println("   [*] Y sau Hard Drop = " + afterY);

        assertTrue(afterY > beforeY,
                "Hard Drop phải đưa khối gạch xuống thấp hơn");

        System.out.println("   [=> SUCCESS] Hard Drop hoạt động chính xác!");
    }

    /**
     * TC02:
     * Sau Hard Drop phải khóa gạch cũ và sinh gạch mới.
     */
    @Test
    public void testUC044_NewPieceSpawnedAfterHardDrop() {

        System.out.println("\n[RUNNING TEST] -> testUC044_NewPieceSpawnedAfterHardDrop");

        Tetromino oldPiece = model.getCurrentPiece();

        assertNotNull(oldPiece);

        System.out.println("   [*] Gạch hiện tại ID = "
                + oldPiece.getType());

        controller.hardDrop();

        Tetromino newPiece = model.getCurrentPiece();

        assertNotNull(newPiece);

        System.out.println("   [*] Gạch mới ID = "
                + newPiece.getType());

        assertNotSame(oldPiece, newPiece,
                "Sau Hard Drop phải sinh ra khối gạch mới");

        System.out.println("   [=> SUCCESS] Sinh gạch mới thành công!");
    }

    /**
     * TC03:
     * Hard Drop không được làm Game Over
     * trong điều kiện bình thường.
     */
    @Test
    public void testUC044_GameStillPlayingAfterHardDrop() {

        System.out.println("\n[RUNNING TEST] -> testUC044_GameStillPlayingAfterHardDrop");

        controller.hardDrop();

        assertNotNull(model.getCurrentPiece());

        System.out.println("   [*] Khối gạch mới đã xuất hiện.");

        System.out.println("   [=> SUCCESS] Game tiếp tục hoạt động bình thường!");
    }
}