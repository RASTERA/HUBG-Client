// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.FontMetrics;
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

    /*
    private float x = 0;
    private float y = 0;

    private float vx = 0;
    private float vy = 0;

    private double this.player.rotation = 0;
    private double this.player.rotationVelocity = 0;*/

    //private Tile[][] map = new Tile[500][500];
    private int mapWidth = 100;
    private int mapHeight = 100;
    private int tileSize = 1000;
    private boolean paused = false;

    private JButton quitGameButton;
    private JButton resumeGameButton;

    private MenuBar menuBar;

    private volatile double loadedResources = 0;
    private BufferedImage[][] map = new BufferedImage[50][50];
    private BufferedImage splash;
    private BufferedImage miniMap;

    private String compass = "";

    private Player player;

    private boolean serverConnected = false;
    private Communicator server;

    private int ID;

    private ArrayList<Enemy> EnemyList = new ArrayList<Enemy>();
    private boolean gameStart = false;

    private class MenuBar {

        private int width = 350;
        private int health = 100;

        private ArrayList<Item> items;

        // Bottom menu bar

        public void update(Graphics2D g, int windowW, int windowH, Player player) {

            items = player.getItems();

            g.setColor(new Color(20, 20, 20, 200));
            g.fillRect(windowW / 2 - this.width / 2, windowH - 79, this.width / 2 - 2, 50);
            g.fillRect(windowW / 2 + 2, windowH - 79, this.width / 2 - 2, 50);

            /*
            if (items.get(0) != null) {
                g.drawImage(items.get(0).getThumbnail(), windowW / 2 - this.width / 2, windowH - 79, this.width / 2 - 2, 50, null);
            }

            if (items.get(1) != null) {
                g.drawImage(items.get(1).getThumbnail(), windowW / 2 + 2, windowH - 79, this.width / 2 - 2, 50, null);
            }*/


            g.setColor(new Color(200, 200, 200, 200));
            g.drawRect(windowW / 2 - this.width / 2, windowH - 25, this.width, 15);


            if (this.health < 50) {
                g.setColor(new Color(200, 0, 0, 200));
            }
            g.fillRect(1 + windowW / 2 - this.width / 2, windowH - 24, (int) ((this.width - 1) * health / 100), 14);
        }

        public void setHealth(int health) {
            this.health = health;
        }

        public int getHealth() {
            return this.health;
        }

    }

    public Game(Main parent) {
        this.parent = parent;
        this.menuBar = new MenuBar();

        for (int i = 0; i < 360; i += 5) {
            if (i % 90 == 0) {
                compass += "    " + "NESW".charAt(i / 90) + "    ";
            }
            else {
                compass += "    " + i + "    ";
            }
        }

        compass += compass + compass;

        resumeGameButton = new JButton("Resume Game");
        resumeGameButton.setActionCommand("resume");
        resumeGameButton.addActionListener(this);

        quitGameButton = new JButton("Quit Game");
        quitGameButton.setActionCommand("quit");
        quitGameButton.addActionListener(this);

        addKeyListener(this);
        setFocusable(true);

        try {
            splash = ImageIO.read(new File("images/splash.png"));
            miniMap = ImageIO.read(new File("images/map/minimap.png"));
        }
        catch (Exception e) {

        }

        try {
            this.server = new Communicator(new byte[] {127, 0, 0, 1}, 25565, this);

            this.serverConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread loadResources = new Thread() {
            public void run() {
                try {

                    double totalResources = 2500;
                    double loaded = 0;

                    for (int x = 0; x < 50; x++) {
                        for (int y = 0; y < 50; y++) {
                            map[x][y] = ImageIO.read(new File("images/map/" + x + "_" + y + ".png"));
                            loaded++;
                            loadedResources = loaded / totalResources;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("What are you going to do about it?");
                }
            }
        };

        loadResources.start();

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

        if (!this.paused && gameStart) {


            if (e.getKeyCode() == e.VK_SPACE) {
                tileSize = 500;
                repaint();
            }


            if (e.getKeyCode() == e.VK_Q) {
                this.player.rotationVelocity -= (this.player.rotationVelocity > 2) ? 0 : 0.5;
            }

            if (e.getKeyCode() == e.VK_E) {
                this.player.rotationVelocity += (this.player.rotationVelocity < -2) ? 0 : 0.5;
            }

            if (e.getKeyCode() == e.VK_W) {
                this.player.vy += Math.sin(Math.toRadians(90 + this.player.rotation));
                this.player.vx += Math.cos(Math.toRadians(90 + this.player.rotation));
            }

            if (e.getKeyCode() == e.VK_S) {
                this.player.vy -= Math.sin(Math.toRadians(90 + this.player.rotation));
                this.player.vx -= Math.cos(Math.toRadians(90 + this.player.rotation));
            }

            if (e.getKeyCode() == e.VK_A) {
                this.player.vy += Math.sin(Math.toRadians(this.player.rotation));
                this.player.vx += Math.cos(Math.toRadians(this.player.rotation));
            }

            if (e.getKeyCode() == e.VK_D) {
                this.player.vy -= Math.sin(Math.toRadians(this.player.rotation));
                this.player.vx -= Math.cos(Math.toRadians(this.player.rotation));
            }

        }

    }

    public void keyReleased(KeyEvent e) {
       if (e.getKeyCode() == e.VK_SPACE)  {
            tileSize = 1000;
            repaint();
        }
    }

    public void CommandProcessor(Message ServerMessage) {
        switch (ServerMessage.type) {
            case 0:
                this.ID = (int) ServerMessage.message;
                System.out.println("Current ID: " + this.ID);
                break;
            case 1:
                for (float[] p : (ArrayList<float[]>) ServerMessage.message) {
                    if (p[3] == this.ID) {
                        player = new Player("Karl", p[0], p[1], p[2]);
                    } else {
                        EnemyList.add(new Enemy("Karl", p));
                    }
                }

                gameStart = true;
                break;
            case 10:
                for (int i = 0; i < EnemyList.size(); i++) {
                    if (EnemyList.get(i).getId() == ((float[]) ServerMessage.message)[3]) {
                        EnemyList.get(i).update((float[]) ServerMessage.message);
                        break;
                    }
                }
                break;
        }
    }

    public void paintComponent(Graphics graphics) {
        if (gameStart) {

            Graphics2D g = (Graphics2D) graphics;


            if (loadedResources < 1) {

                g.drawImage(splash, 0, 0, getWidth(), getHeight(), this);

                g.setColor(Color.WHITE);
                g.fillRect(100, 300, getWidth() - 200, 30);
                g.setColor(Color.RED);
                g.drawRect(100, 300, getWidth() - 200, 30);

                g.fillRect(100, 300, (int) ((getWidth() - 200) * loadedResources), 30);


                g.drawString(loadedResources * 100 + "% loaded", 10, 10);


                return;
            }

        this.player.vx = ((this.player.vx < 0) ? -1 : 1) * Math.min(1, Math.abs(this.player.vx));
        this.player.vy = ((this.player.vy < 0) ? -1 : 1) * Math.min(1, Math.abs(this.player.vy));

            this.player.rotation += this.player.rotationVelocity;
            this.player.rotation = this.player.rotation % 360;

            if (this.player.rotation < 0) {
                this.player.rotation = 360 - this.player.rotation;
            }

            this.player.x += this.player.vx / 80;
            this.player.y += this.player.vy / 80;

            this.player.x = Math.max(Math.min(0, this.player.x), -50);
            this.player.y = Math.max(Math.min(0, this.player.y), -50);

            if (Math.abs(this.player.vx) > 0) {
                this.player.vx += (this.player.vx > 0 ? -1 : 1) * 0.01;
            }

            if (Math.abs(this.player.vy) > 0) {
                this.player.vy += (this.player.vy > 0 ? -1 : 1) * 0.01;
            }

            if (Math.abs(this.player.rotationVelocity) > 0) {
                this.player.rotationVelocity += (this.player.rotationVelocity > 0 ? -1 : 1) * 0.01;
            }

            if (Math.abs(this.player.rotationVelocity) <= 0.1) {
                this.player.rotationVelocity = 0;
            }


            g.setColor(Color.BLUE);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.rotate(Math.toRadians(-1 * this.player.rotation), getWidth() / 2, getHeight() / 2);


            for (int mx = 0; mx < 50; mx++) {
                for (int my = 0; my < 50; my++) {
                    g.setColor(new Color((mx + my) % 2 == 0 ? 255 : 0, 255, 255));

                    g.drawImage(map[mx][my], (int) ((this.player.x * tileSize + tileSize * mx) + (getWidth() / 2)), (int) ((this.player.y * tileSize + tileSize * my) + (getHeight() / 2)), tileSize, tileSize, this);
                    g.drawRect((int) ((this.player.x * tileSize + tileSize * mx) + (getWidth() / 2)), (int) ((this.player.y * tileSize + tileSize * my) + (getHeight() / 2)), tileSize, tileSize);

                }
            }


            g.rotate(Math.toRadians(this.player.rotation), getWidth() / 2, getHeight() / 2);

        g.drawString(compass, getWidth() / 2 - 3820 - (int) this.player.rotation * 10, 10);

            // Mini map
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(getWidth() - 250, 0, 250, 250);
            g.drawImage(miniMap, getWidth() - 250, 0, 250, 250, this);

            g.setColor(Color.RED);
            g.fillRect(getWidth() - 250 + (int) (-250 * this.player.x / 50), (int) (-250 * this.player.y / 50), 2, 2);

        // Player
        g.fillRect(getWidth() / 2 - (int) (tileSize * 0.05) / 2, getHeight() / 2 - (int) (tileSize * 0.05) / 2, (int) (tileSize * 0.05), (int) (tileSize * 0.05));

        menuBar.setHealth((int) (this.player.x * -2));

        g.drawString(this.player.rotation + " X:" + this.player.x + " Y:" + this.player.y + " VX:" + this.player.vx + " VY" + this.player.vy, 10, getHeight() - 20);

            resumeGameButton.setBounds(Main.w / 2 - 150, 120, 300, 30);
            quitGameButton.setBounds(Main.w / 2 - 150, 160, 300, 30);

        menuBar.update(g, getWidth(), getHeight(), player);

            if (this.paused) {
                g.setColor(new Color(10, 10, 10, 100));
                g.fillRect(0, 0, Main.w, Main.h);

                g.setColor(new Color(255, 255, 255));
                g.drawString("PAUSED U GEIIIII", 200, 200);

                add(resumeGameButton);
                add(quitGameButton);

            } else {
                remove(resumeGameButton);
                remove(quitGameButton);
            }
        }
    }
}