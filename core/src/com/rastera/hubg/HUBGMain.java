package com.rastera.hubg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Base64Coder;
import com.rastera.hubg.Screens.HUBGGame;

import com.rastera.hubg.desktop.Communicator;
import com.rastera.hubg.desktop.Main;
import org.json.JSONObject;

public class HUBGMain extends Game implements ApplicationListener{
	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public static final float TILESIZE = 48;
	public static final float METERPERTILE = 2;
	public static final float PPM = 48/METERPERTILE;
	public static final float SYNC_INTERVAL = 9/60f;

	public static JSONObject skinDataJSON;

	private HUBGGame game;
	//public static HashMap<String, Texture> skinDataHashMap;

	public SpriteBatch batch;
	public com.rastera.hubg.desktop.Game parentGame;

	public HUBGMain(com.rastera.hubg.desktop.Game parentGame) {
		super();
		this.parentGame = parentGame;

		skinDataJSON = Communicator.request(Communicator.RequestType.GET, null, Communicator.getURL(Communicator.RequestDestination.API) + "shop/");
	}

	public static Texture getSkin(String name) {

		try {
			byte[] imageDecoded = Base64Coder.decode(skinDataJSON.getJSONObject(name).getString("image").trim());
			return new Texture(new Pixmap(imageDecoded, 0, imageDecoded.length));
		} catch (Exception e) {
			Main.errorQuit(e);
		}

		return null;
	}

	@Override
	public void create () {
		this.batch = new SpriteBatch();
		this.game = new HUBGGame(this, this.parentGame);
		this.setScreen(this.game);
	}

	@Override
	public void render () {
		super.render();
	}

    public void dispose() {
		this.game.conn.write(30, this.game.getPlayer().playerWeapons);

		if (this.game != null && this.game.conn != null) {
			this.game.conn.write(1000, null);
		}

		this.parentGame.exitGame();
    }

}
