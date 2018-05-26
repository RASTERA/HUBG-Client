import java.net.URI;
import java.awt.*;

class Rah {
    public static void webbrowserOpen(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public static void drawCenteredString(Graphics g, String text, int x, int y) {
        FontMetrics metric = g.getFontMetrics();

        int xc = x - metric.stringWidth(text) / 2;

        g.drawString(text, xc, y);
    }
}
