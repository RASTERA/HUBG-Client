package com.rastera.hubg.desktop;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.awt.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import Decoder.BASE64Decoder;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.rastera.Networking.Message;

public class Rah {

    public static BitmapFont cloneFont(BitmapFont font) {
        return new BitmapFont(font.getData(), font.getRegion(), font.usesIntegerPositions());
    }

    public static Message messageBuilder(int type, Object message) {
        Message nMessage = new Message();

        nMessage.type = type;
        nMessage.message = message;

        return nMessage;
    }

    public static String stringMultiply(int times, String item){

        return new String(new char[times]).replace("\0", item);  // Creates a String using a string array and replace the blanks
    }

    public static void centerText(Batch batch, BitmapFont font, float size, String text, int x, int y) {

        font.getData().setScale(size);

        GlyphLayout layout = new GlyphLayout(font, text);

        font.draw(batch, text, x - layout.width / 2, y + layout.height / 2);

    }

    public static ImageIcon getScaledIcon(String name) {
        return new ImageIcon(new ImageIcon(name).getImage().getScaledInstance(20, 20,  java.awt.Image.SCALE_SMOOTH));
    }

    // ToDo: Figure out why exception is thrown
    public static Component checkParent(Component parent) {
        try {
            Insets screenInsets = parent.getToolkit().getScreenInsets(parent.getGraphicsConfiguration());

            return parent;
        } catch (Exception e) {
            System.out.println("Window is zero?");
            return null;
        }
    }

    public static ArrayList<String> wrapText(int width, String text, FontMetrics metrics) {
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>(Arrays.asList(text.split(" ")));
        StringBuilder currentLine = new StringBuilder();
        int lineWidth = (int) (width * 0.9);

        while (true) {

            if (words.size() > 0 && metrics.stringWidth(words.get(0)) > lineWidth) {
                words.set(0, words.get(0).substring(0, 6) + "...");
            }

            if (words.size() > 0 && metrics.stringWidth(currentLine + " " + words.get(0)) <= lineWidth) {
                currentLine.append(" ").append(words.get(0));
                words.remove(0);
            } else {
                lines.add(currentLine.toString().trim());
                currentLine = new StringBuilder();

                if (words.size() == 0) {
                    break;
                }
            }
        }

        return lines;
    }

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
