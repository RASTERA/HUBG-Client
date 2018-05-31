// Some game magic bs

import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Iterator;

class Menu extends GeiPanel implements KeyListener, ActionListener {

    private enum SUBPANEL { ACTIVITY, SHOP, CHAT }
    private SUBPANEL currentPanel = SUBPANEL.ACTIVITY;
    private final GeiButton startButton;
    private final GeiButton activityButton;
    private final GeiButton shopButton;
    private final GeiButton chatButton;
    private final GeiButton logoutButton;
    private final GeiScrollPane activityScrollPane;
    private final GeiScrollPane chatScrollPane;
    private final GeiScrollPane shopScrollPane;
    private final JProgressBar loadingBar;
    private GeiStatsPanel recentActionsPanel;
    private GeiShopPanel shopPanel;
    private GeiChatPanel chatPanel;
    private final int activityPanelWidth = 250;
    private final int shopPanelWidth = 500;
    private final int chatPanelWidth = 500;
    private int currentPanelWidth = activityPanelWidth;
    //private double currentPanelWidthPrecise = (float) currentPanelWidth;
    //public int currentPanelVelocity = 0;
    //private int currentPanelTarget = activityPanelWidth;
    private BufferedImage background;
    private String statsText = "";
    private long lastUpdated = System.currentTimeMillis();
    private volatile boolean statsLoaded = false;

    public Menu(Main parent) {

        this.parent = parent;
        this.parent.setMasterTimer(10000);
        this.constantUpdate = true;

        try {
            this.background = ImageIO.read(new File("images/menu-background-2.png"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.startButton = new GeiButton("Start");
        this.startButton.setActionCommand("start");
        this.startButton.addActionListener(this);

        this.activityButton = new GeiButton("Activity");
        this.activityButton.setActionCommand("activity");
        this.activityButton.addActionListener(this);

        this.shopButton = new GeiButton("Shop");
        this.shopButton.setActionCommand("shop");
        this.shopButton.addActionListener(this);

        this.chatButton = new GeiButton("Chat");
        this.chatButton.setActionCommand("chat");
        this.chatButton.addActionListener(this);

        this.logoutButton = new GeiButton("Logout");
        this.logoutButton.setActionCommand("logout");
        this.logoutButton.addActionListener(this);

        try {
            this.recentActionsPanel = new GeiStatsPanel(this.activityPanelWidth, Main.session.user.getJSONArray("actions"));
            this.shopPanel = new GeiShopPanel(this.shopPanelWidth);
            this.chatPanel = new GeiChatPanel(this.chatPanelWidth, Main.session.user.getJSONArray("actions"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.loadingBar = new JProgressBar();
        this.loadingBar.setIndeterminate(true);
        
        this.chatScrollPane = new GeiScrollPane(this.chatPanel);
        this.chatScrollPane.setBorder(null);
        this.chatScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.chatScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));
        this.chatScrollPane.setHorizontalScrollBarPolicy(GeiScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.chatScrollPane.setParent(this);

        this.shopScrollPane = new GeiScrollPane(this.shopPanel);
        this.shopScrollPane.setBorder(null);
        this.shopScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.shopScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));
        this.shopScrollPane.setHorizontalScrollBarPolicy(GeiScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.shopScrollPane.setParent(this);

        this.activityScrollPane = new GeiScrollPane(this.recentActionsPanel);
        this.activityScrollPane.setBorder(null);
        this.activityScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        this.activityScrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));
        this.activityScrollPane.setHorizontalScrollBarPolicy(GeiScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.activityScrollPane.setParent(this);

        this.recentActionsPanel.setParent(this.activityScrollPane);
        this.shopPanel.setParent(this.shopScrollPane);
        this.chatPanel.setParent(this.chatScrollPane);

        this.add(this.loadingBar);
        this.addKeyListener(this);
        this.setFocusable(true);

        Thread loadResources = new Thread(() -> {

            System.out.println("Loading stats...");

            try {
                Main.shopData = Communicator.getShop();

                Iterator keys = Main.shopData.keys();

                String key;
                while (keys.hasNext()){
                    key = keys.next().toString();
                    Main.skinHashMap.put(key, Rah.decodeToImage(Main.shopData.getJSONObject(key).getString("image")));
                }

            } catch (Exception e) {
                Main.errorQuit(e);
            }



            Menu.this.shopPanel.updateItems();
            Menu.this.updateData();
            Menu.this.statsLoaded = true;

            // Waits for stats stuff to load
            Menu.this.repaint();
            Menu.this.remove(Menu.this.loadingBar);
            Menu.this.add(Menu.this.activityScrollPane);

            Menu.this.add(Menu.this.startButton);
            Menu.this.add(Menu.this.activityButton);
            Menu.this.add(Menu.this.shopButton);
            Menu.this.add(Menu.this.chatButton);
            Menu.this.add(Menu.this.logoutButton);

        });

        loadResources.start();

    }

    public void updateData() {
        try {

            JSONObject tempUser = Communicator.refresh(Main.session.getToken());

            Main.session.user = tempUser;
            Main.session.updateJSON();
            Main.messages = Communicator.getMessages();

            System.out.println(tempUser);

            this.statsText = String.format("%s Kills      |      %s Deaths      |      %s Matches      |      %s Zhekko", Main.session.user.getString("kills"), Main.session.user.getString("deaths"), Main.session.user.getString("matches"), Main.session.user.getString("money"));
            this.lastUpdated = System.currentTimeMillis();

            switch (currentPanel) {
                case ACTIVITY:
                    this.recentActionsPanel.update(Main.session.user.getJSONArray("actions"));
                    break;
                case SHOP:
                    this.shopPanel.update(Main.session.user.getJSONArray("skins"));
                case CHAT:
                    this.chatPanel.update(Main.messages);

            }

            this.repaint();

        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public void clearPanels() {
        remove(shopScrollPane);
        remove(chatScrollPane);
        remove(activityScrollPane);
    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":

                System.out.println("SWITCH");

                this.removeKeyListener(this);
                this.parent.startPage(Main.Pages.GAME);

                break;

            case "chat":

                //if (this.currentPanelVelocity == 0) {

                System.out.println("SWITCHED TO CHAT");

                this.currentPanel = SUBPANEL.CHAT;
                this.currentPanelWidth = this.chatPanelWidth;
                //this.currentPanelTarget = this.shopPanelWidth;
                //this.currentPanelVelocity = this.currentPanelTarget > this.currentPanelWidth ? 1 : -1;
                //this.parent.setMasterTimer(500);

                clearPanels();
                add(chatScrollPane);
                this.updateData();

                repaint();

                //}

                break;


            case "shop":

                //if (this.currentPanelVelocity == 0) {

                System.out.println("SWITCHED TO SHOP");

                this.currentPanel = SUBPANEL.SHOP;
                this.currentPanelWidth = this.shopPanelWidth;
                //this.currentPanelTarget = this.shopPanelWidth;
                //this.currentPanelVelocity = this.currentPanelTarget > this.currentPanelWidth ? 1 : -1;
                //this.parent.setMasterTimer(500);

                clearPanels();
                add(shopScrollPane);
                this.updateData();

                repaint();

                //}

                break;

            case "activity":

                //if (this.currentPanelVelocity == 0) {

                System.out.println("SWITCHED TO ACTIVITY");

                this.currentPanel = SUBPANEL.ACTIVITY;
                this.currentPanelWidth = this.activityPanelWidth;
                //this.currentPanelTarget = this.activityPanelWidth;
                //this.currentPanelVelocity = this.currentPanelTarget > this.currentPanelWidth ? 1 : -1;
                //this.parent.setMasterTimer(500);

                clearPanels();
                add(activityScrollPane);
                this.updateData();

                repaint();

                //}

                break;

            case "logout":

                if(JOptionPane.showConfirmDialog (null, "Are you sure you want to logout?","RASTERA Authentication Service", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
                    System.out.println("SWITCH");

                    Main.session = null;
                    this.removeKeyListener(this);
                    this.parent.startPage(Main.Pages.LOGIN);

                }


                break;
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

        if (!this.statsLoaded) {
            this.loadingBar.setBounds(50, this.getHeight() / 2 + 40, this.getWidth() - 100, 20);

            String loadingMessage = "Waiting for server";

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());

            g.setColor(Color.BLACK);
            g.setFont(Main.getFont("Lato-Light", 30));

            FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 30));
            g.drawString(loadingMessage, this.getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);

        } else {

            /*
            if (this.currentPanelVelocity != 0) {

                this.shopButton.setEnabled(false);
                this.activityButton.setEnabled(false);

                double trueVel = this.currentPanelVelocity * Math.max(Math.abs(this.currentPanelWidthPrecise - (double) this.currentPanelTarget) / 5, 0.05);

                System.out.println(trueVel);

                this.currentPanelWidthPrecise += trueVel;
                this.currentPanelWidth = (int) this.currentPanelWidthPrecise;

                if (Math.abs(this.currentPanelWidth - this.currentPanelTarget) <= 5) {
                    this.currentPanelWidth = this.currentPanelTarget;
                    this.currentPanelWidthPrecise = (float) this.currentPanelTarget;
                    this.currentPanelVelocity = 0;
                    this.parent.setMasterTimer(10000);

                    this.shopButton.setEnabled(true);
                    this.activityButton.setEnabled(true);

                    System.out.println("reset");
                    repaint();
                }
            } */

            g.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

            this.parent.updateFrameRate();

            this.startButton.setBounds(20, 10, 150, 40);

            this.activityButton.setBounds(getWidth() - this.currentPanelWidth, getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.shopButton.setBounds(getWidth() - this.currentPanelWidth * 3 / 4, getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.chatButton.setBounds(getWidth() - this.currentPanelWidth * 1 / 2, getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.logoutButton.setBounds(getWidth() - this.currentPanelWidth * 1 / 4, getHeight() - 40, this.currentPanelWidth / 4, 40);

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, Main.w, Main.h);

            int size = Math.max(this.getWidth() - this.currentPanelWidth, this.getHeight() - 60);

            g.drawImage(this.background, 0, 60, size, size, this);

            g.setColor(new Color(5, 15, 24));
            g.fillRect(0, 0, Main.w, 60);

            // Recent Actions panel
            switch (this.currentPanel) {
                case SHOP:
                    this.shopScrollPane.setBounds(getWidth() - this.shopPanelWidth, 60, this.shopPanelWidth, getHeight() - 100);
                    this.shopScrollPane.revalidate();
                    this.shopScrollPane.repaint();
                    break;

                case ACTIVITY:
                    this.activityScrollPane.setBounds(getWidth() - this.activityPanelWidth, 60, this.activityPanelWidth, getHeight() - 100);
                    this.activityScrollPane.revalidate();
                    this.activityScrollPane.repaint();
                    break;

                case CHAT:
                    this.chatScrollPane.setBounds(getWidth() - this.chatPanelWidth, 60, this.chatPanelWidth, getHeight() - 100);
                    this.chatScrollPane.revalidate();
                    this.chatScrollPane.repaint();
                    break;   
            }

            g.setColor(Color.WHITE);

            // Rank badge
            g.setFont(Main.getFont("Lato-Normal", 30));
            g.drawString("" + Main.session.getRank(), Main.w - this.activityPanelWidth + 20, 40);

            // Username
            g.setFont(Main.getFont("Lato-Light", 30));
            g.drawString(Main.session.getUsername(), Main.w - this.activityPanelWidth + 60, 40);

            // Top Bar stats
            g.setFont(Main.getFont("Lato-Light", 20));
            FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 20));
            g.drawString(this.statsText, this.getWidth() / 2 - metrics.stringWidth(this.statsText) / 2, 35);

            // Last updated
            String updateText = "Last sync: " + new Date(this.lastUpdated / 1000).toString();
            g.setColor(new Color(50, 50, 50));
            g.setFont(Main.getFont("Lato-Light", 12));
            g.drawString(updateText, 10, this.getHeight() - 15);

            // Penguin preview
            int dimension = (int) (Math.min(this.getHeight(), this.getWidth()) * 0.6);
            g.drawImage(Main.skinHashMap.get(Main.session.getSkin()), (this.getWidth() - this.currentPanelWidth) / 2 - dimension / 2, (this.getHeight() + 60) / 2 - dimension / 2, dimension, dimension, this);
        }
    }
}