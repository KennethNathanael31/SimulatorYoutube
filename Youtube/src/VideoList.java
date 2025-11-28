import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class VideoList {
    public void selectAndPlay(User user, int index, Scanner sc) throws SQLException {
        index--;
        if (index >= 0 && index < videos.size()) {
            videos.get(index).play(user, sc);
        } else {
            System.out.println("Video tidak ditemukan.");
        }
    }

}