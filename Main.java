// Main.java

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
import java.io.InputStream;

public class Main extends JFrame implements ActionListener, ComponentListener {

	public enum Pages {MENU, GAME, LOGIN}

	private static Timer masterTimer;
	public static int usersOnline = 0;
	public static int w = 1100;
	public static int h = 500;
	private static GeiPanel panel;
	private static Pages page = Pages.LOGIN;
	public static Session session;
	public static JSONObject shopData;
	public static Clip menuMusic;
	public static AudioInputStream audioIn;
	public static HashMap<String, BufferedImage> skinHashMap = new HashMap<>();
	private static final HashMap<String, Font> fontHashMap = new HashMap<>();

	private static long prevFrame = 0;
	private static boolean graphicsStarted = false;

	private Main() {
		super("HUBG - Henning's Unknown Battle Ground");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		this.setSize(w, h);
		this.setMinimumSize(new Dimension(1100, 500));
		this.setLayout(new BorderLayout());

		this.startGraphics();

		this.getContentPane().addComponentListener(this);
		this.setVisible(true);


		//System.setProperty("awt.useSystemAAFontSettings","on");
		//System.setProperty("swing.aatext", "true");

		try {
			audioIn = AudioSystem.getAudioInputStream(this.getClass().getResource("music/menu.wav"));

			//startMusic();
			
			InputStream is;

			String[] fonts = new String[]{"Lato-Light", "Lato-Normal", "Lato-Thin", "Lato-Bold", "Lato-Black"};
			for (String font : fonts) {
				is = Main.class.getResourceAsStream(String.format("fonts/%s.ttf", font));
				fontHashMap.put(font, Font.createFont(Font.TRUETYPE_FONT, is));
			}
		} catch (Exception e) {
			errorQuit(e);
		}

		/*
		AuthToken tempAuth = Session.readSession();

		if (tempAuth != null) {
			System.out.println("Login from token");

			// Refresh token
			Session tempSession = Communicator.login(tempAuth);

			if (tempSession != null) {
				Main.session = tempSession;
				Main.page = Pages.MENU;
			} else {

				Session.destroySession();

				JOptionPane.showMessageDialog(null, "Unable to login with token", "HUBG Error", JOptionPane.ERROR_MESSAGE);
			}
		}*/
	}

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

	public static Font getFont(String name, float size) {
		return fontHashMap.get(name).deriveFont(size);
	}

	public void setMasterTimer(int interval) {
		if (masterTimer != null) {
			masterTimer.stop();
			masterTimer = null;
		}

		masterTimer = new Timer(interval, this);
		masterTimer.start();
	}

	public void startPage(Pages page) {
		System.out.println("Starting page " + page.toString());
		this.getContentPane().remove(panel);
		Main.page = page;
		panel = null;
		this.startGraphics();
	}

	public void updateFrameRate() {
		long currentTime = System.currentTimeMillis();
		this.setTitle(String.format("HUBG - Henning's Unknown Battle Ground - [%d FPS]", (currentTime - prevFrame) > 0 ? 1000 / (currentTime - prevFrame) : 0));
		prevFrame = currentTime;
	}


	private void startGraphics() {
		if (panel == null || !panel.getClass().getName().toUpperCase().equals(page.toString())) {

			if (masterTimer != null) {
				masterTimer.stop();
				masterTimer = null;
			}

			System.out.println("Switch Pages " + page.toString());

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

		if (graphicsStarted) {
			this.startGraphics();

			if (panel.constantUpdate) {
				this.repaint();
			}

			if (panel instanceof Menu) {
				((Menu) panel).updateData();
			}

		}

        /*
        long currentTime = System.currentTimeMillis();

        System.out.println(1000/(currentTime - prevFrame));

        prevFrame = currentTime;*/

	}

	public static void errorQuit(String e) {
		if (masterTimer != null) {
			masterTimer.stop();
			masterTimer = null;
		}

		System.out.println("Something went wrong");
		JOptionPane.showMessageDialog(null, "HUBG experienced an unexpected error:\n\n" + e, "HUBG Error", JOptionPane.ERROR_MESSAGE);

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