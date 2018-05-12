public class rah {
    public static Message messageBuilder (int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

public class Rah {
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
