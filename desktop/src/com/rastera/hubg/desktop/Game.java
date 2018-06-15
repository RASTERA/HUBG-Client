// PROJECT HUBG
// Henry Tu, Ryan Zhang, Syed Safwaan
// rastera.xyz
// 2018 ICS4U FINAL
//
// Game.java - Launches libgdx game

package com.rastera.hubg.desktop;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.rastera.hubg.HUBGMain;

public class Game extends HUBGPanel {

	private BufferedImage background;
	private boolean minimized = false;

	public Game(com.rastera.hubg.desktop.Main parent) {
		this.parent = parent;
		this.parent.setMasterTimer(50);
		this.constantUpdate = false;

		// Load resources
		try {
			this.background = ImageIO.read(new File("images/menu-background.png"));
		} catch (Exception e) {
			com.rastera.hubg.desktop.Main.errorQuit(e);
		}

		this.repaint();
		Main.stopMusic();

		// Start libgdx game
		new LwjglApplication(new HUBGMain(this), this.parent.config);
	}

	// Shortcut to minimize window
	public void minimize() {
		this.parent.setState(Frame.ICONIFIED);
	}

	// Shortcut to restore launcher
	public void exitGame() {
		this.parent.setState(Frame.NORMAL);
		this.parent.startPage(com.rastera.hubg.desktop.Main.Pages.MENU);
	}

	// Displays message if server refused connection
	public void rejectConnection(String message) {
		JOptionPane.showMessageDialog(Util.checkParent(this.parent), message, "HUBG Error", JOptionPane.ERROR_MESSAGE);
	}

	@Override
	public void paintComponent(Graphics graphics) {

		this.parent.updateFrameRate();

		Graphics2D g = (Graphics2D) graphics;

		// Enables antialias
		g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Draws background
		int dimension = Math.max(this.getHeight(), this.getWidth());
		g.drawImage(this.background, 0, 0, dimension, dimension, this);

		// Loading message
		String loadingMessage = "Game is still in progress...";

		g.setColor(Color.WHITE);
		g.setFont(com.rastera.hubg.desktop.Main.getFont("Lato-Light", 30));
		FontMetrics metrics = g.getFontMetrics(com.rastera.hubg.desktop.Main.getFont("Lato-Light", 30));
		g.drawString(loadingMessage, this.getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);

		// Minimizes window if not already
		if (!this.minimized) {
			this.minimized = true;
			this.minimize();
		}
	}

}