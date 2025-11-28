import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {
   // METHOD UPLOAD - BARIS 68-71
public void uploadVideo(Video video) throws SQLException {
    channel.uploadVideo(video);
    publicList.addVideo(video);
}
}
