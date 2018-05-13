import java.net.URI;
import java.awt.*;

public class Rah {
    public static Message messageBuilder(int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

        return nMessage;
    }

    public static void webbrowserOpen(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        }
        catch (Exception error) {
            error.printStackTrace();
        }
    }
}
