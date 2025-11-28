import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ChannelGrup extends Channel {
   // OVERRIDE DENGAN CEK HAK AKSES - BARIS 185-191
@Override
public void uploadVideo(Video video) throws SQLException {
    if (getTingkatAkses(video.getIdPengguna()) > 4) {
        System.out.println("You dont have access");
        return;
    }
    super.uploadVideo(video);
}

}
