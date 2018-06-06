package com.rastera.hubg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Base64Coder;
import com.rastera.hubg.Screens.HUBGGame;

import com.rastera.hubg.desktop.Communicator;
import com.rastera.hubg.desktop.Main;
import com.rastera.hubg.desktop.Rah;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

public class HUBGMain extends Game implements ApplicationListener{
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float TILESIZE = 48;
	public static final float METERPERTILE = 2;
	public static final float PPM = 48/METERPERTILE;
	public static final float SYNC_INTERVAL = 9/60f;

	public static JSONObject skinDataJSON;
	public static HashMap<String, Texture> skinDataHashMap;

	public SpriteBatch batch;
	public com.rastera.hubg.desktop.Game parentGame;

	public HUBGMain(com.rastera.hubg.desktop.Game parentGame) {
		super();
		this.parentGame = parentGame;

		/*
		try {
			skinDataJSON = Communicator.request(Communicator.RequestType.GET, null, Communicator.getURL(Communicator.RequestDestination.API) + "shop/");

			Iterator keys = skinDataJSON.keys();
			String key;
			String imageName;
            BufferedImage outputImage;
			//byte[] imageDecoded;

			while (keys.hasNext()) {
				key = keys.next().toString();

				imageName = "skin_buffer/" + key.replaceAll(" ", "_") + ".png";

				outputImage = Rah.decodeToImage(skinDataJSON.getJSONObject(key).getString("image"));
				ImageIO.write(outputImage, "png", new File(imageName));

                skinDataHashMap.put(key, new Texture(Gdx.files.internal(imageName)));

				// Doesn't work, ghetto solution for now
				//imageDecoded = Base64Coder.decode(skinDataJSON.getJSONObject(key).getString("image").trim());
    			//skinDataHashMap.put(key, new Texture(new Pixmap(imageDecoded, 0, imageDecoded.length)));
			}

		} catch (Exception e) {
			Main.errorQuit(e);
		}

		System.out.println(skinDataHashMap); */

	}
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new HUBGGame(this, this.parentGame));
	}

	@Override
	public void render () {
		super.render();
	}

    public void dispose() {
        System.out.println("lol");
        this.parentGame.exitGame();
    }

}
