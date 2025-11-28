import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {
    private User user;

    private Controller() {
        publicList = new VideoList();
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public boolean makeChannel(String nama, String deskripsi, int tipeChannel) throws SQLException {
        channel = Channel.makeChannel(user.getIdPengguna(), nama, deskripsi, tipeChannel);
        channel.import_ID_for_NewChannel(nama, deskripsi, tipeChannel);
        channel.exportChannel();

        return channel != null;
    }
  
    public User getUser() {
        return user;
    }

    public boolean login(String email, String password) throws SQLException {
        user = User.login(email, password);
        return user != null;
    }
}
