import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {
    // METHOD UPLOAD - BARIS 68-71
    public void uploadVideo(Video video) throws SQLException {
        channel.uploadVideo(video);
        publicList.addVideo(video);
    }

    public void play(Scanner sc, Video video) throws SQLException {
        video.play(user, sc);
    }

    // METHOD SELECT AND PLAY - BARIS 87-89
    public void selectAndPlay(int index, Scanner sc) throws SQLException {
        publicList.selectAndPlay(user, index, sc);
    }
}
