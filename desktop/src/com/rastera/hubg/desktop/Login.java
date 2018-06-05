// Some game magic bs

package com.rastera.hubg.desktop;

import javax.imageio.ImageIO;
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
import java.util.ArrayList;

class Login extends GeiPanel implements ActionListener, KeyListener, MouseListener {

    private final GeiTextField emailOrUserField;
    private final JPasswordField passwordField;
    private final JLabel emailOrUserLabel;
    private final JLabel passwordLabel;
    private final JLabel forgetPasswordLabel;
    private final JLabel createAccountLabel;
    private final GeiButton loginButton;

    private final ArrayList<BufferedImage> backgroundFrames = new ArrayList<>();
    private BufferedImage rasteraLogo;
    private BufferedImage hubgLogo;

    private int frame = 0;
    private final int frameCap = 30;

    public Login(Main parent) {
        this.parent = parent;
        this.parent.setMasterTimer(45);

        //this.constantUpdate = false;

        this.addKeyListener(this);
        this.setFocusable(true);
        this.setLayout(null);

        try {
            String fileName;

            for (int i = 0; i <= this.frameCap; i++) {
                fileName = String.format("images/menu_animation/%d.png", i);
                this.backgroundFrames.add(ImageIO.read(new File(fileName)));
            }

            this.rasteraLogo = ImageIO.read(new File("images/rastera.png"));
            this.hubgLogo = ImageIO.read(new File("images/hubg-logo.png"));

        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.emailOrUserField = new GeiTextField();
        this.passwordField = new JPasswordField();

        this.emailOrUserLabel = new JLabel("Email/Username");
        this.passwordLabel = new JLabel("Password");
        this.forgetPasswordLabel = new JLabel("Forgot your password?");
        this.createAccountLabel = new JLabel("Create an account");

        this.emailOrUserLabel.setForeground(Color.WHITE);
        this.passwordLabel.setForeground(Color.WHITE);

        this.loginButton = new GeiButton("SIGN IN");
        this.loginButton.setActionCommand("login");
        this.loginButton.addActionListener(this);
        this.loginButton.setEnabled(false);

        // Submit on enter
        this.emailOrUserField.addActionListener(e -> this.loginButton.doClick());

        this.passwordField.addActionListener(e -> this.loginButton.doClick());

        // Validate text
        this.emailOrUserField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.emailOrUserField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    Login.this.validateText();
                }
            }
        });

        this.passwordField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.passwordField.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    Login.this.validateText();
                }
            }
        });


        this.forgetPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.forgetPasswordLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        this.createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.createAccountLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        this.add(this.emailOrUserLabel);
        this.add(this.passwordLabel);
        this.add(this.forgetPasswordLabel);
        this.add(this.createAccountLabel);

        this.add(this.emailOrUserField);
        this.add(this.passwordField);

        this.add(this.loginButton);

        Thread tokenRefresh = new Thread(() -> {
            AuthToken tempAuth = Session.readSession();

            if (tempAuth != null) {
                disableLogin();

                System.out.println("Login from token");

                // Refresh token
                Session tempSession = Communicator.login(tempAuth);

                if (tempSession != null) {
                    Main.session = tempSession;
                    this.removeKeyListener(this);
                    this.parent.startPage(Main.Pages.MENU);
                } else {

                    Session.destroySession();

                    JOptionPane.showMessageDialog(null, "Unable to login with token", "HUBG Error", JOptionPane.ERROR_MESSAGE);
                    enableLogin();

                }
            }
        });

        tokenRefresh.start();
    }

    private void validateText() {
        if (this.emailOrUserField.getText().length() > 0 && String.valueOf(this.passwordField.getPassword()).length() > 0) {
            this.loginButton.setEnabled(true);
        } else {
            this.loginButton.setEnabled(false);
        }
    }

    private void disableLogin() {
        this.loginButton.setEnabled(false);
        this.passwordField.setEditable(false);
        this.emailOrUserField.setEditable(false);
        this.loginButton.setText("Authenticating...");
    }

    private void enableLogin() {

        this.passwordField.setEditable(true);
        this.emailOrUserField.setEditable(true);
        this.passwordField.setText("");
        this.loginButton.setText("SIGN IN");
    }

    /*
    *
    * k so let's say you had a very short amount of time to code a game (which will be interviewed and stuff) and someone had code for a portion of the program, but it was only usable for that one person (since only they can understand it); although the logic's there, it would take you a long time to understand, it's unfinished, and requires quite a bit of revision.
you create a new system that fits the basic criteria of your assignment and propose that they implement their own code in later.
they want to implement their complex system right away, even though you have this short time span and don't have the basics of the project done.
what do you do?
    * */

    private void startMenu(Session session) {
        System.out.println("Login Successful");
        Main.session = session;

        Session.writeSession();

        this.removeKeyListener(this);
        this.parent.startPage(Main.Pages.MENU);
    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "login":

                this.disableLogin();

                System.out.println("Trying to login...");

                // Separate thread so that UI thread is not blocked
                Thread authentication = new Thread(() -> {

                    //Session session = new Session("Hi", "Hi");
                    Session session = Communicator.login(Login.this.emailOrUserField.getText(), String.valueOf(Login.this.passwordField.getPassword()));

                    if (session == null) {
                        JOptionPane.showMessageDialog(Rah.checkParent(this.parent), "An error occurred while connecting to the authentication server. Please try again later.", "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                        Login.this.enableLogin();
                    } else if (session.user == null) {
                        JOptionPane.showMessageDialog(Rah.checkParent(this.parent), session.getToken(), "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                        Login.this.enableLogin();
                    } else {
                        Login.this.startMenu(session);
                    }

                });

                authentication.start();
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void paintComponent(Graphics g) {

        this.parent.updateFrameRate();

        this.emailOrUserLabel.setBounds(Main.w - 230, 140, 210, 20);
        this.emailOrUserField.setBounds(Main.w - 230, 160, 210, 30);

        this.passwordLabel.setBounds(Main.w - 230, 200, 210, 20);
        this.passwordField.setBounds(Main.w - 230, 220, 210, 30);

        this.loginButton.setBounds(Main.w - 230, Main.h - 200, 210, 30);

        this.forgetPasswordLabel.setBounds(Main.w - 230, Main.h - 70, 210, 20);
        this.createAccountLabel.setBounds(Main.w - 230, Main.h - 90, 210, 20);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Main.w, Main.h);

        int size = Math.max(Main.w - 250, Main.h);

        g.drawImage(this.backgroundFrames.get(this.frame), 0, Main.h - size, size, size, this);
        g.drawImage(this.rasteraLogo, 30, this.getHeight() - 55, 150, 25, this);


        g.setColor(new Color(1, 10, 19));
        g.fillRect(Main.w - 250, 0, 250, Main.h);

        g.drawImage(this.hubgLogo, Main.w - 230, 40, 210, 66, this);

        if (this.frame == this.frameCap) {
            this.frame = 0;
        } else {
            this.frame++;
        }

    }


}