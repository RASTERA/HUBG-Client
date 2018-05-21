import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.util.*;

public class GeiStatsPanel extends JPanel {

    private ArrayList<GeiActionEvent> eventArrayList = new ArrayList<>();
    private int width;
    private JScrollPane parent;

    public GeiStatsPanel(int width) {
        this.width = width;

        // parsing bs goes here bro

        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));
        eventArrayList.add(new GeiActionEvent(GeiActionEvent.Type.KILL, "Karl Zhu", "10 hrs ago bro"));

        this.setPreferredSize(new Dimension(width, 60 + eventArrayList.size() * (GeiActionEvent.height + 20)));

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
