import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.util.*;

public class GeiStatsPanel extends JPanel {

    private ArrayList<GeiActionEvent> eventArrayList = new ArrayList<>();
    private int width;
    private JScrollPane parent;
    private long currentTime;

    public GeiStatsPanel(int width, JSONArray actionArray) {
        this.width = width;

        this.currentTime = System.currentTimeMillis();

        this.update(actionArray);

        this.setPreferredSize(new Dimension(width, 60 + eventArrayList.size() * (GeiActionEvent.height + 20)));

    }

    public void update(JSONArray actionArray) {
        try {
            JSONObject actionObject;
            for (int i = actionArray.length() - 1; i > -1; i--) {
                actionObject = actionArray.getJSONObject(i);

                eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, actionObject.getString("caption"), getTimestamp(actionObject.getLong("date"))));

                System.out.println(actionArray.get(i));
            }

            if (actionArray.length() == 0) {
                eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Nothing to see here :)", getTimestamp(this.currentTime)));
            }
        } catch (Exception e) {
            eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Uh Oh... Something went wrong", getTimestamp(this.currentTime)));
        }

    }

    public String getTimestamp(long time) {
        long difference = (this.currentTime - time) / 1000L;

        System.out.println(difference);

        if (difference < 5) {
            return "Just now";
        }
        else if (difference < 60) {
            return difference + " seconds ago";
        } else if (difference < 3600) {
            return difference / 60L + " minutes ago";
        } else if (difference < 86400) {
            return difference / 3600L + " hours ago";
        } else if (difference < 604800) {
            return difference / 86400L + " days ago";
        } else if (difference < 2592000) {
            return difference / 604800L + " weeks ago";
        } else if (difference < 31536000) {
            return difference / 2592000L + " months ago";
        } else {
            return difference / 31536000L + " years ago";
        }
    }

    public void setParent(JScrollPane parent) {
        this.parent = parent;
    }

    public void paintComponent(Graphics g) {

        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawString("Recent Encounters", 20, 20);

        boolean scrollEnabled = 60 + eventArrayList.size() * (GeiActionEvent.height + 20) > this.parent.getHeight();

        for (int y = 0; y < eventArrayList.size(); y ++) {
            eventArrayList.get(y).update(g, 20, 40 + y * (GeiActionEvent.height + 20), scrollEnabled ? 205 : 210);
        }
    }

}
