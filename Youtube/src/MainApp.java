import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainApp {
    public static Controller controller = Controller.getInstance();
    public static ConnectDB konektor = ConnectDB.getInstance();
    public static boolean logOut = false;
    public static void main(String[] args) throws SQLException {
        Scanner sc = new Scanner(System.in);
        int action;
        boolean checkAction = false;

        System.out.println("=== WELCOME TO YOUTUBE ===");
        while (true) {
            printCommand(new String[] { "Register", "Login", "Video Page", "Exit" });
            System.out.print("Pick your Action: ");
            action = sc.nextInt();
            System.out.println();
            switch (action) {
                case 1:
                    checkAction = registerPage(sc);
                    break;
                case 2:
                    checkAction = loginPage(sc);
                    break;
                case 3:
                    videoPage(sc);
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
            if (checkAction) {
                if (controller.importBrandchannel()) {
                    if (controller.importChannel()) {
                        homePageContentCreatorHaveChannel(sc);
                    }
                    else {
                        homePageContentCreatorNoChannel(sc);
                    }
                }
                else {
                    if (controller.importChannel()) {
                    homePageHaveChannel(sc);
                    }
                    else {
                        homePageNoChannel(sc);
                    }
                }
            }
        }
    }

    // PAGE MEMBUAT CHANNEL - BARIS 183-203
    public static boolean makeChannelPage(Scanner sc) throws SQLException {
        System.out.println();
        String nama, deskripsi;
        int tipeChannel;

        System.out.print("Channel Name : ");
        sc.nextLine();
        nama = sc.nextLine();

        System.out.print("Channel Description : ");
        deskripsi = sc.nextLine();

        System.out.println("==== CHANNEL TYPE (Individual = 1 | Group = 2) ====");
        System.out.println("1. Individual");
        System.out.println("2. Group");
        System.out.print("Channel type (1 / 2): ");
        tipeChannel = sc.nextInt();

        return controller.makeChannel(nama, deskripsi, tipeChannel);
    }
    public static boolean loginPage(Scanner sc) throws SQLException {
        System.out.println();
        String email, password;
        boolean check;

        System.out.println("==== LOGIN =====");
        sc.nextLine();

        System.out.print("Email: ");
        email = sc.nextLine();

        System.out.print("Password: ");
        password = sc.nextLine();

        check = controller.login(email, password);

        if (!check) {
            System.out.println("Wrong email or password!\n");
        }
        else {
            System.out.println("Login successfull!\n");
        }
        return check;
    }

    /*
     * Home page bagi pengguna yang belum pernah membuat channel
     * Pengguna dapat membuat channel, menonton video, melihat subscriber, dan log out
     * Jika pengguna membuat channel, akan langsung dialihkan ke home page yang sudah memiliki channel
     */
    public static void homePageNoChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        boolean check = false;

        while (true) {
            printCommand(new String[] { "Create Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    check = makeChannelPage(sc);
                    break;
                case 2:
                    videoPage(sc);
                    break;
                case 3:
                    subscribePage(sc);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }

            if (check) {
                homePageHaveChannel(sc);
                break;
            }
        }
    }
    public static void homePageHaveChannel(Scanner sc) throws SQLException{
        System.out.println();
        System.out.printf("=== WELCOME TO YOUTUBE %s ===\n", controller.getUser().getNamaPengguna());
        while (true) {
            printCommand(new String[] { "My Channel", "Video Page", "Subscription", "Log Out" });
            System.out.print("Pick your Action : ");
            int action = sc.nextInt();
            System.out.println();

            switch (action) {
                case 1:
                    if (controller.getChannel() instanceof ChannelGrup) {
                        myChannelGroupPage(sc);
                    } else {
                        myChannelPage(sc);
                    }
                    break;
                case 2:
                    videoPage(sc);
                    break;
                case 3:
                    subscribePage(sc);
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Command is not valid!");
                    break;
            }
        }
    }

    public static void printCommand(String[] command) {
        System.out.println("==================");
        for (int i = 0; i < command.length; i++) {
            System.out.printf("[%d] %s\n", i + 1, command[i]);
        }
        System.out.println("==================");
    }

    public void getCurrentUser() {
        // return user;
    }
}
