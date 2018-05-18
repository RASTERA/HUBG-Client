import javax.swing.JTextField;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

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