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
import javax.imageio.ImageIO;
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

    private BufferedImage background;

    public Login(Main parent) {
        this.parent = parent;

        addKeyListener(this);
        setFocusable(true);
        setLayout(null);

        try {
            background = ImageIO.read(new File("images/splash.png"));
        }
        catch (Exception e) {
            System.out.println("ur bad");
        }

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");

        loginButton = new JButton("SIGN IN");
        loginButton.setActionCommand("login");

        loginButton.addActionListener(this);

        add(usernameLabel);
        add(passwordLabel);

        add(usernameField);
        add(passwordField);

        add(loginButton);

    }

    public void disableLogin() {
        passwordField.setEditable(false);
        usernameField.setEditable(false);
        loginButton.setText("Authenticating...");
        loginButton.setEnabled(false);
    }

    public void enableLogin() {
        passwordField.setEditable(true);
        usernameField.setEditable(true);
        passwordField.setText("");
        loginButton.setText("SIGN IN");
        loginButton.setEnabled(true);
    }

    public void startMenu(Session session) {
        System.out.println("Login Successful");
        Main.session = session;
        removeKeyListener(this);
        this.parent.startPage(Main.Pages.MENU);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":

                disableLogin();

                // Seperate thread so that UI thread is not blocked
                Thread authentication = new Thread() {
                    public void run() {

                        Session session = Communicator.login(usernameField.getText(), String.valueOf(passwordField.getPassword()));

                        if (session == null){
                            JOptionPane.showMessageDialog(parent, "An error occured while connecting to the authentication server. Please try again later.", "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                            enableLogin();
                        }
                        else if (session.getUsername().equals("")) {
                            JOptionPane.showMessageDialog(parent, session.getToken(), "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                            enableLogin();
                        }
                        else {
                            startMenu(session);
                        }

                    }
                };

                authentication.start();
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

        g.drawImage(background, 0, 0, Main.w - 250, Main.h, this);

        g.setColor(new Color(1, 10, 19));
        g.fillRect(Main.w - 250, 0, 250, Main.h);

        usernameLabel.setBounds(Main.w - 230, 140, 210, 20);
        usernameField.setBounds(Main.w - 230, 160, 210, 30);

        passwordLabel.setBounds(Main.w - 230, 200, 210, 20);
        passwordField.setBounds(Main.w - 230, 220, 210, 30);

        loginButton.setBounds(Main.w - 230, Main.h - 200, 210, 30);

    }


}