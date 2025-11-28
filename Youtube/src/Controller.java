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

    public User getUser() {
        return user;
    }

    public boolean register(String email, String username, String password) throws SQLException {
        user = User.register(email, username, password);
        return user != null;
    }
}
