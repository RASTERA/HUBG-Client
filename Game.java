// Some game magic bs

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

class Game extends GeiPanel {

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

        int dimension = Math.max(this.getHeight(), this.getWidth());
        g.drawImage(this.background, 0, 0, dimension, dimension, this);

        g.setColor(Color.WHITE);
        g.setFont(Main.getFont("Lato-Light", 30));
        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 30));
        g.drawString(this.loadingMessage, this.getWidth() / 2 - metrics.stringWidth(this.loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);


    }

}