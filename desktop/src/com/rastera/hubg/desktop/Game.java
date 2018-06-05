package com.rastera.hubg.desktop;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.rastera.hubg.HUBGMain;

public class Game extends GeiPanel {

	private BufferedImage background;

	public Game(com.rastera.hubg.desktop.Main parent) {
		this.parent = parent;
		this.parent.setMasterTimer(50);
		this.constantUpdate = false;

		try {
			this.background = ImageIO.read(new File("images/menu-background.png"));
		} catch (Exception e) {
			com.rastera.hubg.desktop.Main.errorQuit(e);
		}

		this.repaint();

		com.rastera.hubg.desktop.Main.stopMusic();

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.fullscreen = true;
		config.forceExit = false;

		new LwjglApplication(new HUBGMain(this), config);


	}

	public void exitGame() {
		this.parent.setState(Frame.NORMAL);
		this.parent.startPage(com.rastera.hubg.desktop.Main.Pages.MENU);
	}

	@Override
	public void paintComponent(Graphics graphics) {

		this.parent.updateFrameRate();

		Graphics2D g = (Graphics2D) graphics;

		g.setRenderingHint( RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		int dimension = Math.max(this.getHeight(), this.getWidth());
		g.drawImage(this.background, 0, 0, dimension, dimension, this);

		String loadingMessage = "Game is still in progress...";

		g.setColor(Color.WHITE);
		g.setFont(com.rastera.hubg.desktop.Main.getFont("Lato-Light", 30));
		FontMetrics metrics = g.getFontMetrics(com.rastera.hubg.desktop.Main.getFont("Lato-Light", 30));
		g.drawString(loadingMessage, this.getWidth() / 2 - metrics.stringWidth(loadingMessage) / 2, this.getHeight() / 2 - metrics.getHeight() / 2);

	}

}