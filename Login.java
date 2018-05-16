// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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


public class Login extends GeiPanel implements ActionListener, KeyListener, MouseListener {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel forgetPasswordLabel;
    private JLabel createAccountLabel;
    private GeiButton loginButton;

    private BufferedImage background;
    private BufferedImage rasteraLogo;

    public Login(Main parent) {
        this.parent = parent;
        this.constantUpdate = false;

        addKeyListener(this);
        setFocusable(true);
        setLayout(null);

        try {
            background = ImageIO.read(new File("images/background.png"));
            rasteraLogo = ImageIO.read(new File("images/rastera.png"));
        }
        catch (Exception e) {
            System.out.println("ur bad");
        }

        usernameField = new JTextField();
        passwordField = new JPasswordField();

        usernameLabel = new JLabel("Username");
        passwordLabel = new JLabel("Password");
        forgetPasswordLabel = new JLabel("Forgot your password?");
        createAccountLabel = new JLabel("Create an account");

        usernameLabel.setForeground(Color.WHITE);
        passwordLabel.setForeground(Color.WHITE);

        loginButton = new GeiButton("SIGN IN");
        loginButton.setActionCommand("login");
        loginButton.addActionListener(this);

        // Submit on enter
        usernameField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        passwordField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loginButton.doClick();
            }
        });

        // Validate text
        usernameField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        usernameField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != e.VK_ENTER) {
                    validateText();
                }
            }
        });

        passwordField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != e.VK_ENTER) {
                    validateText();
                }
            }
        });


        forgetPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgetPasswordLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        createAccountLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        add(usernameLabel);
        add(passwordLabel);
        add(forgetPasswordLabel);
        add(createAccountLabel);

        add(usernameField);
        add(passwordField);

        add(loginButton);

    }

    public void validateText() {
        if (usernameField.getText().length() > 0 && String.valueOf(passwordField.getPassword()).length() > 0) {
            loginButton.setEnabled(true);
        } else {
            loginButton.setEnabled(false);
        }
    }

    public void disableLogin() {
        loginButton.setEnabled(false);
        passwordField.setEditable(false);
        usernameField.setEditable(false);
        loginButton.setText("Authenticating...");
    }

    public void enableLogin() {
        passwordField.setEditable(true);
        usernameField.setEditable(true);
        passwordField.setText("");
        loginButton.setText("SIGN IN");
    }

    public void startMenu(Session session) {
        System.out.println("Login Successful");
        Main.session = session;
        removeKeyListener(this);
        this.parent.startPage(Main.Pages.MENU);
    }

    public void mouseExited(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseClicked(MouseEvent e) {

    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":

                disableLogin();

                // Separate thread so that UI thread is not blocked
                Thread authentication = new Thread() {
                    public void run() {
                        //Session session = new Session("Hi", "Hi");
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

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {

        usernameLabel.setBounds(Main.w - 230, 140, 210, 20);
        usernameField.setBounds(Main.w - 230, 160, 210, 30);

        passwordLabel.setBounds(Main.w - 230, 200, 210, 20);
        passwordField.setBounds(Main.w - 230, 220, 210, 30);

        loginButton.setBounds(Main.w - 230, Main.h - 200, 210, 30);

        forgetPasswordLabel.setBounds(Main.w - 230, Main.h - 70, 210, 20);
        createAccountLabel.setBounds(Main.w - 230, Main.h - 90, 210, 20);

        g.drawImage(background, 0, 0, Math.max(Main.w - 250, Main.h), Math.max(Main.w - 250, Main.h), this);
        g.drawImage(rasteraLogo, 30, getHeight() - 55, 150, 25, this);

        g.setColor(new Color(1, 10, 19));
        g.fillRect(Main.w - 250, 0, 250, Main.h);

    }


}