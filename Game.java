// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Game extends JPanel implements KeyListener {

    private Main parent;
    private Tile[][] map = new Tile[5000][5000];
    private int tileSize = 30;
    private float x = 2500;
    private float y = 2500;
    private double rotation = 50;

    private class MenuBar {
        public int x, y, w, h;

        // Bottom menu bar
    }

    public Game(Main parent) {
        this.parent = parent;

        addKeyListener(this);
        setFocusable(true);
        /*
        for (int x = 0; x < 5000; x++) {
            for (int y = 0; y < 5000; y++) {
                map[x][y] = new Tile(Tile.Types.GRASS);
            }
        }*/

    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        System.out.println("geii" + rotation);

        if (e.getKeyCode() == e.VK_D) {
            rotation += 0.5;
            ///System.out.println(rotation + " is da angle");
        }

        if (e.getKeyCode() == e.VK_A) {
            rotation -= 0.5;
            ///////System.out.println(rotation + " is da angle");
        }

        // this.repaint();
    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics graphics) {
        System.out.println(rotation);
        super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.RED);
        g.fillRect(0, 0, Main.w, Main.h);

        g.rotate(Math.toRadians(rotation), Main.w / 2, Main.h / 2);

        for (int x = 0; x < 50; x++) {
            for (int y = 0; y < 50; y++) {
                g.setColor(new Color(x * 2, y * 2, x + y));
                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }


        /*
        Tile tile;

        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                tile = map[x][y];

                g.setColor(new Color(x / 2, y / 2, 0));

                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }*/

        g.rotate(Math.toRadians(-1 * rotation), Main.w / 2, Main.h / 2);

        g.setColor(Color.RED);
        g.fillRect(400, 400, 50, 50);

    }


}