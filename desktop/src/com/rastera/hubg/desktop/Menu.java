// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Menu.java - Main menu

package com.rastera.hubg.desktop;

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

class Menu extends HUBGPanel implements KeyListener, ActionListener {

    // Subpanel config
    private enum SUBPANEL { ACTIVITY, SHOP, CHAT }
    private SUBPANEL currentPanel = SUBPANEL.ACTIVITY;

    // Core UI elements
    private final HUBGButton startButton;
    private final HUBGButton activityButton;
    private final HUBGButton shopButton;
    private final HUBGButton chatButton;
    private final HUBGButton logoutButton;

    private final HUBGEdgeButton minimizeButton;
    private final HUBGEdgeButton closeButton;

    private final HUBGScrollPane activityScrollPane;
    private final HUBGScrollPane chatScrollPane;
    private final HUBGScrollPane shopScrollPane;
    private final JProgressBar loadingBar;
    private HUBGTextField chatTextField;
    private HUBGActionPanel recentActionsPanel;
    private HUBGShopPanel shopPanel;
    private HUBGChatPanel chatPanel;
    private final int activityPanelWidth = 252;
    private final int shopPanelWidth = 500;
    private final int chatPanelWidth = 500;
    private int currentPanelWidth = this.activityPanelWidth;

    private BufferedImage background;

    // Dynamic data
    private String statsText = "-- Kills      |      -- Deaths      |      -- Matches      |      -- Zhekko      |      -- KTD";
    private long lastUpdated = System.currentTimeMillis();
    private volatile boolean statsLoaded = false;

    public Menu(Main parent) {

        // Configs core settings
        this.parent = parent;
        this.parent.setMasterTimer(10000); // 10s timer to update stats
        this.constantUpdate = true;

        // Load resources
        try {
            this.background = ImageIO.read(new File("images/menu-background-2.png"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        // Configure core UI
        this.minimizeButton = new HUBGEdgeButton("-");
        this.minimizeButton.setActionCommand("minimize");
        this.minimizeButton.addActionListener(this);

        this.closeButton = new HUBGEdgeButton("X");
        this.closeButton.setActionCommand("close");
        this.closeButton.addActionListener(this);

        this.startButton = new HUBGButton("Start");
        this.startButton.setActionCommand("start");
        this.startButton.addActionListener(this);

        this.activityButton = new HUBGButton(Util.getScaledIcon("icons/log.png"));
        this.activityButton.setActionCommand("activity");
        this.activityButton.addActionListener(this);

        this.shopButton = new HUBGButton(Util.getScaledIcon("icons/shop.png"));
        this.shopButton.setActionCommand("shop");
        this.shopButton.addActionListener(this);

        this.chatButton = new HUBGButton(Util.getScaledIcon("icons/chat.png"));
        this.chatButton.setActionCommand("chat");
        this.chatButton.addActionListener(this);

        this.logoutButton = new HUBGButton(Util.getScaledIcon("icons/logout.png"));
        this.logoutButton.setActionCommand("logout");
        this.logoutButton.addActionListener(this);

        // Create subpanels
        try {
            this.recentActionsPanel = new HUBGActionPanel(this.activityPanelWidth, Main.session.user.getJSONArray("actions"));
            this.shopPanel = new HUBGShopPanel(this.shopPanelWidth);
            this.chatPanel = new HUBGChatPanel(this.chatPanelWidth, Main.session.user.getJSONArray("actions"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        // Core UI
        this.loadingBar = new JProgressBar();
        this.loadingBar.setIndeterminate(true);
        
        this.chatScrollPane = new HUBGScrollPane(this.chatPanel);
        this.chatScrollPane.setParent(this);
        this.chatTextField = new HUBGTextField();

        this.shopScrollPane = new HUBGScrollPane(this.shopPanel);
                this.shopScrollPane.setParent(this);

        this.activityScrollPane = new HUBGScrollPane(this.recentActionsPanel);
        this.activityScrollPane.setParent(this);

        this.recentActionsPanel.setParent(this.activityScrollPane);
        this.shopPanel.setParent(this.shopScrollPane);
        this.chatPanel.setParent(this.chatScrollPane);

        this.add(this.loadingBar);
        this.addKeyListener(this);
        this.setFocusable(true);

        // Adds edge button if borderless
        if (Main.borderless) {
            this.add(Menu.this.minimizeButton);
            this.add(Menu.this.closeButton);
        }

        // Submit chat message on enter
        this.chatTextField.addActionListener(e -> this.sendMessage());

        // Separate thread to enable loading screen
        Thread loadResources = new Thread(() -> {

            System.out.println("Loading stats...");

            // Downloads stop data
            try {
                Main.shopData = Communicator.getShop();

                Iterator keys = Main.shopData.keys();

                String key;
                while (keys.hasNext()){
                    key = keys.next().toString();

                    // Decodes skins from base64 to buffered image
                    Main.skinHashMap.put(key, Util.decodeToImage(Main.shopData.getJSONObject(key).getString("image")));
                }

            } catch (Exception e) {
                Main.errorQuit(e);
            }

            Menu.this.shopPanel.updateItems();
            Menu.this.statsLoaded = true;

            // Waits for stats stuff to load
            Menu.this.repaint();
            Menu.this.remove(Menu.this.loadingBar);
            Menu.this.add(Menu.this.activityScrollPane);

            Menu.this.updateData();

            // Start buttons
            Menu.this.add(Menu.this.startButton);
            Menu.this.add(Menu.this.activityButton);
            Menu.this.add(Menu.this.shopButton);
            Menu.this.add(Menu.this.chatButton);
            Menu.this.add(Menu.this.logoutButton);

            this.minimizeButton.setBackgroundDark();
            this.closeButton.setBackgroundDark();
            this.resetButtons();
        });

        loadResources.start();

        if (!Main.musicPlaying()) {
            Main.startMusic();
        }
    }

    // Send chat message
    public void sendMessage() {
        String message = this.chatTextField.getText();
        this.chatTextField.setText("");

        if (message != null && message.length() > 0) {

            JSONObject newMessages = Communicator.sendMessage(message);
            HUBGChatPanel.updateMessages(newMessages);
            this.chatPanel.update(Main.session.messages);
            this.chatScrollPane.revalidate();
            this.chatScrollPane.repaint();

        }
    }

    // Update dynamic data
    public void updateData() {

        // Separate thread to prevent slowdown
        Thread data = new Thread(() -> {
            try {
                // Sends token to authenticate
                JSONObject tempUser = Communicator.refresh(Main.session.getToken());

                // Refreshes user object/session
                Main.session.user = tempUser;
                Main.session.updateJSON();

                // Update messages
                HUBGChatPanel.updateMessages(Communicator.getMessages());

                // Updates UI
                Menu.this.statsText = String.format("%s Kills      |      %s Deaths      |      %.2f KTD      |      %s Zhekko", Main.session.user.getString("kills"), Main.session.user.getString("deaths"), Main.session.user.getDouble("kills") / Math.max(1, Main.session.user.getDouble("deaths")), Main.session.user.getString("money"));
                Menu.this.lastUpdated = System.currentTimeMillis();

                // Updates subpanel
                switch (this.currentPanel) {
                    case ACTIVITY:
                        Menu.this.recentActionsPanel.update(Main.session.user.getJSONArray("actions"));
                        break;
                    case SHOP:
                        Menu.this.shopPanel.update(Main.session.user.getJSONArray("skins"));
                    case CHAT:
                        Menu.this.chatPanel.update(Main.session.messages);

                }

                Menu.this.repaint();

            } catch (Exception e) {
                Main.errorQuit(e);
            }
        });

        data.start();

    }

    // Clear all subpanels
    public void clearPanels() {
        this.parent.setMasterTimer(10000);

        this.remove(this.shopScrollPane);
        this.remove(this.chatScrollPane);
        this.remove(this.activityScrollPane);
        this.remove(this.chatTextField);

        this.shopButton.selected = false;
        this.activityButton.selected = false;
        this.chatButton.selected = false;
    }

    // Clear subpanel buttons
    public void resetButtons() {
        switch (this.currentPanel) {
            case ACTIVITY:
                this.activityButton.selected = true;
                //this.activityButton.setEnabled(false);
                break;
            case SHOP:
                this.shopButton.selected = true;
                //this.shopButton.setEnabled(false);
                break;
            case CHAT:
                this.chatButton.selected = true;
                //this.chatButton.setEnabled(false);
                break;
        }
    }

    // Button actions
    public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {

            case "start":

                System.out.println("SWITCH");

                this.removeKeyListener(this);
                this.parent.startPage(Main.Pages.GAME);

                break;

            case "chat":

                System.out.println("SWITCHED TO CHAT");

                this.currentPanel = SUBPANEL.CHAT;
                this.currentPanelWidth = this.chatPanelWidth;

                this.clearPanels();
                this.add(this.chatScrollPane);
                this.add(this.chatTextField);
                this.updateData();
                this.parent.setMasterTimer(2000);

                this.repaint();
                this.chatTextField.requestFocus();

                break;


            case "shop":

                System.out.println("SWITCHED TO SHOP");

                this.currentPanel = SUBPANEL.SHOP;
                this.currentPanelWidth = this.shopPanelWidth;


                this.clearPanels();
                this.add(this.shopScrollPane);
                this.updateData();

                this.repaint();

                break;

            case "activity":

                System.out.println("SWITCHED TO ACTIVITY");

                this.currentPanel = SUBPANEL.ACTIVITY;
                this.currentPanelWidth = this.activityPanelWidth;

                this.clearPanels();
                this.add(this.activityScrollPane);
                this.updateData();

                this.repaint();

                break;

            case "logout":

                if(JOptionPane.showConfirmDialog (Util.checkParent(this.parent), "Are you sure you want to logout?","RASTERA Authentication Service", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION){
                    System.out.println("SWITCH");

                    Session.destroySession();
                    this.removeKeyListener(this);
                    this.parent.startPage(Main.Pages.LOGIN);
                }

                break;

            case "minimize":
                this.parent.minimize();
                break;

            case "close":
                this.parent.close();
                break;
        }

        this.resetButtons();

    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        // Enable anti alias
        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Edge button config
        this.minimizeButton.setBounds(this.getWidth() - 40, 0, 20, 20);
        this.closeButton.setBounds(this.getWidth() - 20, 0, 20, 20);

        // Loading screen
        if (!this.statsLoaded) {
            this.loadingBar.setBounds(50, this.getHeight() / 2 + 40, this.getWidth() - 100, 20);

            String loadingMessage = "Downloading Resources";

            int size = Math.max(this.getWidth(), this.getHeight());

            g.drawImage(this.background, 0, 0, size, size, this);

            g.setColor(Color.BLACK);
            g.setFont(Main.getFont("Lato-Light", 30));

            FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 30));
            g.drawString(loadingMessage, this.getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);

        } else {

            this.parent.updateFrameRate();

            // Sets JButton positions
            this.startButton.setBounds(20, 10, 150, 40);

            this.activityButton.setBounds(this.getWidth() - this.currentPanelWidth, this.getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.shopButton.setBounds(this.getWidth() - this.currentPanelWidth * 3 / 4, this.getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.chatButton.setBounds(this.getWidth() - this.currentPanelWidth / 2, this.getHeight() - 40, this.currentPanelWidth / 4, 40);
            this.logoutButton.setBounds(this.getWidth() - this.currentPanelWidth / 4, this.getHeight() - 40, this.currentPanelWidth / 4, 40);

            // Background image
            int size = Math.max(this.getWidth() - this.currentPanelWidth, this.getHeight() - 60);
            g.drawImage(this.background, 0, 60, size, size, this);

            // Topbar
            g.setColor(new Color(5, 15, 24));
            g.fillRect(0, 0, Main.w, 60);

            // Subpanel update
            switch (this.currentPanel) {
                case SHOP:
                    this.shopScrollPane.setBounds(this.getWidth() - this.shopPanelWidth, 60, this.shopPanelWidth + 1, this.getHeight() - 100);
                    this.shopScrollPane.revalidate();
                    this.shopScrollPane.repaint();
                    break;

                case ACTIVITY:
                    this.activityScrollPane.setBounds(this.getWidth() - this.activityPanelWidth, 60, this.activityPanelWidth + 1, this.getHeight() - 100);
                    this.activityScrollPane.revalidate();
                    this.activityScrollPane.repaint();
                    break;

                case CHAT:
                    this.chatScrollPane.setBounds(this.getWidth() - this.chatPanelWidth, 60, this.chatPanelWidth + 1, this.getHeight() - 130);
                    this.chatScrollPane.revalidate();
                    this.chatScrollPane.repaint();
                    this.chatTextField.setBounds(this.getWidth() - this.chatPanelWidth, this.getHeight() - 70, this.chatPanelWidth, 30);
                    break;   
            }

            // Text data
            g.setColor(Color.WHITE);

            // Top Bar stats
            g.setFont(Main.getFont("Lato-Light", 20));
            FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 20));
            g.drawString(this.statsText, this.getWidth() / 2 - metrics.stringWidth(this.statsText) / 2, 35);

            // Last updated
            String updateText = String.format("Last sync: %s   |   Users online: %d   |   Logged in as: %s", new Date(this.lastUpdated).toString(), Main.usersOnline, Main.session.getUsername());
            g.setColor(new Color(50, 50, 50));
            g.setFont(Main.getFont("Lato-Light", 12));
            g.drawString(updateText, 10, this.getHeight() - 10);

            // Skin preview
            int dimension = (int) (Math.min(this.getHeight(), this.getWidth()) * 0.6);
            g.drawImage(Main.skinHashMap.get(Main.session.getSkin()), (this.getWidth() - this.currentPanelWidth) / 2 - dimension / 2, (this.getHeight() + 60) / 2 - dimension / 2, dimension, dimension, this);
        }
    }
}