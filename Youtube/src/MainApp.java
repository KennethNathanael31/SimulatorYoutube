import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MainApp {
    

    /*
     * Page untuk melihat detail channel grup
     * Pengguna dapat mengupload, melihat upload, menghapus video, edit video, melihat 
     * detail channel, melihat laporan, dan mengatur member yang masuk ke dalam grup
     */
    public static void myChannelGroupPage(Scanner sc) throws SQLException {
        System.out.println();
        while (true) {
            System.out.println("=== MY CHANNEL PAGE ===");
            printCommand(new String[] {"Upload Video", "See Uploaded Videos", "Remove Video", "Edit Video", "Channel Detail", 
                            "View Report", "Edit Member","Edit Channel", "Back"});
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Video Title: ");
                    String title = sc.nextLine();

                    System.out.print("Video Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Video Duration (in seconds): ");
                    int duration = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Video File URL (https://www.youtube.com/watch?v=Bwgnw0muVlY): ");
                    String videoPath = sc.nextLine();

                    System.out.print("Subtitle Text File Path (C:\\Users\\vandy\\Downloads\\subtitle.txt): ");
                    String subPath = sc.nextLine();
                    Video newVid = new Video(title, duration,  desc, subPath, videoPath, controller.getUser().getIdPengguna(),controller.getChannel().getIdKanal());
                    controller.uploadVideo(newVid);
                    break;

                case 2:
                    controller.printAllVideos();
                    break;

                case 3:
                    controller.printAllVideos();
                    System.out.print("Enter video index to remove: ");
                    int removeIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    controller.removeVideo(removeIdx);
                    break;

                case 4:
                    controller.printAllVideos();
                    System.out.print("Enter video index to edit: ");
                    int editIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    editVideoMenu(sc, editIdx, controller.getChannel());
                    break;

                case 5:
                    controller.getChannelDetail(sc);
                    break;

                case 6:
                    reportPage(sc, controller.getChannel());
                    break;

                case 7:
                    editMemberMenu(sc, (ChannelGrup) controller.getChannel());
                    break;
                case 8:
                    EditChannelPage(sc, controller.getChannel());
                    break;
                case 9:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    /*
     * Page untuk melihat detail channel individu
     * Pengguna dapat mengupload, melihat upload, menghapus video, edit video, melihat 
     * detail channel, dan melihat laporan
     */
    public static void myChannelPage(Scanner sc) throws SQLException {
        User user = controller.getUser();
        Channel myChannel = controller.getChannel();
        while (true) {
            System.out.println("==== MY CHANNEL PAGE ====");
            printCommand(new String[] {"Upload Video", "See Uploaded Videos", "Remove Video", "Edit Video", "Channel Detail", 
                            "View Report","Edit Channel", "Back"});
            System.out.print("Pick your action: ");
            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Video Title: ");
                    String title = sc.nextLine();

                    System.out.print("Video Description: ");
                    String desc = sc.nextLine();

                    System.out.print("Video Duration (in seconds): ");
                    int duration = sc.nextInt();
                    sc.nextLine();

                    System.out.print("Video File URL (https://www.youtube.com/watch?v=Bwgnw0muVlY): ");
                    String videoPath = sc.nextLine();

                    System.out.print("Subtitle Text File Path (C:\\Users\\vandy\\Downloads\\subtitle.txt): ");
                    String subPath = sc.nextLine();

                    Video newVid = new Video(title, duration, desc, subPath, videoPath, user.getIdPengguna(),myChannel.getIdKanal());
                    controller.uploadVideo(newVid);
                    break;

                case 2:
                    controller.printAllVideos();
                    break;

                case 3:
                    controller.printAllVideos();
                    System.out.print("Enter video index to remove: ");
                    int removeIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    controller.removeVideo(removeIdx);
                    break;

                case 4:
                    controller.printAllVideos();
                    System.out.print("Enter video index to edit: ");
                    int editIdx = sc.nextInt() - 1;
                    sc.nextLine();
                    editVideoMenu(sc, editIdx, controller.getChannel());
                    break;

                case 5:
                    controller.getChannelDetail(sc);
                    break;

                case 6:
                    reportPage(sc, controller.getChannel());
                    break;
                case 7:
                    EditChannelPage(sc, controller.getChannel());
                    break;
                case 8:
                    return; // Back to HomePage
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

}
