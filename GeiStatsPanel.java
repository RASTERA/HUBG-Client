import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.*;

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
