// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Main.java - Main Class

package com.rastera.hubg.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import org.json.JSONObject;

import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

public class Main extends JFrame implements ActionListener, ComponentListener {

	public enum Pages {MENU, GAME, LOGIN}

	// Master panel refresh timer
	private static Timer masterTimer;
	public static int usersOnline = 0;

	// Window params
	public static int w = 1100;
	public static int h = 700;

	// Core session data
	public static Session session;
	public static JSONObject shopData;

	// Resources
	public static Clip menuMusic;
	public static AudioInputStream audioIn;
	public static HashMap<String, BufferedImage> skinHashMap = new HashMap<>();
	private static final HashMap<String, Font> fontHashMap = new HashMap<>();

	// Centralized panel
    private static HUBGPanel panel;
    private static Pages page = Pages.LOGIN;

    // Keep track of prev frame for FPS count
	private static long prevFrame = 0;

	private static boolean graphicsStarted = false;

	// Borderless window tracking
	private int prevX, prevY;

	// Customizable features
	public static final boolean borderless = !false;
	public static final boolean showFPS = false;

	// Centralized libgdx config
	public LwjglApplicationConfiguration config;

	private Main() {

		// Setup JFrame
		super("HUBG - Henning's Unknown Battle Ground");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setSize(w, h);
		this.setMinimumSize(new Dimension(1100, 700));
		this.setLayout(new BorderLayout());

		this.setUndecorated(borderless);

		// Import resources
		try {
			File is;

			String[] fonts = new String[]{"Lato-Light", "Lato-Normal", "Lato-Thin", "Lato-Bold", "Lato-Black"};
			for (String font : fonts) {
				is = new File(String.format("fonts/%s.ttf", font));
				fontHashMap.put(font, Font.createFont(Font.TRUETYPE_FONT, is));
			}
		} catch (Exception e) {
			Main.errorQuit(e);
		}

		// Start drawing loading screen
		this.startGraphics();

		this.getContentPane().addComponentListener(this);
		this.setVisible(true);

		// https://stackoverflow.com/questions/2442599/how-to-set-jframe-to-appear-centered-regardless-of-monitor-resolution?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

		// Enable anti aliasing
		System.setProperty("awt.useSystemAAFontSettings","on");
		System.setProperty("swing.aatext", "true");

		//LIBGDX config
		this.config = new LwjglApplicationConfiguration();
		//this.config.fullscreen = true;
		this.config.forceExit = false;
		this.config.width = 1100;
		this.config.height = 700;

		// Load music
		try {
			audioIn = AudioSystem.getAudioInputStream(new File("sounds/menu.wav"));
			startMusic();

		} catch (Exception e) {
			errorQuit(e);
		}

		// Configure borderless window
		if (borderless) {
			// Courtesy of https://java-demos.blogspot.com/2013/11/how-to-move-undecorated-jframe.html

			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					// Get x,y and store them
					Main.this.prevX = e.getX();
					Main.this.prevY = e.getY();
				}
			});

			this.addMouseMotionListener(new MouseAdapter() {
				public void mouseDragged(MouseEvent e) {
					// Set the location
					// get the current location x-co-ordinate and then get
					// the current drag x co-ordinate, add them and subtract most recent
					// mouse pressed x co-ordinate
					// do same for y co-ordinate
					Main.this.setLocation(Main.this.getLocation().x + e.getX() - Main.this.prevX, Main.this.getLocation().y + e.getY() - Main.this.prevY);
				}
			});
		}
	}

	// Shortcut to minimize launcher
	public void minimize() {
		this.setState(Frame.ICONIFIED);
	}

	// Shortcut to terminate game
	public void close() {
		System.exit(0);
	}

	// Music config
	public static void startMusic() {
		try {
			menuMusic = AudioSystem.getClip();
			menuMusic.open(Main.audioIn);

			menuMusic.start();
			menuMusic.loop(menuMusic.LOOP_CONTINUOUSLY);
		} catch (Exception e) {
			Main.errorQuit(e);
		}
	}

	public static void stopMusic() {
		menuMusic.stop();
		menuMusic.setFramePosition(0);
	}

	public static boolean musicPlaying() {
		return !(menuMusic != null && menuMusic.getFramePosition() == 0);
	}

	// Get font given name
	public static Font getFont(String name, float size) {
		return fontHashMap.get(name).deriveFont(size);
	}

	// Set timer interval
	public void setMasterTimer(int interval) {
		if (masterTimer != null) {
			masterTimer.stop();
			masterTimer = null;
		}

		masterTimer = new Timer(interval, this);
		masterTimer.start();
	}

	// Swap JPanel
	public void startPage(Pages page) {
		System.out.println("Starting page " + page.toString());
		this.getContentPane().remove(panel);
		Main.page = page;
		panel = null;
		this.startGraphics();
	}

	// Keep track of FPS
	public void updateFrameRate() {
		if (showFPS) {
			long currentTime = System.currentTimeMillis();
			this.setTitle(String.format("HUBG - Henning's Unknown Battle Ground - [%d FPS]", (currentTime - prevFrame) > 0 ? 1000 / (currentTime - prevFrame) : 0));
			prevFrame = currentTime;
		}
	}

	// Core graphics update
	private void startGraphics() {
		if (panel == null || !panel.getClass().getName().toUpperCase().contains(page.toString())) {

			// Stops timer to prevent conflict
			if (masterTimer != null) {
				masterTimer.stop();
				masterTimer = null;
			}

			System.out.println("Switch Pages " + page.toString());

			// Creates new JPanel object
			switch (page) {
				case LOGIN:
					panel = new Login(this);
					break;

				case MENU:
					panel = new Menu(this);
					break;

				case GAME:
					panel = new Game(this);
					break;

			}

			graphicsStarted = true;

			this.add(panel);
			panel.requestFocus();
			this.setVisible(true);
			panel.repaint();

		}
	}

	@Override
	public void componentHidden(ComponentEvent ce) {

	}

	@Override
	public void componentShown(ComponentEvent ce) {

	}

	@Override
	public void componentMoved(ComponentEvent ce) {

	}

	// Window resizing on bordered mode
	@Override
	public void componentResized(ComponentEvent ce) {
		w = this.getWidth();
		h = this.getHeight();

		if (panel != null) {
			this.repaint();
			panel.repaint();
		}

	}

	@Override
	public void actionPerformed(ActionEvent evt) {

		// Timer actions
		if (graphicsStarted) {
			this.startGraphics();

			if (panel.constantUpdate) {
				this.repaint();
			}

			if (panel instanceof Menu) {
				((Menu) panel).updateData();
			}

		}
	}

	// Displays dialog with traceback if error occurs
	public static void errorQuit(String e) {
		if (masterTimer != null) {
			masterTimer.stop();
			masterTimer = null;
		}

		System.out.println("Something went wrong");
		JOptionPane.showMessageDialog(Util.checkParent(panel), "HUBG experienced an unexpected error:\n\n" + e, "HUBG Error", JOptionPane.ERROR_MESSAGE);

		System.exit(0);
	}

	public static void errorQuit(Exception e) {
		e.printStackTrace();

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);

		errorQuit(sw.toString());
	}

	public static void main(String[] args) {
		Main frame = new Main();
	}

}