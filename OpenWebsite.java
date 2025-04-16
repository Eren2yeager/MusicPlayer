import java.awt.Desktop;
import java.net.URI;

public class OpenWebsite {
    public static void main(String[] args) {
        try {
            // Replace with your desired URL
            Thread.sleep(2000); // Wait for 5 seconds before opening the URL
            URI url = new URI("http://127.0.0.1:5000/");

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(url);
            } else {
                System.out.println("Desktop is not supported.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}