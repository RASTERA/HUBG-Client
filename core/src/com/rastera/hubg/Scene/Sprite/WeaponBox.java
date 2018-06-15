package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.rastera.hubg.Scene.HUD;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Item;
import com.rastera.hubg.Sprites.Player;
import com.rastera.hubg.Util.ItemList;
import com.rastera.hubg.Util.WeaponList;
import com.rastera.hubg.desktop.Util;

public class WeaponBox extends Sprite {
    private int weaponID = 0;
    private int boxID;
    public int offseth = 79;
    private int width = 170;

    private ShapeRenderer sr;
    private Player player;
    private HUD parent;
    public boolean active = false; // If the box is selected or not

    private int screenHeight;
    private int screenWidth;

    public WeaponBox (HUD parent, Player player, int ID, ShapeRenderer sr) {
        this.boxID = ID;
        this.parent = parent;
        this.sr = sr;
        this.player = player;
    }

    // Updates location
    public void updateLocation (int screenWidth, int screenHeight) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.setPosition(-this.width + (this.width+2) * boxID, screenHeight / -2 + offseth);
    }

    // Handles the clicks, uiclick check if the click happened on other ui's so the weapon wont deselect.
    public void updateClick(int mx, int my, boolean uiclick) {
        mx -= screenWidth / 2;
        my = screenHeight / 2 - my;

        if (getX() < mx && mx < getX() + width-2 && getY() < mx && my < getY() + 50) {
            active = true;
            if (player.weapon.getCurrentWeapon() != player.playerWeapons[boxID]) {
                player.weapon.setCurrentWeapon(player.playerWeapons[boxID]);
                parent.game.conn.write(31, new int[] {parent.game.ID, player.playerWeapons[boxID]}); // Sending message to the server
            }
        } else if (!uiclick){ // If somewhere else on the screen was clicked, then deselect the weapon
            active = false;
        }
    }

    // Drawing the UI element
    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        if (active) { // If the box is selected, than change some colors
            sr.setColor(0.1f, 0.1f, 0.1f, 0.5f);
        } else {
            sr.setColor(0.1f, 0.1f, 0.1f, 0.3f);
        }
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(getX(), getY(), width - 2, 50);
        sr.end();


        if (active) {
            sr.setColor(Color.WHITE);
        } else {
            sr.setColor(0.5f, 0.5f, 0.5f, 0.7f);
        }
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(getX(), getY(), width - 2, 50);
        sr.end();
        if (player.playerWeapons != null) {
            if (player.playerWeapons[boxID] != 0) {
                Texture gun = ItemList.itemGraphics.get(player.playerWeapons[boxID]);
                float scale = Math.max(gun.getWidth() / (width - 20), gun.getHeight() / (50 - 10));
                sb.begin();
                sb.draw(gun, getX() + ((width - 2) - gun.getWidth() / scale) / 2, getY() + ((50 - 2) - gun.getHeight() / scale) / 2, gun.getWidth() / scale, gun.getHeight() / scale);
                sb.end();
            }
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Drawing the text for ammo
        if (player.playerWeapons != null && player.playerWeapons[boxID] < 0) {
            sb.begin();
            Util.centerText(sb, HUBGGame.latoFont, 0.2f, "" + player.gunAmmo[boxID], (int) getX() + 10, (int) getY() + 10);
            sb.end();
        }
    }

}
