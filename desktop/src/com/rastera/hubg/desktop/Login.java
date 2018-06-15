// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Login .java - Login Screen

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

class Login extends HUBGPanel implements ActionListener, KeyListener, MouseListener {

    // UI Elements
    private final HUBGTextField emailOrUserField;
    private final JPasswordField passwordField;
    private final JLabel emailOrUserLabel;
    private final JLabel passwordLabel;
    private final JLabel forgetPasswordLabel;
    private final JLabel createAccountLabel;
    private final JLabel creditsLabel;
    private final HUBGButton loginButton;

    // Window buttons
    private final HUBGEdgeButton minimizeButton;
    private final HUBGEdgeButton closeButton;

    // Splash animation
    private final ArrayList<BufferedImage> backgroundFrames = new ArrayList<>();
    private BufferedImage rasteraLogo;
    private BufferedImage hubgLogo;

    // Animation config
    private int frame = 0;
    private final int frameCap = 30;

    // Flag for loading screen
    private volatile boolean tokenLogin = true;

    // Graphics
    private BufferedImage background;
    private JProgressBar loadingBar;

    public Login(Main parent) {
        this.parent = parent;
        this.parent.setMasterTimer(45);

        // Loading bar
        this.loadingBar = new JProgressBar();
        this.loadingBar.setIndeterminate(true);

        // Configures environment
        this.addKeyListener(this);
        this.setFocusable(true);
        this.setLayout(null);

        // UI Setup
        this.minimizeButton = new HUBGEdgeButton("-");
        this.minimizeButton.setActionCommand("minimize");
        this.minimizeButton.addActionListener(this);

        this.closeButton = new HUBGEdgeButton("X");
        this.closeButton.setActionCommand("close");
        this.closeButton.addActionListener(this);

        // Adds edge button if borderless
        if (Main.borderless) {
            this.add(this.minimizeButton);
            this.add(this.closeButton);
        }

        // Import resources
        try {
            this.background = ImageIO.read(new File("images/menu-background-2.png"));

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

        // Config core UI
        this.emailOrUserField = new HUBGTextField();
        this.passwordField = new JPasswordField();

        this.emailOrUserLabel = new JLabel("Email/Username");
        this.passwordLabel = new JLabel("Password");
        this.creditsLabel = new JLabel("About/Credits");
        this.forgetPasswordLabel = new JLabel("Forgot your password?");
        this.createAccountLabel = new JLabel("Create an account");

        this.emailOrUserLabel.setForeground(Color.WHITE);
        this.passwordLabel.setForeground(Color.WHITE);

        this.loginButton = new HUBGButton("SIGN IN");
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

        // Credits
        this.creditsLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.creditsLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Util.checkParent(Login.this.parent), "PROJECT HUBG | RASTERA | rastera.xyz\nLicenced under WTFPL\n\nDeveloped by:\nHenry Tu (github.com/henrytwo, henrytu.me)\nRyan Zhang (github.com/ryanz34)\nSyed Safwaan (github.com/syed-safwaan)\n\nICS4U Final Project - 2017/2018\n\nAll copyrighted works are property of their respective owner.\nIcons courtesy of icons8.com.", "RASTERA | PROJECT HUBG", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Password reset
        this.forgetPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.forgetPasswordLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(Util.checkParent(Login.this.parent), "Yeah... if you forget your password you're kinda out of luck ¯\\_(ツ)_/¯", "Message from Torvalds", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Account creation
        this.createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.createAccountLabel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Util.webbrowserOpen("https://rastera.xyz");
            }
        });

        this.add(Login.this.loadingBar);

        // Thread for validating token
        Thread tokenRefresh = new Thread(() -> {

            // Loads token from session
            AuthToken tempAuth = Session.readSession();

            if (tempAuth != null) {

                System.out.println("Login from token");

                // Refresh token
                Session tempSession = Communicator.login(tempAuth);

                // Checks if token exists
                if (tempSession.getUsername() != null) {
                    Main.session = tempSession;
                    this.removeKeyListener(this);
                    this.parent.startPage(Main.Pages.MENU);
                } else {

                    Session.destroySession();
                    this.startLoginUI();

                }
            } else {
                this.startLoginUI();
            }
        });

        tokenRefresh.start();
    }

    // Add core UI
    public void startLoginUI() {
        this.remove(Login.this.loadingBar);
        this.tokenLogin = false;
        this.repaint();


        this.add(this.emailOrUserLabel);
        this.add(this.passwordLabel);
        this.add(this.forgetPasswordLabel);
        this.add(this.createAccountLabel);
        this.add(this.creditsLabel);

        this.add(this.emailOrUserField);
        this.add(this.passwordField);

        this.add(this.loginButton);

        this.minimizeButton.setBackgroundDark();
        this.closeButton.setBackgroundDark();
    }

    // Validate login credentials
    // Ensure not blank
    private void validateText() {
        if (this.emailOrUserField.getText().length() > 0 && String.valueOf(this.passwordField.getPassword()).length() > 0) {
            this.loginButton.setEnabled(true);
        } else {
            this.loginButton.setEnabled(false);
        }
    }

    // Disable UI while loading
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

    // Start menu after successful auth
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

    // Button actions
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "minimize":
                this.parent.minimize();
                break;

            case "close":
                this.parent.close();
                break;

            case "login":

                // Locks UI
                this.disableLogin();

                System.out.println("Trying to login...");

                // Separate thread so that UI thread is not blocked
                Thread authentication = new Thread(() -> {

                    Session session = Communicator.login(Login.this.emailOrUserField.getText(), String.valueOf(Login.this.passwordField.getPassword()));

                    // Validates token
                    if (session == null) {
                        JOptionPane.showMessageDialog(Util.checkParent(this.parent), "An error occurred while connecting to the authentication server. Please try again later.", "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                        Login.this.enableLogin();
                    } else if (session.user == null) {
                        JOptionPane.showMessageDialog(Util.checkParent(this.parent), session.getToken(), "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
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
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        this.minimizeButton.setBounds(this.getWidth() - 40, 0, 20, 20);
        this.closeButton.setBounds(this.getWidth() - 20, 0, 20, 20);

        this.parent.updateFrameRate();

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Displays loading if waiting for token to be authorized
        if (this.tokenLogin) {
            this.loadingBar.setBounds(50, this.getHeight() / 2 + 40, this.getWidth() - 100, 20);

            String loadingMessage = "Authenticating";

            int size = Math.max(this.getWidth(), this.getHeight());

            g.drawImage(this.background, 0, 0, size, size, this);

            g.setColor(Color.BLACK);
            g.setFont(Main.getFont("Lato-Light", 30));

            FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 30));
            g.drawString(loadingMessage, this.getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);

        } else {

            this.emailOrUserLabel.setBounds(this.getWidth() - 230, 140, 210, 20);
            this.emailOrUserField.setBounds(this.getWidth() - 230, 160, 210, 30);

            this.passwordLabel.setBounds(this.getWidth() - 230, 200, 210, 20);
            this.passwordField.setBounds(this.getWidth() - 230, 220, 210, 30);


            this.loginButton.setBounds(this.getWidth() - 230, this.getHeight() - 200, 210, 30);

            this.forgetPasswordLabel.setBounds(this.getWidth() - 230, this.getHeight() - 70, 210, 20);
            this.createAccountLabel.setBounds(this.getWidth() - 230, this.getHeight() - 90, 210, 20);
            this.creditsLabel.setBounds(this.getWidth() - 230, this.getHeight() - 110, 210, 20);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            int size = Math.max(this.getWidth() - 250, this.getHeight());

            // Animate splash
            g.drawImage(this.backgroundFrames.get(this.frame), 0, this.getHeight() - size, size, size, this);
            g.drawImage(this.rasteraLogo, 30, this.getHeight() - 55, 150, 25, this);

            if (this.frame == this.frameCap) {
                this.frame = 0;
            } else {
                this.frame++;
            }

            g.setColor(new Color(1, 10, 19));
            g.fillRect(this.getWidth() - 250, 0, 250, this.getHeight());

            g.drawImage(this.hubgLogo, this.getWidth() - 230, 40, 210, 66, this);
        }
    }
}