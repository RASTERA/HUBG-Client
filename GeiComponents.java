import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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

class GeiStatsPanel extends JPanel {

	private ArrayList<GeiActionEvent> eventArrayList = new ArrayList<>();
	private final int width;
	private JScrollPane parent;
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

				this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.valueOf(actionObject.getString("type")), actionObject.getString("caption"), this.getTimestamp(actionObject.getLong("date"))));
			}

			if (actionArray.length() == 0) {
				this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Nothing to see here :)", this.getTimestamp(this.currentTime)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			this.eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.INFO, "Uh Oh... Something went wrong", this.getTimestamp(this.currentTime)));
		}

		this.setPreferredSize(new Dimension(this.width, 60 + this.eventArrayList.size() * (GeiActionEvent.height + 20)));


	}

	private String getTimestamp(long time) {
		long difference = (this.currentTime - time) / 1000L;

		if (difference < 0) {
			return "From the future???";
		} else if (difference < 5) {
			return "Just now";
		} else if (difference < 60) {
			return difference + " seconds ago";
		} else if (difference < 3600) {
			return difference / 60L + String.format(" minute%s ago", difference / 60L > 1 ? "s" : "");
		} else if (difference < 86400) {
			return difference / 3600L + String.format(" hour%s ago", difference / 3600L > 1 ? "s" : "");
		} else if (difference < 604800) {
			return difference / 86400L + String.format(" day%s ago", difference / 86400L > 1 ? "s" : "");
		} else if (difference < 2592000) {
			return difference / 604800L + String.format(" week%s ago", difference / 604800L > 1 ? "s" : "");
		} else if (difference < 31536000) {
			return difference / 2592000L + String.format(" month%s ago", difference / 2592000L > 1 ? "s" : "");
		} else {
			return difference / 31536000L + String.format(" year%s ago", difference / 31536000L > 1 ? "s" : "");
		}
	}

	public void setParent(JScrollPane parent) {
		this.parent = parent;
	}

	@Override
	public void paintComponent(Graphics g) {
		g.setColor(new Color(1, 10, 19));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		g.setColor(Color.WHITE);
		g.setFont(Main.getFont("Lato-Light", 15));
		g.drawString("Recent Activity", 20, 20);

		boolean scrollEnabled = 60 + this.eventArrayList.size() * (GeiActionEvent.height + 20) > this.parent.getHeight();

		for (int y = 0; y < this.eventArrayList.size(); y++) {
			this.eventArrayList.get(y).update(g, 20, 40 + y * (GeiActionEvent.height + 20), scrollEnabled ? 205 : 210);
		}
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