package audio;

import javax.sound.sampled.*;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * {@code SoundManager} chịu trách nhiệm quản lý toàn bộ âm thanh trong game.
 *
 * <p>Hiện tại lớp này hỗ trợ:
 * <ul>
 *     <li>Phát nhạc nền (Background Music).</li>
 *     <li>Dừng nhạc nền.</li>
 * </ul>
 *
 * <p>Trong tương lai có thể mở rộng thêm:
 * <ul>
 *     <li>Âm thanh xoay khối (Rotate Sound).</li>
 *     <li>Âm thanh xóa dòng (Clear Line Sound).</li>
 *     <li>Âm thanh Game Over.</li>
 * </ul>
 *
 * <p>Lớp này được tách riêng khỏi Controller nhằm tuân thủ nguyên tắc
 * phân tách trách nhiệm (Separation of Concerns):
 * Controller chỉ điều phối hành động,
 * còn việc xử lý âm thanh sẽ được giao hoàn toàn cho SoundManager.
 */
public class SoundManager {
    /**
     * Đối tượng {@link Clip} dùng để phát nhạc nền.
     *
     * <p>Clip cho phép:
     * <ul>
     *     <li>Phát âm thanh.</li>
     *     <li>Dừng phát.</li>
     *     <li>Phát lặp vô hạn.</li>
     * </ul>
     */
    private Clip clip;

    /**
     * Phát nhạc nền từ một file âm thanh.
     *
     * <p>Nếu nhạc đang phát thì phương thức sẽ không làm gì,
     * tránh trường hợp người chơi nhấn phím nhiều lần làm chồng nhiều bản nhạc.
     *
     * <p>Nhạc sẽ được phát lặp vô hạn bằng:
     * <pre>
     * Clip.LOOP_CONTINUOUSLY
     * </pre>
     *
     * path đường dẫn tới file âm thanh (.wav)
     */
    public void play(String fileName) {
        try {
            // Nếu nhạc đang phát thì bỏ qua
            if(clip!=null && clip.isRunning()) {
                return;
            }
            // Đọc dữ liệu âm thanh từ file
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName));
            // Tạo Clip mới để chứa dữ liệu âm thanh
            clip = AudioSystem.getClip();
            // Nạp dữ liệu âm thanh vào Clip
            clip.open(audio);
            // Thiết lập phát lặp vô hạn
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            // Bắt đầu phát nhạc
            clip.start();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Dừng nhạc nền đang phát.
     *
     * <p>Nếu chưa có nhạc nào được phát thì phương thức sẽ không làm gì.
     */
    public void stopMusic() {
        if (clip != null) {
            clip.stop();
        }
    }
    /**
     * Kiểm tra trạng thái phát nhạc hiện tại.
     *
     * @return {@code true} nếu nhạc đang phát,
     *         {@code false} nếu nhạc đang dừng hoặc chưa được khởi tạo.
     */
    public boolean isMusicPlaying() {
        return clip != null && clip.isRunning();
    }
    /**
     * Phát hiệu ứng âm thanh (SFX) một lần duy nhất.
     * Tạo một đối tượng Clip cục bộ để không làm đụng độ nhạc nền đang phát.
     *
     * @param fileName đường dẫn tới file âm thanh (.wav)
     */
    public void playSFX(String fileName) {
        try {
            // Khởi tạo luồng âm thanh mới
            AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName));
            Clip sfxClip = AudioSystem.getClip();
            sfxClip.open(audio);

            // start() chỉ phát 1 lần rồi tự kết thúc, KHÔNG dùng loop()
            sfxClip.start();

        } catch (Exception e) {
            System.err.println("Lỗi phát âm thanh Combo: " + e.getMessage());
        }
    }
}
