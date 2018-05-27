import javax.swing.JButton;
import java.awt.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

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