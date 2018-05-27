// Main.java

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.io.InputStream;

public class Main extends JFrame implements ActionListener, ComponentListener {

    public enum Pages {MENU, GAME, LOGIN}

    private static Timer masterTimer;
    public static int w = 1160;
    public static int h = 600;
    private static GeiPanel panel;
    private static Pages page = Pages.LOGIN;
    public static Session session;
    private static final HashMap<String, Font> fontHashMap = new HashMap<>();

    private static long prevFrame = 0;
    private static boolean graphicsStarted = false;

    private Main() {
        super("HUBG - Henning's Unknown Battle Ground");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(w, h);
        this.setMinimumSize(new Dimension(1160, 600));
        this.setLayout(new BorderLayout());

        this.startGraphics();

        this.getContentPane().addComponentListener(this);
        this.setVisible(true);

        String[] fonts = new String[] {"Lato-Light", "Lato-Normal", "Lato-Thin", "Lato-Bold", "Lato-Black"};

        try {
            InputStream is;
            for (String font : fonts) {
                is = Main.class.getResourceAsStream(String.format("fonts/%s.ttf", font));
                fontHashMap.put(font, Font.createFont(Font.TRUETYPE_FONT, is));
            }
        } catch (Exception e) {
            errorQuit(e);
        }
    }

    public static Font getFont(String name, float size) {
        return fontHashMap.get(name).deriveFont(size);
    }

    public void setMasterTimer(int interval) {
        if (masterTimer != null) {
            masterTimer.stop();
            masterTimer = null;
        }

        masterTimer = new Timer(interval, this);
        masterTimer.start();
    }

    public void startPage(Pages page) {
        System.out.println("Starting page " + page.toString());
        this.getContentPane().remove(panel);
        Main.page = page;
        panel = null;
        this.startGraphics();
    }

    public void updateFrameRate() {
        long currentTime = System.currentTimeMillis();
        this.setTitle(String.format("HUBG - Henning's Unknown Battle Ground - [%d FPS]", (currentTime - prevFrame) > 0 ? 1000 / (currentTime - prevFrame) : 0));
        prevFrame = currentTime;
    }


    private void startGraphics() {
        if (panel == null || !panel.getClass().getName().toUpperCase().equals(page.toString())) {

            if (masterTimer != null) {
                masterTimer.stop();
                masterTimer = null;
            }

            System.out.println("Switch Pages " + page.toString());

            switch (page) {
                case LOGIN:
                    panel = new Login(this);
                    break;

                case MENU:
                    panel = new Menu(this);
                    break;

                case GAME:
                    panel = new Game(this);
                    break;

            }

            graphicsStarted = true;

            this.add(panel);
            panel.requestFocus();
            this.setVisible(true);
            panel.repaint();

        }
    }

    public void componentHidden(ComponentEvent ce) {

    }

    public void componentShown(ComponentEvent ce) {

    }

    public void componentMoved(ComponentEvent ce) {

    }

    public void componentResized(ComponentEvent ce) {
        w = this.getWidth();
        h = this.getHeight();

        if (panel != null) {
            this.repaint();
            panel.repaint();
        }

    }

    public void actionPerformed(ActionEvent evt) {

        if (graphicsStarted) {
            this.startGraphics();

            if (panel.constantUpdate) {
                this.repaint();
            }

            if (panel instanceof Menu) {
                ((Menu) panel).updateStats();
            }

        }

        /*
        long currentTime = System.currentTimeMillis();

        System.out.println(1000/(currentTime - prevFrame));

        prevFrame = currentTime;*/

    }

    public static void errorQuit(String e) {
        if (masterTimer != null) {
            masterTimer.stop();
            masterTimer = null;
        }

        System.out.println("Something went wrong");
        JOptionPane.showMessageDialog(null, "HUBG experienced an unexpected error:\n\n" + e, "HUBG Error", JOptionPane.ERROR_MESSAGE);

        System.exit(0);
    }

    public static void errorQuit(Exception e) {
        e.printStackTrace();

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        errorQuit(sw.toString());
    }

    public static void main(String[] args) {
        Main frame = new Main();
    }

}