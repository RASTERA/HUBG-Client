package com.rastera.hubg.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.rastera.hubg.Scene.Sprite.EnergyBar;
import com.rastera.hubg.Scene.Sprite.HealthBar;
import com.rastera.hubg.Scene.Sprite.ItemPickUp;
import com.rastera.hubg.Scene.Sprite.WeaponBox;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Player;

import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.nio.file.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class HUD implements Disposable{
    private ShapeRenderer sr;
    private WeaponBox a;
    private WeaponBox b;
    private HealthBar healthUI;
    private EnergyBar energyUI;
    private HashMap<Integer, Texture> weaponGraphics;
    private BitmapFont font;
    private ArrayList<Fixture> itemArray;
    private ItemPickUp itempickup;
    private HUBGGame game;

    public HUD (SpriteBatch sb, Viewport staticView, Player player, HUBGGame game, BitmapFont font) {
        this.font = font;
        this.game = game;
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(staticView.getCamera().combined);

        itempickup = new ItemPickUp(new ArrayList<Fixture>(), font, game, sr) ;
        weaponGraphics = new HashMap<Integer, Texture>();

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        a = new WeaponBox(weaponGraphics, 0, sr);
        b = new WeaponBox(weaponGraphics, 1, sr);
        healthUI = new HealthBar(sr, player);
        energyUI = new EnergyBar(sr, player);
    }

    public void draw(Batch sb, OrthographicCamera staticCam) {
        sb.setProjectionMatrix(staticCam.combined);
        sr.setProjectionMatrix(staticCam.combined);
        a.draw(sb);
        b.draw(sb);
        healthUI.draw(sb);
        energyUI.draw(sb);
        itempickup.draw(sb);
    }

    public void processKeyDown(int x, int y, int mb) {
        itempickup.processKeyDown(x, y, mb);
    }

    public void setItemArray(ArrayList<Fixture> items) {
        this.itemArray = items;
        itempickup.items = items;
    }

    public void update(Viewport staticView) {
        a.updateLocation(staticView.getScreenHeight());
        b.updateLocation(staticView.getScreenHeight());
        energyUI.updateLocation(staticView.getScreenHeight());
        healthUI.updateLocation(staticView.getScreenHeight());
        itempickup.updateLocation(staticView.getScreenHeight(), staticView.getScreenWidth());
    }

    @Override
    public void dispose() {

    }
}
