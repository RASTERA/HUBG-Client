package com.rastera.hubg.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.rastera.hubg.Scene.Sprite.HUDBar;
import com.rastera.hubg.Scene.Sprite.ItemPickUp;
import com.rastera.hubg.Scene.Sprite.WeaponBox;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class HUD implements Disposable{
    private ShapeRenderer sr; // The ShapeRenderer that is used to draw the different geometric shapes
    public WeaponBox a;  // The two weapon boxes
    public WeaponBox b;
    private HUDBar healthUI;  // The health and energy bar
    private HUDBar energyUI;
    private BitmapFont font;
    private ItemPickUp itempickup;
    private Player player;
    public HUBGGame game;

    public HUD (SpriteBatch sb, Viewport staticView, Player player, HUBGGame game, BitmapFont font) {
        this.game = game;
        this.sr = new ShapeRenderer();
        this.sr.setProjectionMatrix(staticView.getCamera().combined); // Setting the projection of the renderer to the static camera

        this.player = player;

        // Creating the different UI elements
        itempickup = new ItemPickUp(new ArrayList<>(), font, game, sr) ;
        this.a = new WeaponBox(this, player, 0, this.sr);
        this.b = new WeaponBox(this, player, 1, this.sr);
        this.healthUI = new HUDBar(this.sr, player, 24, "Health");
        this.energyUI = new HUDBar(this.sr, player, 48, "Energy");
    }

    /**
     * Drawing the different UI ements
     *
     * @param sb The SpriteBatcher to draw the images
     * @param staticCam The Static Camera to draw UI
     */
    public void draw(Batch sb, OrthographicCamera staticCam) {
        sb.setProjectionMatrix(staticCam.combined);  // Setting the projection matrix to draw according to the static camera
        this.sr.setProjectionMatrix(staticCam.combined);
        this.a.draw(sb);
        this.b.draw(sb);

        this.healthUI.draw(sb, this.player.getHealth());
        this.energyUI.draw(sb, this.player.getEnergy());
        this.itempickup.draw(sb);
    }

    /**
     * Processing the mouse input received from the InputAdaptor
     *
     * @param x The x coordinate of the click
     * @param y The y coordinate of the click
     * @param mb The mouse button pressed
     */
    public void processKeyDown(int x, int y, int mb) {

        // Passing the information to the individual UI elements
        boolean uiclick = itempickup.processKeyDown(x, y, mb);
        a.updateClick(x, y, uiclick);
        b.updateClick(x, y, uiclick);

        // If the player is not holding a gun, then tell the server about it.
        if (!a.active && !b.active) {
            if (player.weapon.getCurrentWeapon() != 0) { // If the player had a weapon active
                player.weapon.setCurrentWeapon(0);
                game.conn.write(31, new int[] {game.ID, 0});  // Sends data to the server
            }
        }

    }

    /**
     * A function to obtain the box selected by the player
     *
     * @return The ID of the box. Returns 2 if no weapons are active
     */
    public int getBoxSelected() {
        if(a.active) {
            return 0;
        } else if (b.active) {
            return 1;
        }
        return 2;
    }

    /**
     * Setting the item array
     * @param items The array that contain the items
     */
    public void setItemArray(ArrayList<Fixture> items) {
        this.itempickup.items = items;
    }

    /**
     * Updating the location of the UI according to the height and the width of the screen
     * @param staticView The viewport. Contains the size of the screen. Is updated in the Main game loop
     */
    public void update(Viewport staticView) {
        this.a.updateLocation(staticView.getScreenWidth(), staticView.getScreenHeight());
        this.b.updateLocation(staticView.getScreenWidth(), staticView.getScreenHeight());
        this.energyUI.updateLocation(staticView.getScreenHeight());
        this.healthUI.updateLocation(staticView.getScreenHeight());
        this.itempickup.updateLocation(staticView.getScreenHeight(), staticView.getScreenWidth());
    }

    @Override
    public void dispose() {
    }
}
