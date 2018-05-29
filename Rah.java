import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.awt.*;
import Decoder.BASE64Decoder;

class Rah {
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            Main.errorQuit(e);
        }
        return image;
    }

    public static String getTimestamp(long time) {
        long difference = (System.currentTimeMillis() - time) / 1000L;

        if (difference < 0) {
            return "From the future???";
        } else if (difference < 5) {
            return "Just now";
        } else if (difference < 60) {
            return difference + " seconds ago";
        } else if (difference < 3600) {
            return difference / 60L + String.format(" minute%s ago", difference / 60L > 1 ? "s" : "");
        } else if (difference < 86400) {
            return difference / 3600L + String.format(" hour%s ago", difference / 3600L > 1 ? "s" : "");
        } else if (difference < 604800) {
            return difference / 86400L + String.format(" day%s ago", difference / 86400L > 1 ? "s" : "");
        } else if (difference < 2592000) {
            return difference / 604800L + String.format(" week%s ago", difference / 604800L > 1 ? "s" : "");
        } else if (difference < 31536000) {
            return difference / 2592000L + String.format(" month%s ago", difference / 2592000L > 1 ? "s" : "");
        } else {
            return difference / 31536000L + String.format(" year%s ago", difference / 31536000L > 1 ? "s" : "");
        }
    }

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
