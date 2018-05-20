import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;

private class GeiActionEvent {

    public enum Type {KILL, KILLED, WIN};

    private String caption;
    private String time;

    public GeiActionEvent(Type type, String enemyName, String time) {

    }
}

public class GeiStatsPanel extends JPanel {

    public GeiStatsPanel(int w) {
        this.setPreferredSize(new Dimension(w, 1000));

    }

    public void paintComponent(Graphics g) {

        g.setColor(new Color(1, 10, 19));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);
        g.drawString("Recent Encounters", 20, 20);

        for (int y = 50; y < 1000; y += 300) {
            g.setColor(Color.RED);
            g.drawRect(20, y, getWidth() - 40, 250);
        }
    }

}
