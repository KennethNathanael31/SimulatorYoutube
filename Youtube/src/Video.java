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
    private String videoNama;
    private String videoDeskripsi;
    private String videoSubtitle;
    private String maker;
    private int idKanal;
    private int videoDurasi;
    private int viewsCount;
    private int idPengguna;
    private int idVideo;
    private char statusVideo;
    private Date uploadDate;
    private String videoPath;
    private List<User> likes = new ArrayList<>();
    private List<User> dislikes = new ArrayList<>();
    private List<User> viewers = new ArrayList<>();
    protected List<Comment> commentList = new ArrayList<>(); // gtni

    public Video(int idVideo, int idKanal, String videoNama, int videoDurasi, int viewsCount, String videoDeskripsi,
            String videoSubtitle, String maker, String videoPath, char statusVideo) {
        this.idVideo = idVideo;
        this.idKanal = idKanal;
        this.maker = maker;
        this.videoNama = videoNama;
        this.videoDurasi = videoDurasi;
        this.viewsCount = viewsCount;
        this.videoDeskripsi = videoDeskripsi;
        this.videoSubtitle = videoSubtitle;
        this.videoPath = videoPath;
        this.statusVideo = statusVideo;
    }

    public char getStatusVideo() {
        return statusVideo;
    }

    public void setStatusVideo(char statusVideo) {
        this.statusVideo = statusVideo;
    }

    public int getViews() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public void setLikes(List<User> likes) {
        this.likes = likes;
    }

    public void setDislikes(List<User> dislikes) {
        this.dislikes = dislikes;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public String getVideoNama() {
        return videoNama;
    }

    public void setVideoNama(String videoNama, User user) throws SQLException {
        this.videoNama = videoNama;

        String query = """
                    UPDATE Video
                    SET videoNama = ?
                    WHERE idVideo = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, videoNama);
        ps.setInt(2, this.idVideo);
        MainApp.konektor.updateTable(ps);
    }

    public void setvideoDurasi(int videoDurasi) throws SQLException {
        this.videoDurasi = videoDurasi;
    }

    public void setVideoDescription(String videoDeskripsi, User user) throws SQLException {
        this.videoDeskripsi = videoDeskripsi;
        String query = """
                    UPDATE Video
                    SET videoDeskripsi = ?
                    WHERE idVideo = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, videoDeskripsi);
        ps.setInt(2, this.idVideo);
        MainApp.konektor.updateTable(ps);
    }

    public void setVideoSubtitle(String videoSubtitle, User user) throws SQLException {
        this.videoSubtitle = videoSubtitle;

        String query = """
                    UPDATE Video
                    SET videoSubtitle = ?
                    WHERE idVideo = ?
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, videoSubtitle);
        ps.setInt(2, this.idVideo);
        MainApp.konektor.updateTable(ps);
    }

    public int getVideoDurasi() {
        return videoDurasi;
    }

    public void setVideoDurasi(int videoDurasi) {
        this.videoDurasi = videoDurasi;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public String getVideoDeskripsi() {
        return videoDeskripsi;
    }

    public void setVideoDeskripsi(String videoDeskripsi) {
        this.videoDeskripsi = videoDeskripsi;
    }

    public String getvideoSubtitle() {
        return videoSubtitle;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public List<User> getLikes() {
        return likes;
    }

    public List<User> getDislikes() {
        return dislikes;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public int getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(int idVideo) {
        this.idVideo = idVideo;
    }

    public int getIdKanal() {
        return idKanal;
    }

    public void setIdKanal(int idKanal) {
        this.idKanal = idKanal;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getDisDisLikedList() {
        return dislikes.size();
    }

    public void getComment() {
        for (Comment comment : commentList) {
            System.out.println(comment);
        }
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public int getViewCount() {
        return viewers.size();
    }

    public List<User> getViewers() {
        return viewers;
    }

    public void setViewers(List<User> viewers) {
        this.viewers = viewers;
    }

    @Override
    public String toString() {
        String video = String.format("%-30s %-10d %-10d %-20s", videoNama, videoDurasi, viewsCount, maker);
        return video;
    }
// CONSTRUCTOR UNTUK VIDEO BARU - BARIS 27-37
public Video(String videoNama, int videoDurasi, String videoDeskripsi, String videoSubtitle, String videoPath,
        int idPengguna, int idKanal) {
    this.videoNama = videoNama;
    this.videoDurasi = videoDurasi;
    this.videoDeskripsi = videoDeskripsi;
    this.videoSubtitle = videoSubtitle;
    this.videoPath = videoPath;
    this.idPengguna = idPengguna;
    this.viewsCount = 0;
    this.idKanal = idKanal;
}

// EXPORT VIDEO KE DATABASE - BARIS 645-677
public void exportDBVideo() throws SQLException {
    // Simpan ke database Video
    String insertVideoQuery = """
                INSERT INTO Video (videoNama, videoDurasi, videoDeskripsi, videoSubtitle, videoPath, statusVideo, idKanal)
                VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    PreparedStatement psVideo = MainApp.konektor.getConnection().prepareStatement(insertVideoQuery);
    psVideo.setString(1, getVideoNama());
    psVideo.setInt(2, getVideoDurasi());
    psVideo.setString(3, getVideoDeskripsi());
    psVideo.setString(4, getvideoSubtitle());
    psVideo.setString(5, getVideoPath());
    psVideo.setString(6, "A");
    psVideo.setInt(7, getIdKanal());

    MainApp.konektor.updateTable(psVideo);
    String query = """
                SELECT TOP 1 idVideo
                FROM Video
                ORDER BY idVideo DESC
            """;

    PreparedStatement cVideo = MainApp.konektor.getConnection().prepareStatement(query);
    ResultSet rs = MainApp.konektor.getTable(cVideo);

    if (rs.next()) {
        setIdVideo(rs.getInt("idVideo"));
    }

    exportDBUnggahVideo();
}

// EXPORT KE TABEL UNGGAH - BARIS 679-691
public void exportDBUnggahVideo() throws SQLException {
    String insertUnggahQuery = "INSERT INTO Unggah (idVideo, idPengguna, idKanal, unggah_tanggal) VALUES (?, ?, ?,?)";
    PreparedStatement psUnggah = MainApp.konektor.getConnection().prepareStatement(insertUnggahQuery);
    psUnggah.setInt(1, getIdVideo());
    psUnggah.setInt(2, getIdPengguna());
    psUnggah.setInt(3, getIdKanal());
    psUnggah.setDate(4, new java.sql.Date(System.currentTimeMillis()));
    MainApp.konektor.updateTable(psUnggah);

    System.out.println("Video '" + getVideoNama() + "' uploaded and saved to database!");
}
}