import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class User {
    private int idPengguna;
    protected String email;
    protected String namaPengguna;
    protected ArrayList<Channel> subscribedChannels;
    
    public User(String email, String namaPengguna) throws SQLException {
        this.email = email;
        this.namaPengguna = namaPengguna;
        subscribedChannels = importSubscribed();
    }
    
    public User(int idPengguna, String email, String namaPengguna) throws SQLException {
        this.idPengguna = idPengguna;
        this.email = email;
        this.namaPengguna = namaPengguna;
        subscribedChannels = importSubscribed();
    }

    public String getEmail() {
        return email;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public ArrayList<Channel> getSubscribedChannels() {
        return subscribedChannels;
    }

    public void setIdPengguna(int id){
        this.idPengguna = id;
    }
    
    public int getIdPengguna(){
        return idPengguna;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        User u = (User) o;
        return idPengguna == u.idPengguna;
    }

    public static User register(String email, String namaPengguna, String passwordPengguna) throws SQLException {
        if (checkRegister(email)) {
            return null;
        }

        String query = """
                    INSERT INTO Pengguna (
                        Pengguna.namaPengguna, 
                        Pengguna.passwordPengguna,
                        Pengguna.email,
                        Pengguna.tanggalPembuatanAkun,
                        Pengguna. tipePengguna
                    )
                    VALUES (?, ?, ?, ?, ?)
                """;
        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, namaPengguna);
        ps.setString(2, passwordPengguna);
        ps.setString(3, email);
        ps.setDate(4, Date.valueOf(LocalDate.now()));
        ps.setInt(5, 1);
        MainApp.konektor.updateTable(ps);
        
        ResultSet rs = importUser(email, passwordPengguna);
        rs.next();
        
        return new User(rs.getInt(1), email, namaPengguna);
    }

    private static boolean checkRegister(String email) throws SQLException {
        String query = """
                    SELECT 
                        p.idPengguna
                    FROM 
                        Pengguna p
                    WHERE
                        p.email = ?
                """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, email);

        ResultSet tempPS = MainApp.konektor.getTable(ps); 
        return tempPS.next();
    }
    // HELPER METHOD UNTUK LOGIN - BARIS 127-140
    private static ResultSet importUser(String email, String passwordPengguna) throws SQLException {
        String query = """
                SELECT 
                    Pengguna.idPengguna,
                    Pengguna.email,
                    Pengguna.namaPengguna
                FROM 
                    Pengguna 
                WHERE 
                    email = ? AND 
                    passwordPengguna = ?;
            """;

        PreparedStatement ps = MainApp.konektor.getConnection().prepareStatement(query);
        ps.setString(1, email);
        ps.setString(2, passwordPengguna);

        return MainApp.konektor.getTable(ps);
    }

    // METHOD LOGIN - BARIS 142-147
    public static User login(String email, String passwordPengguna) throws SQLException {
        ResultSet rs = importUser(email, passwordPengguna);
        if (rs.next()) {
            return new User(rs.getInt(1), rs.getString(2), rs.getString(3));
        }
        return null;
    }
}
