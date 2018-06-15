package com.rastera.hubg.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
    private ShapeRenderer sr;
    public WeaponBox a;
    public WeaponBox b;
    private HUDBar healthUI;
    private HUDBar energyUI;
    private BitmapFont font;
    private ArrayList<Fixture> itemArray;
    private ItemPickUp itempickup;
    private Player player;
    public HUBGGame game;

    public HUD (SpriteBatch sb, Viewport staticView, Player player, HUBGGame game, BitmapFont font) {
        BitmapFont font1 = font;
        this.game = game;
        this.sr = new ShapeRenderer();
        this.sr.setProjectionMatrix(staticView.getCamera().combined);

        this.player = player;

        itempickup = new ItemPickUp(new ArrayList<>(), font, game, sr) ;

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        this.a = new WeaponBox(this, player, 0, this.sr);
        this.b = new WeaponBox(this, player, 1, this.sr);
        this.healthUI = new HUDBar(this.sr, player, 24, "Health");
        this.energyUI = new HUDBar(this.sr, player, 48, "Energy");
    }

    public void draw(Batch sb, OrthographicCamera staticCam) {
        sb.setProjectionMatrix(staticCam.combined);
        this.sr.setProjectionMatrix(staticCam.combined);
        this.a.draw(sb);
        this.b.draw(sb);

        this.healthUI.draw(sb, this.player.getHealth());
        this.energyUI.draw(sb, this.player.getEnergy());
        this.itempickup.draw(sb);
    }

    public void processKeyDown(int x, int y, int mb) {
        boolean uiclick = itempickup.processKeyDown(x, y, mb);
        a.updateClick(x, y, uiclick);
        b.updateClick(x, y, uiclick);

        if (!a.active && !b.active) {
            if (player.weapon.getCurrentWeapon() != 0) {
                player.weapon.setCurrentWeapon(0);
                game.conn.write(31, new int[] {game.ID, 0});
            }
        }

    }

    public int getBoxSelected() {
        if(a.active) {
            return 0;
        } else if (b.active) {
            return 1;
        }

        return 2;
    }

    public void setItemArray(ArrayList<Fixture> items) {
        ArrayList<Fixture> itemArray = items;
        this.itempickup.items = items;
    }

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
