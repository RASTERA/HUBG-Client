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
import java.util.ArrayList;


class Login extends GeiPanel implements ActionListener, KeyListener, MouseListener {

    private final GeiTextField emailField;
    private final JPasswordField passwordField;
    private final JLabel emailLabel;
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

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(this.getClass().getResource("music/menu.wav"));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
            clip.loop(clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.emailField = new GeiTextField();
        this.passwordField = new JPasswordField();

        this.emailLabel = new JLabel("Email");
        this.passwordLabel = new JLabel("Password");
        this.forgetPasswordLabel = new JLabel("Forgot your password?");
        this.createAccountLabel = new JLabel("Create an account");

        this.emailLabel.setForeground(Color.WHITE);
        this.passwordLabel.setForeground(Color.WHITE);

        this.loginButton = new GeiButton("SIGN IN");
        this.loginButton.setActionCommand("login");
        this.loginButton.addActionListener(this);
        this.loginButton.setEnabled(false);

        // Submit on enter
        this.emailField.addActionListener(e -> this.loginButton.doClick());

        this.passwordField.addActionListener(e -> this.loginButton.doClick());

        // Validate text
        this.emailField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.emailField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    Login.this.validateText();
                }
            }
        });

        this.passwordField.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.passwordField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);

                if (e.getKeyCode() != KeyEvent.VK_ENTER) {
                    Login.this.validateText();
                }
            }
        });


        this.forgetPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.forgetPasswordLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        this.createAccountLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.createAccountLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Rah.webbrowserOpen("https://rastera.xyz");
            }
        });

        this.add(this.emailLabel);
        this.add(this.passwordLabel);
        this.add(this.forgetPasswordLabel);
        this.add(this.createAccountLabel);

        this.add(this.emailField);
        this.add(this.passwordField);

        this.add(this.loginButton);

    }

    private void validateText() {
        if (this.emailField.getText().length() > 0 && String.valueOf(this.passwordField.getPassword()).length() > 0) {
            this.loginButton.setEnabled(true);
        } else {
            this.loginButton.setEnabled(false);
        }
    }

    private void disableLogin() {
        this.loginButton.setEnabled(false);
        this.passwordField.setEditable(false);
        this.emailField.setEditable(false);
        this.loginButton.setText("Authenticating...");
    }

    private void enableLogin() {

        this.passwordField.setEditable(true);
        this.emailField.setEditable(true);
        this.passwordField.setText("");
        this.loginButton.setText("SIGN IN");
    }

    private void startMenu(Session session) {
        System.out.println("Login Successful");
        Main.session = session;
        this.removeKeyListener(this);
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

                this.disableLogin();

                System.out.println("Trying to login...");

                // Separate thread so that UI thread is not blocked
                Thread authentication = new Thread(() -> {

                    //Session session = new Session("Hi", "Hi");
                    Session session = Communicator.login(Login.this.emailField.getText(), String.valueOf(Login.this.passwordField.getPassword()));

                    if (session == null) {
                        JOptionPane.showMessageDialog(Login.this.parent, "An error occurred while connecting to the authentication server. Please try again later.", "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                        Login.this.enableLogin();
                    } else if (session.getUsername().equals("")) {
                        JOptionPane.showMessageDialog(Login.this.parent, session.getToken(), "RASTERA Authentication Service", JOptionPane.ERROR_MESSAGE);
                        Login.this.enableLogin();
                    } else {
                        Login.this.startMenu(session);
                    }


                });

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

        this.parent.updateFrameRate();

        this.emailLabel.setBounds(Main.w - 230, 140, 210, 20);
        this.emailField.setBounds(Main.w - 230, 160, 210, 30);

        this.passwordLabel.setBounds(Main.w - 230, 200, 210, 20);
        this.passwordField.setBounds(Main.w - 230, 220, 210, 30);

        this.loginButton.setBounds(Main.w - 230, Main.h - 200, 210, 30);

        this.forgetPasswordLabel.setBounds(Main.w - 230, Main.h - 70, 210, 20);
        this.createAccountLabel.setBounds(Main.w - 230, Main.h - 90, 210, 20);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Main.w, Main.h);

        int size = Math.max(Main.w - 250, Main.h);

        g.drawImage(this.backgroundFrames.get(this.frame), 0, 0, size, size, this);
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