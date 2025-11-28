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

    public boolean sudahMenonton(User user) throws SQLException {
        String query = "SELECT 1 FROM Nonton WHERE idPengguna = ? AND idVideo = ?";
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setInt(1, user.getIdPengguna());
        ps.setInt(2, this.idVideo);
        ResultSet rs = MainApp.konektor.getTable(ps);
        return rs.next();
    }

    // INSERT/UPDATE RECORD NONTON - BARIS 546-573
    public void insertOrUpdateNonton(User user) throws SQLException {
        if (user == null) {
            return;
        }
        if (sudahMenonton(user)) {
            String updateQuery = """
                        UPDATE Nonton
                        SET tanggal_nonton = ?
                        WHERE idPengguna = ? AND idVideo = ?
                    """;
            PreparedStatement updatePs = MainApp.konektor.getConnection().prepareStatement(updateQuery);
            updatePs.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            updatePs.setInt(2, user.getIdPengguna());
            updatePs.setInt(3, this.idVideo);
            MainApp.konektor.updateTable(updatePs);
        } else {
            String insertQuery = """
                        INSERT INTO Nonton (idPengguna, idVideo, tanggal_nonton)
                        VALUES (?, ?, ?)
                    """;
            PreparedStatement insertPs = MainApp.konektor.getConnection().prepareStatement(insertQuery);
            insertPs.setInt(1, user.getIdPengguna());
            insertPs.setInt(2, this.idVideo);
            insertPs.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            MainApp.konektor.updateTable(insertPs);
        }
    }

    // METHOD PLAY VIDEO - BARIS 575-620
    public void play(User user, Scanner sc) throws SQLException {
        // masukkan ke Nonton
        insertOrUpdateNonton(user);

        try {
            Desktop.getDesktop().browse(new URI(videoPath));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int check = -1;
        while (true) {
            // Simulate video screen
            System.out.println("#####################################");
            System.out.printf("# Now Playing: %-25s #\n", videoNama);
            System.out.println("#####################################");
            System.out.println("1. Like");
            System.out.println("2. Dislike");
            System.out.println("3. Comment");
            System.out.println("4. Subscribe Channel");
            System.out.println("5. See Detail");
            System.out.println("6. Back");
            System.out.print("Choose action: ");
            String choice = sc.nextLine();
            System.out.println();

            switch (choice) {
                case "1": // Like
                case "2": // Dislike
                case "3": // Comment
                case "4": // Subscribe
                case "5": // Detail
                case "6": // Exit
                    return;
            }
        }
    }

    // IMPORT DATA NONTON - BARIS 408-424
    public void importNonton() throws SQLException {
        String query = """
                    SELECT
                        p.email,
                        p.namaPengguna
                    FROM
                        (SELECT
                            n.idPengguna
                        FROM
                            Nonton n
                        WHERE
                            n.idVideo = ?) AS nontonList
                    INNER JOIN Pengguna p ON nontonList.idPengguna = p.idPengguna
                """;
        PreparedStatement nontonPs = MainApp.konektor.getConnection().prepareStatement(query);
        nontonPs.setInt(1, idVideo);
        ResultSet nontonRs = MainApp.konektor.getTable(nontonPs);
        while (nontonRs.next()) {
            this.viewers.add(new User(nontonRs.getString(1), nontonRs.getString(2)));
        }
        viewsCount = viewers.size();
    }
}