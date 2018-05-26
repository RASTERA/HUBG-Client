// Some game magic bs

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.io.File;
import java.util.Date;
import java.util.HashMap;

public class Menu extends GeiPanel implements KeyListener, ActionListener {

    private GeiButton startButton;
    private JScrollPane recentActions;
    private GeiStatsPanel recentActionsPanel;
    private int statsPanelWidth = 250;
    private BufferedImage background;
    private String statsText = "";
    private long lastUpdated = System.currentTimeMillis();
    private HashMap<String, BufferedImage> skinHashMap = new HashMap<>();

    public Menu(Main parent) {

        this.parent = parent;
        this.parent.setMasterTimer(10000);
        this.constantUpdate = true;

        try {
            this.background = ImageIO.read(new File("images/menu-background-2.png"));

            this.skinHashMap.put("PENGUIN", ImageIO.read(new File("images/skins/penguin.png")));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.startButton = new GeiButton("Start");
        this.startButton.setActionCommand("start");
        this.startButton.addActionListener(this);

        try {
            this.recentActionsPanel = new GeiStatsPanel(statsPanelWidth, Main.session.user.getJSONArray("actions"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.recentActions = new JScrollPane(this.recentActionsPanel);
        this.recentActions.setBorder(null);
        this.recentActions.getVerticalScrollBar().setUnitIncrement(16);
        this.recentActions.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));

        this.recentActionsPanel.setParent(recentActions);

        add(this.recentActions);
        add(this.startButton);

        addKeyListener(this);
        setFocusable(true);

        Thread loadResources = new Thread() {
            public void run() {
                updateStats();
            }
        };

        loadResources.start();

    }

    public void updateStats() {
        try {
            JSONObject tempUser = Communicator.refresh(Main.session.getToken());

            if (tempUser != null) {
                Main.session.user = tempUser;
                Main.session.updateJSON();

                System.out.println(tempUser);

                this.statsText = String.format("%d Kills      |      %s Deaths      |      %s Matches      |      %s Zhekko", Main.session.user.getLong("kills"), Main.session.user.getString("deaths"), Main.session.user.getString("matches"), Main.session.user.getString("money"));
                this.recentActionsPanel.update(Main.session.user.getJSONArray("actions"));
                this.lastUpdated = System.currentTimeMillis();

            } else {
                System.out.println("Unable to connect to server");
            }

        } catch (Exception e) {
            Main.errorQuit(e);
        }
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

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

        this.parent.updateFrameRate();

        startButton.setBounds(20, 10, 150, 40);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Main.w, Main.h);

        int size = Math.max(getWidth() - statsPanelWidth, getHeight() - 60);

        g.drawImage(this.background, 0, 60, size, size, this);

        g.setColor(new Color(5, 15, 24));
        g.fillRect(0, 0, Main.w, 60);

        //g.setColor(new Color(1, 10, 19));
        //g.fillRect(Main.w - 250, 0, 250, Main.h);

        // Recent Actions panel
        recentActions.setBounds(Main.w - this.statsPanelWidth, 60, this.statsPanelWidth, Main.h - 60);
        recentActions.revalidate();
        recentActions.repaint();

        g.setColor(Color.WHITE);

        // Rank badge
        g.setFont(Main.getFont("Lato-Normal", 30));
        g.drawString("" + Main.session.getRank(), Main.w - this.statsPanelWidth + 20, 40);

        // Username
        g.setFont(Main.getFont("Lato-Light", 30));
        g.drawString(Main.session.getUsername(), Main.w - this.statsPanelWidth + 60, 40);

        // Top Bar stats
        g.setFont(Main.getFont("Lato-Light", 20));
        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 20));
        g.drawString(statsText, getWidth() / 2 - metrics.stringWidth(statsText) / 2, 35);

        // Last updated
        String updateText = "Last sync: " + new Date(lastUpdated / 1000).toString();
        g.setFont(Main.getFont("Lato-Light", 12));
        metrics = g.getFontMetrics(Main.getFont("Lato-Light", 12));
        g.drawString(updateText, 10, getHeight() - 15);

        // Penguin preview
        int dimension = (int) (Math.min(getHeight(), getWidth()) * 0.6);
        g.drawImage(skinHashMap.get(Main.session.getSkin()), (getWidth() - this.statsPanelWidth) / 2 - dimension / 2, (getHeight() + 60) / 2 - dimension / 2, dimension, dimension, this);
    }


}