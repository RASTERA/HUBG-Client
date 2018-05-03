// Main.java

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.HashMap;

public class Main extends JFrame implements ActionListener, ComponentListener {

    public static enum Pages {MENU, GAME};
    public static Timer gameTimer;
    public static HashMap<String, Class<? extends JPanel>> screens = new HashMap<>();
    public static int w = 800;
    public static int h = 600;
    public static JPanel panel;
    public static Pages page = Pages.GAME;

    public Main() {
        super("HUBG - Henning's Unknown Battle Ground");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(w, h);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

        gameTimer = new Timer(50, this);
        gameTimer.start();

        getContentPane().addComponentListener(this);
        setVisible(true);

        startGraphics();

    }

    public void startGraphics() {
        if (panel == null) {

            switch (page) {
                case GAME:
                    panel = new Game(this);
                    break;
            }

            add(panel);
            setVisible(true);
            panel.repaint();

        }
    }

    public void componentHidden(ComponentEvent ce) {

    }

    public void componentShown(ComponentEvent ce) {

    }

    public void componentMoved(ComponentEvent ce) {

    }

    public void componentResized(ComponentEvent ce) {
        w = getWidth();
        h = getHeight();
    }

    public void actionPerformed(ActionEvent evt) {
        startGraphics();

        if (panel != null) {
            panel.repaint();
        }

    }


    public static void main(String[] args) {
        Main frame = new Main();
    }

}