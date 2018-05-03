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

    public Game(Main parent) {
        this.parent = parent;

        addKeyListener(this);
        setFocusable(true);

    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int y = 0; y < 14; y+=2) {
            g.setColor(Color.WHITE);
            g.fillRect(0, y * Main.h / 13, Main.w, Main.h / 13);

            g.setColor(Color.RED);
            g.fillRect(0, (y + 1) * Main.h / 13, Main.w, Main.h / 13);
        }

        g.setColor(Color.BLUE);
        g.fillRect(0, 0, Main.w / 2, Main.h / 2);

    }


}