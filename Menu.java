// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Menu extends JPanel implements KeyListener, ActionListener {

    private Main parent;
    private JButton startButton;

    public Menu(Main parent) {
        this.parent = parent;

        startButton = new JButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        add(startButton);

        addKeyListener(this);
        setFocusable(true);

    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":
                System.out.println("SWITCH");

                removeKeyListener(this);
                this.parent.startPage(Main.Pages.GAME);
        }
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {



        if (e.getKeyCode() == e.VK_W) {
            System.out.println("SWITCH");

            removeKeyListener(this);
            this.parent.startPage(Main.Pages.GAME);
        }



    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {
        startButton.setBounds(Main.w / 2 - 150, 120, 300, 30);
        g.drawString("Yoyoyoyo", 0, 100);
    }


}