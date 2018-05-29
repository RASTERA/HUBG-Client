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

    private GeiButton buyButton, useButton;

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

        this.buyButton = new GeiButton(String.format("Buy: Z$%d", this.cost));
        this.buyButton.setActionCommand("buy-" + this.name);
        this.buyButton.addActionListener((ActionListener) this.parent);

        this.useButton = new GeiButton(String.format("Wear", this.cost));
        this.useButton.setActionCommand("use-" + this.name);
        this.useButton.addActionListener((ActionListener) this.parent);

		this.updateButtonState();

        this.parent.add(this.buyButton);
        this.parent.add(this.useButton);
    }

    public void updateButtonState() {
		if (this.unlocked) {
			this.buyButton.setEnabled(false);
			this.useButton.setEnabled(true);
		} else {
			this.useButton.setEnabled(false);
			this.buyButton.setEnabled(true);
		}
	}

    public void update(Graphics g, int x, int y, int width) {

        this.buyButton.setBounds(x + width - 150, y + 40, 120, 30);
        this.useButton.setBounds(x + width - 150, y + 80, 120, 30);

        GeiShopItem.width = width; // Dynamically changes width based on presence of nasty scrollbar

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, GeiShopItem.width, height);

        g.drawImage(this.texture, x + 20, y + 25, 100, 100, null);

        g.setColor(Color.WHITE);

        g.setFont(Main.getFont("Lato-Light", 30));
        g.drawString(this.name, x + 140, y + 70);

        g.setFont(Main.getFont("Lato-Light", 15));
        g.drawString(this.description, x + 141, y + 88);


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

	public void update(Graphics g, int x, int y, int width) {

		GeiActionEvent.width = width; // Dynamically changes width based on presence of nasty scrollbar

		g.setColor(new Color(5, 15, 24));
		g.fillRect(x, y, GeiActionEvent.width, height);

		g.setFont(Main.getFont("Lato-Light", 15));

		FontMetrics metrics = g.getFontMetrics(Main.getFont("Lato-Light", 15));
		ArrayList<String> lines = new ArrayList<>();
		ArrayList<String> words = new ArrayList<>(Arrays.asList(this.caption.split(" ")));
		String currentLine = "";
		int lineWidth = (int) (width * 0.9);

		while (true) {

			if (words.size() > 0 && metrics.stringWidth(words.get(0)) > lineWidth) {
				words.set(0, words.get(0).substring(0, 6) + "...");
			}

			if (words.size() > 0 && metrics.stringWidth(currentLine + " " + words.get(0)) <= lineWidth) {
				currentLine += " " + words.get(0);
				words.remove(0);
			} else {
				lines.add(currentLine.trim());
				currentLine = "";

				if (words.size() == 0) {
					break;
				}
			}
		}

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
	public GeiButton(String text) {
		super(text);

		this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		this.setForeground(new Color(92, 91, 87));
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

class GeiStatsPanel extends GeiPanel {

	private ArrayList<GeiActionEvent> eventArrayList = new ArrayList<>();
	private final int width;
	private GeiScrollPane parent;
	private long currentTime;

	public GeiStatsPanel(int width, JSONArray actionArray) {
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
			e.printStackTrace();
			this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Uh Oh... Something went wrong", Rah.getTimestamp(this.currentTime)));
		}

		this.setPreferredSize(new Dimension(this.width, 60 + this.eventArrayList.size() * (GeiActionEvent.height + 20)));


	}

	public void setParent(GeiScrollPane parent) {
		this.parent = parent;
	}

	@Override
	public void paintComponent(Graphics g) {

		g.setColor(new Color(1, 10, 19));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.WHITE);
		g.setFont(Main.getFont("Lato-Light", 15));
		g.drawString("Recent Activity", 20, 30);

		boolean scrollEnabled = 60 + this.eventArrayList.size() * (GeiActionEvent.height + 20) > this.parent.getHeight();

		for (int y = 0; y < this.eventArrayList.size(); y++) {
			this.eventArrayList.get(y).update(g, 20, 40 + y * (GeiActionEvent.height + 20), scrollEnabled ? 205 : 210);
		}
	}

}

class GeiChatPanel extends GeiPanel {

	private ArrayList<GeiActionEvent> chatArrayList = new ArrayList<>();
	private final int width;
	private GeiScrollPane parent;
	private GeiTextField textField;
	private long currentTime;
	private JSONArray chatArray;

	public GeiChatPanel(int width, JSONArray chatArray) {
		this.width = width;
		this.textField = new GeiTextField();
		this.chatArray = chatArray;

		this.add(this.textField);
	}

	public void update(JSONArray chatArray) {

		this.currentTime = System.currentTimeMillis();
		this.chatArrayList = new ArrayList<>();

		System.out.println(this.parent.getHeight() + "lol");
        this.textField.setBounds(0, this.parent.getHeight() - 30, this.width, 30);

		try {
			JSONObject actionObject;
			for (int i = chatArray.length() - 1; i > -1; i--) {
				actionObject = chatArray.getJSONObject(i);

				this.chatArrayList.add(new GeiActionEvent(GeiActionEvent.Type.valueOf(actionObject.getString("type")), actionObject.getString("caption"), Rah.getTimestamp(actionObject.getLong("date"))));
			}

			if (chatArray.length() == 0) {
				this.chatArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Nothing to see here :)", Rah.getTimestamp(this.currentTime)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.chatArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Uh Oh... Something went wrong", Rah.getTimestamp(this.currentTime)));
		}

		this.setPreferredSize(new Dimension(this.width, 60 + this.chatArrayList.size() * (GeiActionEvent.height + 20)));


	}

	public void setParent(GeiScrollPane parent) {
		this.parent = parent;
        this.update(this.chatArray);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(1, 10, 19));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

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

				String response = Communicator.shopRequest(eventSource[0], eventSource[1]);

				System.out.println(response);

				if (response.equals("ok")) {
					this.parent.getParent().updateData();

					if (eventSource[0].equals("buy")) {
						JOptionPane.showMessageDialog(this.parent.getParent().parent, "Successfully purchased: " + eventSource[1], "HUBG Shop", JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(this.parent.getParent().parent, response, "HUBG Shop", JOptionPane.ERROR_MESSAGE);
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

        this.setPreferredSize(new Dimension(this.width, 20 + this.itemArrayList.size() * (GeiShopItem.height + 20)));

    }

	public void update(JSONArray purchasedSkins) {

        try {
            for (int i = 0; i < purchasedSkins.length(); i++) {
                for (GeiShopItem item : itemArrayList) {
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
	public void paintComponent(Graphics g) {
		g.setColor(new Color(1, 10, 19));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

        boolean scrollEnabled = 60 + this.itemArrayList.size() * (GeiShopItem.height + 20) > this.parent.getHeight();

        for (int i = 0; i < itemArrayList.size(); i ++) {
            itemArrayList.get(i).update(g, 20, 20 + 170 * i, scrollEnabled ? 455 : 460);
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