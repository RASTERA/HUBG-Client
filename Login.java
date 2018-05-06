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

public class Login extends JPanel implements ActionListener, KeyListener {

    private Main parent;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JButton loginButton;

    public Login(Main parent) {
        this.parent = parent;

        addKeyListener(this);
        setFocusable(true);
        setLayout(null);

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");

        loginButton = new JButton("Login");
        loginButton.setActionCommand("login");

        loginButton.addActionListener(this);

        add(usernameLabel);
        add(passwordLabel);

        add(usernameField);
        add(passwordField);

        add(loginButton);

    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":
                Communicator.login(usernameField.getText(), String.valueOf(passwordField.getPassword()));
                break;
        }
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


        // Position is updated every frame

        usernameLabel.setBounds(Main.w / 2 - 150, 10, 200, 20);
        usernameField.setBounds(Main.w / 2 - 150, 30, 300, 30);

        passwordLabel.setBounds(Main.w / 2 - 150, 60, 200, 20);
        passwordField.setBounds(Main.w / 2 - 150, 80, 300, 30);

        loginButton.setBounds(Main.w / 2 - 150, 120, 300, 30);
    }


}