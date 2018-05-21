import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.util.*;

public class GeiActionEvent {

    public static enum Type {KILL, KILLED, WIN};

    public static int height = 80;
    public static int width = 210;

    private String caption;
    private String time;

    public GeiActionEvent(Type type, String enemyName, String time) {
        this.caption = enemyName;
    }

    public void update(Graphics g, int x, int y, int width) {

        this.width = width; // Dynamically changes width based on presence of nasty scrollbar

        g.setColor(new Color(5, 15, 24));
        g.fillRect(x, y, this.width, this.height);

        g.setColor(Color.WHITE);
        g.drawString(this.caption + " is super gei", x + 5, y + 20);

    }
}