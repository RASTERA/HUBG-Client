package com.rastera.hubg.desktop;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.*;

class GeiShopItem {

    public String name, description;
    public long cost;
    public BufferedImage texture;
    public boolean unlocked;
    public static final int height = 150;
    public static int width = 500;
    public GeiPanel parent;

    public GeiButton buyButton, useButton;

    public GeiShopItem(GeiPanel parent, String name, JSONObject data) {
        this.parent = parent;

        try {
            this.description = data.getString("description");
            this.name = name;
            this.cost = data.getLong("cost");
            this.texture = Rah.decodeToImage(data.getString("image"));
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.buyButton = new GeiButton(String.format("Z$%d", this.cost));
        this.buyButton.setActionCommand("buy-" + this.name);
        this.buyButton.addActionListener((ActionListener) this.parent);

        this.useButton = new GeiButton(String.format("EQUIP", this.cost));
        this.useButton.setActionCommand("use-" + this.name);
        this.useButton.addActionListener((ActionListener) this.parent);

        this.updateButtonState();

        this.parent.add(this.buyButton);
        this.parent.add(this.useButton);
    }

    public void updateButtonState() {

        if (this.unlocked) {
            this.buyButton.setEnabled(false);
            //this.buyButton.setText("PURCHASED");

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

    public void update(Graphics graphics, int x, int y, int width) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        this.buyButton.setBounds(x + 140, y + 85, 120, 30);
        this.useButton.setBounds(x + 270, y + 85, 120, 30);

        GeiShopItem.width = width; // Dynamically changes width based on presence of nasty scrollbar

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, GeiShopItem.width, height);

        g.drawImage(this.texture, x + 20, y + 25, 100, 100, null);

        g.setColor(Color.WHITE);

        g.setFont(Main.getFont("Lato-Light", 30));
        g.drawString(this.name, x + 140, y + 55);

        g.setFont(Main.getFont("Lato-Light", 15));
        g.drawString(this.description, x + 141, y + 73);


    }
}

class GeiActionEvent {

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
            return color;
        }
    }

    public static final int height = 80;
    private static int width = 210;

    private String caption;
    private String time;
    private Type type;

    public GeiActionEvent(Type type, String caption, String time) {
        this.caption = caption;
        this.time = time;
        this.type = type;
    }

    public void update(Graphics graphics, int x, int y, int width) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        GeiActionEvent.width = width; // Dynamically changes width based on presence of nasty scrollbar

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, GeiActionEvent.width, height);

        g.setFont(Main.getFont("Lato-Light", 15));

        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 15));
        ArrayList<String> lines = Rah.wrapText(GeiActionEvent.width, this.caption, metrics);
        g.setColor(Color.WHITE);

        for (int i = 0; i < lines.size(); i++) {
            g.drawString(lines.get(i), x + 10, y + 20 + (metrics.getHeight() + 2) * i);
        }

        g.setColor(new Color(100, 100, 100));
        g.drawString(this.time, x + 10, y + height - 8);

        g.setColor(this.type.getColor());
        g.fillRect(GeiActionEvent.width - 3 + x, y, 3, height);

    }
}

class GeiButton extends JButton {

    public GeiButton(ImageIcon icon) {
        super(icon);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        /*
        this.setForeground(new Color(200, 200, 200));
        this.setBackground(new Color(30, 35, 40));
        this.setFocusPainted(false);

        this.addChangeListener(evt -> {
            if (this.getModel().isPressed()) {
                this.setBackground(new Color(30, 35, 40));
            } else if (GeiButton.this.getModel().isRollover()) {
                this.setBackground(new Color(30, 35, 40));
            } else {
                this.setBackground(new Color(30, 35, 40));
            }
        }); */
    }

    public GeiButton(String text) {
        super(text);

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.setForeground(new Color(200, 200, 200));
		this.setBackground(new Color(30, 35, 40));
		this.setFocusPainted(false);

		this.addChangeListener(evt -> {
			if (this.getModel().isPressed()) {
				this.setBackground(new Color(30, 35, 40));
			} else if (GeiButton.this.getModel().isRollover()) {
				this.setBackground(new Color(30, 35, 40));
			} else {
				this.setBackground(new Color(30, 35, 40));
			}
		});
    }
}

abstract class GeiPanel extends JPanel {

    Main parent;
    public boolean constantUpdate = true;

}

class GeiActionPanel extends GeiPanel {

    private ArrayList<GeiActionEvent> eventArrayList = new ArrayList<>();
    private final int width;
    private GeiScrollPane parent;
    private long currentTime;

    public GeiActionPanel(int width, JSONArray actionArray) {
        this.width = width;

        this.update(actionArray);
    }

    public void update(JSONArray actionArray) {

        this.currentTime = System.currentTimeMillis();
        this.eventArrayList = new ArrayList<>();

        try {
            JSONObject actionObject;
            for (int i = actionArray.length() - 1; i > -1; i--) {
                actionObject = actionArray.getJSONObject(i);

                this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.valueOf(actionObject.getString("type")), actionObject.getString("caption"), Rah.getTimestamp(actionObject.getLong("date"))));
            }

            if (actionArray.length() == 0) {
                this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Nothing to see here :)", Rah.getTimestamp(this.currentTime)));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.setPreferredSize(new Dimension(this.width, 60 + this.eventArrayList.size() * (GeiActionEvent.height + 10)));


    }

    public void setParent(GeiScrollPane parent) {
        this.parent = parent;
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

        boolean scrollEnabled = 60 + this.eventArrayList.size() * (GeiActionEvent.height + 10) > this.parent.getHeight();

        for (int y = 0; y < this.eventArrayList.size(); y++) {
            this.eventArrayList.get(y).update(g, 20, 40 + y * (GeiActionEvent.height + 10), scrollEnabled ? 205 : 210);
        }
    }
}


class GeiChatItem {

    public static int height = 50;
    public static int width = 500;

    private String text;
    private String time;

    public GeiChatItem(String text, String time) {
        this.text = text;
        this.time = time;
    }

    public void update(Graphics graphics, int x, int y, int width) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 15));
        ArrayList<String> lines = Rah.wrapText(GeiChatItem.width, text, metrics);

        height = 50 + 10 * lines.size();
        GeiChatItem.width = width; // Dynamically changes width based on presence of nasty scrollbar

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, GeiChatItem.width, height);

        g.setFont(Main.getFont("Lato-Light", 15));




        g.setColor(Color.WHITE);

        for (int i = 0; i < lines.size(); i++) {
            g.drawString(lines.get(i), x + 10, y + 20 + (metrics.getHeight() + 2) * i);
        }

        g.setColor(new Color(100, 100, 100));
        g.drawString(this.time, x + 10, y + height - 8);

    }
}

class GeiChatPanel extends GeiPanel {

    private ArrayList<GeiChatItem> chatArrayList = new ArrayList<>();
    private final int width;
    private GeiScrollPane parent;
    private long currentTime;
    private JSONArray chatArray;

    public GeiChatPanel(int width, JSONArray chatArray) {
        this.width = width;
        this.chatArray = chatArray;
    }

    public static void updateMessages(JSONObject newMessages) {
        try {

            if (newMessages.has("messages")) {

                JSONArray messages = newMessages.getJSONArray("messages");

                boolean located;
                JSONObject existingMsg, newMsg;
                for (int i = messages.length() - 1; i > -1; i--) {

                    located = false;

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
                    } else {
                        break;
                    }
                }

            } else {
                Main.errorQuit(newMessages.getString("error"));
            }


        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public void update(JSONArray chatArray) {

        this.currentTime = System.currentTimeMillis();
        this.chatArrayList = new ArrayList<>();

        try {
            for (int i = chatArray.length() - 1; i > -1; i--) {
                this.chatArrayList.add(new GeiChatItem(chatArray.getJSONObject(i).getString("message"), Rah.getTimestamp(chatArray.getJSONObject(i).getLong("time"))));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }
    }

    public void setParent(GeiScrollPane parent) {
        this.parent = parent;
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        this.setPreferredSize(new Dimension(this.width, 20 + this.chatArrayList.size() * (GeiChatItem.height + 20)));

        int totalHeight = 0;
        int yPos = 20;

        // Determine expected chat height
        for (int i = 0; i < chatArrayList.size(); i++) {
            totalHeight += this.chatArrayList.size() * (GeiChatItem.height + 20);
        }

        boolean scrollEnabled = 60 + totalHeight > this.parent.getHeight();

        // Updates chat items
        for (int i = 0; i < chatArrayList.size(); i++) {
            chatArrayList.get(i).update(g, 20, yPos, scrollEnabled ? 455 : 460);

            yPos += GeiChatItem.height + 10;
        }
    }

}


class GeiShopPanel extends GeiPanel implements ActionListener {

    private ArrayList<GeiShopItem> itemArrayList = new ArrayList<>();
    private int width;
    private GeiScrollPane parent;

    public GeiShopPanel(int width) {
        this.width = width;
    }

    public void actionPerformed(ActionEvent e) {

        String[] eventSource = e.getActionCommand().split("-");

        for (GeiShopItem item : itemArrayList) {
            if (eventSource[1].equals(item.name)) {

                if (eventSource[0].equals("buy")) {
                    if(JOptionPane.showConfirmDialog (Rah.checkParent(this.parent), String.format("Are you sure you want to buy %s for Z$%d?", item.name, item.cost),"HUBG Shop", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION){
                        return;
                    }

                    item.buyButton.setEnabled(false);
                    item.buyButton.setText("Loading");

                } else {
                    item.useButton.setEnabled(false);
                    item.useButton.setText("Loading");
                }

                String response = Communicator.shopRequest(parent.getParent().parent, eventSource[0], eventSource[1]);

                System.out.println(response);

                if (response.equals("ok")) {
                    this.parent.getParent().updateData();
                    item.updateButtonState();

                    if (eventSource[0].equals("buy")) {
                        JOptionPane.showMessageDialog(Rah.checkParent(this.parent.getParent().parent), "Successfully purchased: " + eventSource[1], "HUBG Shop", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(Rah.checkParent(this.parent.getParent().parent), response, "HUBG Shop", JOptionPane.ERROR_MESSAGE);
                }

            }
        }

    }

    public void updateItems() {

        Iterator keys = Main.shopData.keys();

        String key;

        try {
            while (keys.hasNext()) {
                key = keys.next().toString();
                itemArrayList.add(new GeiShopItem(this, key, Main.shopData.getJSONObject(key)));
            }
        } catch (Exception e) {
            Main.errorQuit(e);
        }

        this.setPreferredSize(new Dimension(this.width, 20 + this.itemArrayList.size() * (GeiShopItem.height + 10)));

    }

    public void update(JSONArray purchasedSkins) {

        try {
            for (GeiShopItem item : itemArrayList) {
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

    public void setParent(GeiScrollPane parent) {
        this.parent = parent;
    }

    @Override
    public void paintComponent(Graphics graphics) {

        Graphics2D g = (Graphics2D) graphics;

        g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);


        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());

        boolean scrollEnabled = 60 + this.itemArrayList.size() * (GeiShopItem.height + 10) > this.parent.getHeight();

        for (int i = 0; i < itemArrayList.size(); i++) {
            itemArrayList.get(i).update(g, 20, 20 + 160 * i, scrollEnabled ? 455 : 460);
        }

    }

}

class GeiScrollPane extends JScrollPane {
    private Menu parent;

    public GeiScrollPane(GeiPanel child) {
        super(child);
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Menu getParent() {
        return this.parent;
    }
}

class GeiTextField extends JTextField {
    public GeiTextField() {

        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //this.setForeground(new Color(92, 91, 87));
        //this.setBackground(new Color(30, 35, 40));

        /*
        addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent evt) {
                if (getModel().isPressed()) {
                    setBackground(new Color(30, 35, 40));
                } else if (getModel().isRollover()) {
                    setBackground(new Color(30, 35, 40));
                } else {
                    setBackground(new Color(30, 35, 40));
                }
            }
        }); */


    }
}