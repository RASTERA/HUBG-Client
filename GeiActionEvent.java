import javax.swing.*;
import java.awt.*;
import java.awt.Dimension;
import java.util.*;

public class GeiActionEvent {

	public static enum Type {KILL, KILLED, WIN, INFO}

	;
	public static HashMap<Type, Color> typeColorHashMap = new HashMap<>();

	static {
		typeColorHashMap.put(Type.KILL, new Color(0, 63, 255));
		typeColorHashMap.put(Type.KILLED, new Color(135, 6, 0));
		typeColorHashMap.put(Type.WIN, new Color(30, 165, 0));
		typeColorHashMap.put(Type.INFO, new Color(200, 200, 0));
	}

	public static int height = 80;
	public static int width = 210;

	private String caption;
	private String time;
	private Type type;

	public GeiActionEvent(Type type, String enemyName, String time) {
		this.caption = enemyName;
		this.time = time;
		this.type = type;
	}

	public void update(Graphics g, int x, int y, int width) {

		this.width = width; // Dynamically changes width based on presence of nasty scrollbar

		g.setColor(new Color(5, 15, 24));
		g.fillRect(x, y, this.width, this.height);

		g.setFont(Main.getFont("Lato-Light", 15));

		g.setColor(Color.WHITE);
		g.drawString(this.caption, x + 10, y + 20);

		g.setColor(new Color(100, 100, 100));
		g.drawString(this.time, x + 10, y + height - 8);

		g.setColor(typeColorHashMap.get(this.type));
		g.fillRect(this.width - 3 + x, y, 3, this.height);

	}
}