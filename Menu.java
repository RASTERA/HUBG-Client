// Some game magic bs

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Menu extends GeiPanel implements KeyListener, ActionListener {

    private GeiButton startButton;
    private JScrollPane recentActions;
    private GeiStatsPanel recentActionsPanel;
    private int statsPanelWidth = 250;
    private BufferedImage background;

    public Menu(Main parent) {

        this.parent = parent;
        this.parent.setMasterTimer(50);
        this.constantUpdate = false;

        try {
            this.background = ImageIO.read(new File("images/menu-background.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        startButton = new GeiButton("Start");
        startButton.setActionCommand("start");
        startButton.addActionListener(this);

        recentActionsPanel = new GeiStatsPanel(statsPanelWidth);
        recentActions = new JScrollPane(recentActionsPanel);
        recentActions.setBorder(null);
        recentActions.getVerticalScrollBar().setUnitIncrement(16);
        recentActions.getVerticalScrollBar().setPreferredSize(new Dimension(5, Integer.MAX_VALUE));

        recentActionsPanel.setParent(recentActions);

        add(recentActions);
        add(startButton);

        addKeyListener(this);
        setFocusable(true);

    }

    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "start":

                System.out.println("SWITCH");

                removeKeyListener(this);
                this.parent.startPage(Main.Pages.GAME);
        }
    }


    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {

    }

    public void keyReleased(KeyEvent e) {

    }

    public void paintComponent(Graphics g) {

        this.parent.updateFrameRate();

        startButton.setBounds(20, 10, 150, 40);

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, Main.w, Main.h);

        int size = Math.max(getWidth() - statsPanelWidth, getHeight() - 60);

        g.drawImage(this.background, 0, 60, size, size, this);

        g.setColor(new Color(5, 15, 24));
        g.fillRect(0, 0, Main.w, 60);

        //g.setColor(new Color(1, 10, 19));
        //g.fillRect(Main.w - 250, 0, 250, Main.h);

        recentActions.setBounds(Main.w - this.statsPanelWidth, 60, this.statsPanelWidth, Main.h - 60);
        recentActions.revalidate();
        recentActions.repaint();

        g.setColor(Color.WHITE);
        g.drawString(Main.session.getEmail(), Main.w - this.statsPanelWidth + 20, 20);
        g.drawString("69 Kills | 12 Deaths | 23 Matches", Main.w - this.statsPanelWidth + 20, 50);
    }


}