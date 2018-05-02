// Main.java

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.util.Random;

public class Main extends JFrame implements ActionListener, ComponentListener {

    public static int w = 800;
    public static int h = 600;

    public Main() {
        super("[RASTERA] HUBG");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(w, h);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout());

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