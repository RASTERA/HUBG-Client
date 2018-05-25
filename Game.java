// Some game magic bs

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Game extends GeiPanel {

    private BufferedImage background;
    private String loadingMessage = "Game is in progress...";

    public Game(Main parent) {
        this.parent = parent;
        this.parent.setMasterTimer(50);
        this.constantUpdate = false;

        try {
            this.background = ImageIO.read(new File("images/menu-background.png"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.repaint();
        System.out.println("IM ALIVE!!!!");
    }

    public void paintComponent(Graphics graphics) {

        this.parent.updateFrameRate();

        Graphics2D g = (Graphics2D) graphics;

        int dimension = Math.max(getHeight(), getWidth());
        g.drawImage(background, 0, 0, dimension, dimension, this);

        g.setColor(Color.WHITE);
        g.setFont(Main.getFont("Lato-Light", 30));
        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 30));
        g.drawString(loadingMessage, getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, getHeight() / 2 - metrics.getHeight() / 2);


    }

}