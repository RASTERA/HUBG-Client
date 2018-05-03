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
    private int tileSize = 10;
    private float x = 2500;
    private float y = 2500;
    private float rotation = 0;

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

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.BLUE);

        Tile tile;

        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                tile = map[x][y];

                g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }

    }


}