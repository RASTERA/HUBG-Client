// Main.java

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.HashMap;

public class Main extends JFrame implements ActionListener, ComponentListener {

    public enum Pages {MENU, GAME, LOGIN}

    public static Timer masterTimer;
    public static int w = 1160;
    public static int h = 600;
    public static GeiPanel panel;
    public static Pages page = Pages.LOGIN;
    public static Session session;

    public static long prevFrame = 0;
    private static boolean graphicsStarted = false;

    public Main() {
        super("HUBG - Henning's Unknown Battle Ground");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(w, h);
        setMinimumSize(new Dimension(1160, 600));
        setLayout(new BorderLayout());

        startGraphics();

        getContentPane().addComponentListener(this);
        setVisible(true);

    }

    public void setMasterTimer(int interval) {
        if (masterTimer != null) {
            masterTimer.stop();
            masterTimer = null;
        }

        masterTimer = new Timer(interval, this);
        masterTimer.start();
    }

    public void startPage(Pages page) {
        System.out.println("Starting page " + page.toString());
        this.getContentPane().remove(this.panel);
        this.page = page;
        this.panel = null;
        //startGraphics();
    }

    public void updateFrameRate() {
        long currentTime = System.currentTimeMillis();
        this.setTitle(String.format("HUBG - Henning's Unknown Battle Ground - [%d FPS]", (currentTime - prevFrame) > 0 ? 1000 / (currentTime - prevFrame) : 0));
        prevFrame = currentTime;
    }


    public void startGraphics() {
        if (this.panel == null || !this.panel.getClass().getName().toUpperCase().equals(page.toString())) {

            if (masterTimer != null) {
                masterTimer.stop();
                masterTimer = null;
            }

            System.out.println("Switch Pages " + page.toString());

            switch (page) {
                case LOGIN:
                    panel = new Login(this);
                    break;

                case MENU:
                    panel = new Menu(this);
                    break;

                case GAME:
                    panel = new Game(this);
                    break;

            }

            graphicsStarted = true;

            add(panel);
            panel.requestFocus();
            setVisible(true);
            this.panel.repaint();

        }
    }

    public void componentHidden(ComponentEvent ce) {

    }

    public void componentShown(ComponentEvent ce) {

    }

    public void componentMoved(ComponentEvent ce) {

    }

    public void componentResized(ComponentEvent ce) {
        this.w = getWidth();
        this.h = getHeight();

        if (this.panel != null) {
            this.repaint();
            this.panel.repaint();
        }

    }

    public void actionPerformed(ActionEvent evt) {

        if (graphicsStarted) {
            startGraphics();

            if (this.panel.constantUpdate) {
                this.repaint();
            }

        }

        /*
        long currentTime = System.currentTimeMillis();

        System.out.println(1000/(currentTime - prevFrame));

        prevFrame = currentTime;*/

    }


    public static void main(String[] args) {
        Main frame = new Main();
    }

}