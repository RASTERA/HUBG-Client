// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Game extends JPanel implements KeyListener, ActionListener {

    private Main parent;

    private float x = 0;
    private float y = 0;

    private float vx = 0;
    private float vy = 0;

    private double rotation = 0;
    private double rotationVelocity = 0;

    //private Tile[][] map = new Tile[500][500];
    private int mapWidth = 100;
    private int mapHeight = 100;
    private int tileSize = 20;
    private boolean paused = false;

    private JButton quitGameButton;
    private JButton resumeGameButton;

    private ArrayList<Bullet> bullets = new ArrayList<Bullet>();

    private BufferedImage map;

    private class MenuBar {
        public int x, y, w, h;

        // Bottom menu bar
    }

    public Game(Main parent) {
        this.parent = parent;

        resumeGameButton = new JButton("Resume Game");
        resumeGameButton.setActionCommand("resume");
        resumeGameButton.addActionListener(this);

        quitGameButton = new JButton("Quit Game");
        quitGameButton.setActionCommand("quit");
        quitGameButton.addActionListener(this);

        addKeyListener(this);
        setFocusable(true);

        /*
        for (int x = 0; x < 500; x++) {
            for (int y = 0; y < 500; y++) {
                if (x == 0 && y == 0) {
                    map[x][y] = new Tile(Tile.Types.WALL);
                }
                else {
                    map[x][y] = new Tile((x + y) % 2 == 0 ? Tile.Types.GRASS : Tile.Types.WATER);
                }
            }
        }*/

        /*
        try {
            Scanner stdin = new Scanner(new File("out.rah"));
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    map[x][y] = new Tile(stdin.next());
                    //System.out.println("X:" + x + " Y:" + y + " " + map[x][y]);
                }
            }
        }
        catch (Exception e) {
            System.out.println("No");
        }

        System.out.println("Ya");*/

        try {
            map = ImageIO.read(new File("images/map.png"));
        }
        catch (Exception e) {
            System.out.println("What are you going to do about it?");
        }

    }


    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
            case "resume":
                this.paused = !this.paused;
                break;

            case "quit":
                System.out.println("SWITCH");

                removeKeyListener(this);
                this.parent.startPage(Main.Pages.MENU);

                break;
        }

    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == e.VK_ESCAPE) {
            this.paused = !this.paused;
        }

        if (!this.paused) {
            if (e.getKeyCode() == e.VK_SPACE) {
                bullets.add(new Bullet((int) (x + Main.w / 2), y + getHeight(), 10, -1 * rotation - 90));
            }

            if (e.getKeyCode() == e.VK_Q) {
                this.rotationVelocity += (this.rotationVelocity > 2) ? 0 : 0.5;
            }

            if (e.getKeyCode() == e.VK_E) {
                this.rotationVelocity -= (this.rotationVelocity < -2) ? 0 : 0.5;
            }

            if (e.getKeyCode() == e.VK_W) {
                this.vy += (this.vy > 2) ? 0 : Math.sin(Math.toRadians(90 - rotation));
                this.vx += (this.vx > 2) ? 0 : Math.cos(Math.toRadians(90 - rotation));
            }

            if (e.getKeyCode() == e.VK_S) {
                this.vy -= (this.vy < -2) ? 0 : Math.sin(Math.toRadians(90 - rotation));
                this.vx -= (this.vx < -2) ? 0 : Math.cos(Math.toRadians(90 - rotation));
            }

            if (e.getKeyCode() == e.VK_A) {
                this.vy += (this.vy > 2) ? 0 : Math.sin(Math.toRadians(-1 * rotation));
                this.vx += (this.vx > 2) ? 0 : Math.cos(Math.toRadians(-1 * rotation));
            }

            if (e.getKeyCode() == e.VK_D) {
                this.vy -= (this.vy < -2) ? 0 : Math.sin(Math.toRadians(-1 * rotation));
                this.vx -= (this.vx < -2) ? 0 : Math.cos(Math.toRadians(-1 * rotation));
            }
            /*
            if (e.getKeyCode() == e.VK_W) {
                this.x += Math.cos(Math.toRadians(rotation)) * 10;
                this.y += Math.sin(Math.toRadians(rotation)) * 10;
            }

            if (e.getKeyCode() == e.VK_S) {
                this.x -= Math.cos(Math.toRadians(rotation)) * 10;
                this.y -= Math.sin(Math.toRadians(rotation)) * 10;
            }*/
        }

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics graphics) {

        //System.out.println(rotation + " X:" + x + " Y:" + y);

        this.rotation += this.rotationVelocity;
        this.x += this.vx;
        this.y += this.vy;


        if (Math.abs(this.vx) > 0) {
            this.vx += (this.vx > 0 ? -1 : 1) * 0.01;
        }

        if (Math.abs(this.vy) > 0) {
            this.vy += (this.vy > 0 ? -1 : 1) * 0.01;
        }

        if (Math.abs(this.rotationVelocity) > 0) {
            this.rotationVelocity += (this.rotationVelocity > 0 ? -1 : 1) * 0.01;
        }

        if (Math.abs(this.rotationVelocity) <= 0.1) {
            this.rotationVelocity = 0;
        }

        //super.paintComponent(graphics);

        Graphics2D g = (Graphics2D) graphics;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.rotate(Math.toRadians(rotation), getWidth() / 2, getHeight());

        g.drawImage(map, (int) x, (int) y, 10000, 10000, this);

        //g.setColor(Color.GREEN);
        //g.fillRect(getWidth() / 2 - getWidth() / 8, getHeight() / 2 - getHeight() / 8, getWidth() / 4, getHeight() / 4);


        // System.out.println(x + " " + y + " " + rotation);

        /*
        for (int v = 0; v < this.mapWidth; v++) {
            for (int c = 0; c < this.mapHeight; c++) {
                g.setColor(map[v][c].color);
                g.fillRect((int) x + v * tileSize, (int) y + c * tileSize, tileSize, tileSize);
            }
        }*/


        for (Bullet bullet : bullets) {
            g.fillRect((int) bullet.x, (int) bullet.y, 5, 5);
            bullet.update();
        }


        g.rotate(Math.toRadians(-1 * rotation), getWidth() / 2, getHeight());


        g.setColor(Color.WHITE);
        g.fillRect(getWidth() - 250, 0, 250, 250);

        g.drawImage(map, getWidth() - 250, 0, 250, 250, this);

        g.setColor(Color.RED);
        g.fillRect(getWidth() / 2 - 25, getHeight() - 50, 50, 50);

        g.drawString(rotation + " X:" + x + " Y:" + y, 10, 10);

        resumeGameButton.setBounds(Main.w / 2 - 150, 120, 300, 30);
        quitGameButton.setBounds(Main.w / 2 - 150, 160, 300, 30);

        if (this.paused) {
            g.setColor(new Color(10, 10, 10, 100));
            g.fillRect(0, 0, Main.w, Main.h);

            g.setColor(new Color(255, 255, 255));
            g.drawString("PAUSED U GEIIIII", 200, 200);

            add(resumeGameButton);
            add(quitGameButton);

        }
        else {
            remove(resumeGameButton);
            remove(quitGameButton);
        }

    }


}