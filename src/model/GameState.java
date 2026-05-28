package model;

/**
 * {@code GameState} là một Enum định nghĩa các trạng thái vòng đời của trò chơi.
 *
 * <p>Được sử dụng trong {@link GameModel} để kiểm soát luồng của game,
 * giúp Controller và View biết nên cập nhật logic hay vẽ màn hình nào.
 */
public enum GameState {
    /** * Trạng thái khi vừa mở game hoặc đang ở màn hình chờ (Menu).
     * Game chưa bắt đầu.
     */
    MENU,

    /** * Trạng thái game đang diễn ra bình thường.
     * Gạch đang rơi và người chơi có thể điều khiển.
     */
    PLAYING,

    /** * Trạng thái tạm dừng trò chơi.
     * Vòng lặp game (Timer) bị dừng tạm thời, mọi hành động điều khiển bị vô hiệu hóa.
     */
    PAUSED,

    /** * Trạng thái kết thúc trò chơi.
     * Xảy ra khi gạch xếp cao chạm nóc bảng (không thể sinh thêm khối mới).
     */
    GAME_OVER
}