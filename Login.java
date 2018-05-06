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
import java.util.Scanner;

public class Login extends JPanel implements KeyListener {

    private Main parent;

    public Login(Main parent) {
        this.parent = parent;

        addKeyListener(this);
        setFocusable(true);

    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {



        if (e.getKeyCode() == e.VK_W) {
            System.out.println("SWITCH");

            removeKeyListener(this);
            this.parent.startPage(Main.Pages.MENU);
        }



    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {


    }


}