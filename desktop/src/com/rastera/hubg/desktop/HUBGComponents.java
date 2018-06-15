// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// HUBGComponents.java - Custon UI elements

package com.rastera.hubg.desktop;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

// Events in action panel
class HUBGActionEvent {

    // Color based on event type
    public enum Type {
        KILL(new Color(0, 63, 255)),
        KILLED(new Color(135, 6, 0)),
        WIN(new Color(30, 165, 0)),
        INFO(new Color(200, 200, 0));

        private Color color;

        Type(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return this.color;
        }
    }

    public static final int height = 80;
    private static final int width = 210;

    private String caption;
    private String time;
    private Type type;

    public HUBGActionEvent(Type type, String caption, String time) {
        this.caption = caption;
        this.time = time;
        this.type = type;
    }

    // Update graphics
    public void update(Graphics graphics, int x, int y) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, HUBGActionEvent.width, height);

        g.setFont(Main.getFont("Lato-Light", 15));

        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 15));
        ArrayList<String> lines = Util.wrapText(HUBGActionEvent.width, this.caption, metrics);
        g.setColor(Color.WHITE);

        // Multiline
        for (int i = 0; i < Math.min(lines.size(), 2); i++) {
            g.drawString(lines.get(i), x + 10, y + 20 + (metrics.getHeight() + 2) * i);
        }

        g.setColor(new Color(100, 100, 100));
        g.drawString(this.time, x + 10, y + height - 8);

        g.setColor(this.type.getColor());
        g.fillRect(HUBGActionEvent.width - 3 + x, y, 3, height);

    }
}

// Edge UI button - Borderless mode
class HUBGEdgeButton extends JButton {

    private final Color backgroundDark = new Color(1, 10, 19);
    private final Color foregroundDark = new Color(200, 200, 200);

    private final Color backgroundLight = new Color(200, 200, 200);
    private final Color foregroundLight = new Color(1, 10, 19);

    private Color currentBackground;

    public HUBGEdgeButton(ImageIcon icon) {
        super(icon);
        this.init();
    }

    public HUBGEdgeButton(String text) {
        super(text);
        this.init();
    }

    // Centralized init method
    public void init() {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.setMargin(new Insets(0,0,0,0));
        this.setBorderPainted(false);
        this.setFocusPainted(false);

        this.setBackgroundLight();

        // Enforces solid background
        this.addChangeListener(evt -> {
            if (this.getModel().isPressed()) {
                this.setBackground(this.currentBackground);
            } else if (HUBGEdgeButton.this.getModel().isRollover()) {
                this.setBackground(this.currentBackground);
            } else {
                this.setBackground(this.currentBackground);
            }
        });
    }

    // Change colors based on existing background
    public void setBackgroundDark() {
        this.setColor(this.backgroundDark, this.foregroundDark);
    }

    public void setBackgroundLight() {
        this.setColor(this.backgroundLight, this.foregroundLight);
    }

    public void setColor(Color backgroundColor, Color textColor) {
        this.setForeground(textColor);
        this.setBackground(backgroundColor);
        this.currentBackground = backgroundColor;
    }
}


// General button with custom skin
class HUBGButton extends JButton {

    // For menu bars
    public boolean selected = false;

    public HUBGButton(ImageIcon icon) {
        super(icon);

        this.init();
    }

    public HUBGButton(String text) {
        super(text);

        this.init();
    }

    // Centralized initialization
    public void init() {
        super.setContentAreaFilled(false);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.setForeground(new Color(200, 200, 200));
        this.setBackground(new Color(30, 35, 40));
        this.setFocusPainted(false);

        this.setBorderPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {

        // Enforce solid background
        if (this.getModel().isPressed() || this.selected) {
            g.setColor(new Color(20, 25, 30));
        } else if (this.getModel().isRollover()) {
            g.setColor(new Color(40, 45, 50));
        } else {
            g.setColor(new Color(30, 35, 40));
        }

        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        super.paintComponent(g);
    }
}

// JPanel with timer parameters
abstract class HUBGPanel extends JPanel {
    Main parent;
    public boolean constantUpdate = true;
}

// Displays recent actions
class HUBGActionPanel extends HUBGPanel {

    private ArrayList<HUBGActionEvent> eventArrayList = new ArrayList<>();
    private final int width;

    public HUBGActionPanel(int width, JSONArray actionArray) {
        this.width = width;

        this.update(actionArray);
    }

    // Update recent activities from server provided JSONArray
    public void update(JSONArray actionArray) {

        // Updates timestamp
        long currentTime = System.currentTimeMillis();

        // ArrayList of new ActionEvents
        this.eventArrayList = new ArrayList<>();

        try {
            JSONObject actionObject;

            // Generate objects
            for (int i = actionArray.length() - 1; i > -1; i--) {
                actionObject = actionArray.getJSONObject(i);

                this.eventArrayList.add(new HUBGActionEvent(HUBGActionEvent.Type.valueOf(actionObject.getString("type")), actionObject.getString("caption"), Util.getTimestamp(actionObject.getLong("date"))));
            }

            if (actionArray.length() == 0) {
                this.eventArrayList.add(new HUBGActionEvent(HUBGActionEvent.Type.INFO, "Nothing to see here :)", Util.getTimestamp(currentTime)));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        // Set size for parent scroll
        this.setPreferredSize(new Dimension(this.width, 60 + this.eventArrayList.size() * (HUBGActionEvent.height + 10)));
    }

    public void setParent(HUBGScrollPane parent) {
        HUBGScrollPane parent1 = parent;
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        g.setColor(Color.WHITE);
        g.setFont(Main.getFont("Lato-Light", 15));
        g.drawString("Recent Activity", 20, 30);

        for (int y = 0; y < this.eventArrayList.size(); y++) {
            this.eventArrayList.get(y).update(g, 20, 40 + y * (HUBGActionEvent.height + 10));
        }
    }
}

// Chat item
class HUBGChatItem {

    public static int height = 50;
    public final static int width = 460;

    private String text;
    private String time;

    public HUBGChatItem(String text, String time) {
        this.text = text;
        this.time = time;
    }

    public void update(Graphics graphics, int x, int y) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 15));
        ArrayList<String> lines = Util.wrapText(HUBGChatItem.width, this.text, metrics);

        height = 50 + 10 * lines.size();

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, HUBGChatItem.width, height);

        g.setFont(Main.getFont("Lato-Light", 15));

        g.setColor(Color.WHITE);

        for (int i = 0; i < lines.size(); i++) {
            g.drawString(lines.get(i), x + 10, y + 20 + (metrics.getHeight() + 2) * i);
        }

        g.setColor(new Color(100, 100, 100));
        g.drawString(this.time, x + 10, y + height - 8);

    }
}

// Chat panel
class HUBGChatPanel extends HUBGPanel {

    private ArrayList<HUBGChatItem> chatArrayList = new ArrayList<>();
    private final int width;
    private HUBGScrollPane parent;

    public HUBGChatPanel(int width, JSONArray chatArray) {
        this.width = width;
        JSONArray chatArray1 = chatArray;
    }

    // Update messages given JSONObject
    // Updates existing messages with new ones
    public static void updateMessages(JSONObject newMessages) {
        try {
            // Check if request was successful
            if (newMessages.has("messages")) {

                JSONArray messages = newMessages.getJSONArray("messages");

                boolean located;
                JSONObject existingMsg, newMsg;

                // Iterates and adds new messages to list
                for (int i = 0; i < messages.length(); i++) {

                    located = false;

                    // Checks if message already exists in local storage
                    for (int m = Main.session.messages.length() - 1; m > -1; m--) {
                        existingMsg = Main.session.messages.getJSONObject(m);
                        newMsg = messages.getJSONObject(i);
                        if (existingMsg.getString("message").equals(newMsg.getString("message")) && existingMsg.getLong("time") == newMsg.getLong("time")) {
                            located = true;
                            break;
                        }
                    }

                    if (!located) {
                        Main.session.messages.put(messages.getJSONObject(i));
                    }
                }

            } else {
                Main.errorQuit(newMessages.getString("error"));
            }

        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    // Generate ChatItems from currently known messages
    public void update(JSONArray chatArray) {

        long currentTime = System.currentTimeMillis();
        this.chatArrayList = new ArrayList<>();

        try {
            // Display messages in reverse order
            for (int i = chatArray.length() - 1; i > -1; i--) {
                this.chatArrayList.add(new HUBGChatItem(chatArray.getJSONObject(i).getString("message"), Util.getTimestamp(chatArray.getJSONObject(i).getLong("time"))));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.setPreferredSize(new Dimension(this.width, 20 + this.chatArrayList.size() * (HUBGChatItem.height + 10)));
        this.parent.revalidate();
    }

    public void setParent(HUBGScrollPane parent) {
        this.parent = parent;
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        int yPos = 20;

        // Updates chat items
        for (HUBGChatItem item : this.chatArrayList) {
            item.update(g, 20, yPos);

            yPos += HUBGChatItem.height + 10;
        }
    }
}


// Shop item
class HUBGShopItem {

    public String name, description;
    public long cost;
    public BufferedImage texture;
    public boolean unlocked;
    public static final int height = 150;
    public static final int width = 460;
    public HUBGPanel parent;

    public HUBGButton buyButton, useButton;

    public HUBGShopItem(HUBGPanel parent, String name, JSONObject data) {
        this.parent = parent;

        // Extracts data from JSON
        try {
            this.description = data.getString("description");
            this.name = name;
            this.cost = data.getLong("cost");
            this.texture = Util.decodeToImage(data.getString("image"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        // Core UI
        this.buyButton = new HUBGButton(String.format("Z$%d", this.cost));
        this.buyButton.setActionCommand("buy-" + this.name);
        this.buyButton.addActionListener((ActionListener) this.parent);

        this.useButton = new HUBGButton(String.format("EQUIP", this.cost));
        this.useButton.setActionCommand("use-" + this.name);
        this.useButton.addActionListener((ActionListener) this.parent);

        this.updateButtonState();

        this.parent.add(this.buyButton);
        this.parent.add(this.useButton);
    }

    // Updates button upon data update
    public void updateButtonState() {

        if (this.unlocked) {
            this.buyButton.setEnabled(false);

            if (Main.session.getSkin().equals(this.name)) {
                this.useButton.setText("EQUIPPED");
                this.useButton.setEnabled(false);
            } else {
                this.useButton.setText("EQUIP");
                this.useButton.setEnabled(true);
            }
        } else {
            this.useButton.setEnabled(false);
            this.useButton.setText("\uD83D\uDD12 LOCKED");

            if (Main.session.getMoney() - this.cost <= 0) {
                this.buyButton.setEnabled(false);
            } else {
                this.buyButton.setEnabled(true);
            }
        }

    }

    // Updates graphics
    public void update(Graphics graphics, int x, int y) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        this.buyButton.setBounds(x + 140, y + 85, 120, 30);
        this.useButton.setBounds(x + 270, y + 85, 120, 30);

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, HUBGShopItem.width, height);

        g.drawImage(this.texture, x + 20, y + 25, 100, 100, null);

        g.setColor(Color.WHITE);

        g.setFont(Main.getFont("Lato-Light", 30));
        g.drawString(this.name, x + 140, y + 55);

        g.setFont(Main.getFont("Lato-Light", 15));
        g.drawString(this.description, x + 141, y + 73);
    }
}

// Shop panel
class HUBGShopPanel extends HUBGPanel implements ActionListener {

    // Shop items
    private ArrayList<HUBGShopItem> itemArrayList = new ArrayList<>();

    private int width;
    private HUBGScrollPane parent;

    public HUBGShopPanel(int width) {
        this.width = width;
    }

    // Button pressed
    public void actionPerformed(ActionEvent e) {

        String[] eventSource = e.getActionCommand().split("-");

        // Looks for origin item
        for (HUBGShopItem item : this.itemArrayList) {

            // Determines action type
            if (eventSource[1].equals(item.name)) {

                // Confirms action and requests permission from server
                if (eventSource[0].equals("buy")) {
                    if(JOptionPane.showConfirmDialog (Util.checkParent(this.parent), String.format("Are you sure you want to buy %s for Z$%d?", item.name, item.cost),"HUBG Shop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION){
                        return;
                    }

                    item.buyButton.setEnabled(false);
                    item.buyButton.setText("Loading");

                } else {
                    item.useButton.setEnabled(false);
                    item.useButton.setText("Loading");
                }

                String response = Communicator.shopRequest(this.parent.getParent().parent, eventSource[0], eventSource[1]);

                // Checks server's response
                if (response.equals("ok")) {
                    this.parent.getParent().updateData();
                    item.updateButtonState();

                    if (eventSource[0].equals("buy")) {
                        JOptionPane.showMessageDialog(Util.checkParent(this.parent.getParent().parent), "Successfully purchased: " + eventSource[1], "HUBG Shop", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Util.checkParent(this.parent.getParent().parent), response, "HUBG Shop", JOptionPane.ERROR_MESSAGE);
                }

            }
        }

    }

    // Generate shop items from JSON
    public void updateItems() {

        Iterator keys = Main.shopData.keys();

        String key;

        try {
            while (keys.hasNext()) {
                key = keys.next().toString();
                this.itemArrayList.add(new HUBGShopItem(this, key, Main.shopData.getJSONObject(key)));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.setPreferredSize(new Dimension(this.width, 20 + this.itemArrayList.size() * (HUBGShopItem.height + 10)));

    }

    // Update states from new JSON data
    public void update(JSONArray purchasedSkins) {

        try {
            for (HUBGShopItem item : this.itemArrayList) {
                item.unlocked = false;
                item.updateButtonState();

                for (int i = 0; i < purchasedSkins.length(); i++) {
                    if (item.name.equals(purchasedSkins.getString(i))) {
                        item.unlocked = true;
                        item.updateButtonState();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            Main.errorQuit(e);
        }

    }

    public void setParent(HUBGScrollPane parent) {
        this.parent = parent;
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        for (int i = 0; i < this.itemArrayList.size(); i++) {
            this.itemArrayList.get(i).update(g, 20, 20 + 160 * i);
        }
    }
}

// Scroll pane
class HUBGScrollPane extends JScrollPane {
    private Menu parent;

    public HUBGScrollPane(HUBGPanel child) {
        super(child);

        this.setBorder(null);
        this.getVerticalScrollBar().setUnitIncrement(16);
        this.setHorizontalScrollBarPolicy(HUBGScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.getVerticalScrollBar().setPreferredSize(new Dimension(1, Integer.MAX_VALUE));
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Menu getParent() {
        return this.parent;
    }
}

// Text field
class HUBGTextField extends JTextField {
    public HUBGTextField() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    }
}