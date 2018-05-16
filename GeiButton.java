import javax.swing.JButton;
import java.awt.*;

class GeiButton extends JButton {
    public GeiButton(String text) {
        super(text);
        this.setEnabled(false);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.setBackground(new Color(59, 89, 182));
        this.setForeground(Color.WHITE);
        this.setFocusPainted(false);

    }
}