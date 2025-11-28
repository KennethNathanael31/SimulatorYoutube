import java.util.*;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.SQLException;
import java.time.LocalTime;

public class Video {
    public void restoreVideo() throws SQLException {
        String query = """
                    UPDATE Video
                    SET statusVideo = 'A'
                    WHERE idVideo = ?
                """;
        // ... implementasi restore
    }

    public void deleteVideo(User user) throws SQLException {
        // Soft delete: update status video ke 'B'
        String query = """
                    UPDATE Video
                    SET statusVideo = 'B'
                    WHERE idVideo = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, this.idVideo);
        MainApp.konektor.updateTable(ps);

        // Insert ke tabel Hapus
        query = """
                    INSERT INTO Hapus (idVideo, idPengguna, idKanal, hapus_tanggal)
                    VALUES (?, ?, ?, ?)
                """;
        ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, this.idVideo);
        ps.setInt(2, user.getIdPengguna());
        ps.setInt(3, getIdKanal());
        ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
        MainApp.konektor.updateTable(ps);

        System.out.printf("Video \"%s\" succesfully deleted by %s\n", getVideoNama(), user.getNamaPengguna());
    }
}