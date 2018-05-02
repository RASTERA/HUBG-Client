// Main.java

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.HashMap;

public class Main extends JFrame implements ActionListener, ComponentListener {

    public static String page = "menu";
    public static Timer gameTimer;
    public static HashMap<String, Class<JFrame>> screens = new HashMap<>();
    public static int w = 800;
    public static int h = 600;

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
        JPanel page = new Game(this);
        add(page);
        setVisible(true);
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

    }


    public static void main(String[] args) {
        Main frame = new Main();
    }

}