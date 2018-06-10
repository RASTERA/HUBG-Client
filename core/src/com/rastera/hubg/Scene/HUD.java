package com.rastera.hubg.Scene;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.rastera.hubg.Scene.Sprite.HealthBar;
import com.rastera.hubg.Scene.Sprite.WeaponBox;
import com.rastera.hubg.Sprites.Player;

import java.util.HashMap;

public class HUD implements Disposable{
    private ShapeRenderer sr;
    private WeaponBox a;
    private WeaponBox b;
    private HealthBar healthUI;
    private HashMap<Integer, Texture> weaponGraphics;
    private BitmapFont font;

    public HUD (SpriteBatch sb, Viewport staticView, Player player, BitmapFont font) {
        this.font = font;
        sr = new ShapeRenderer();
        sr.setProjectionMatrix(staticView.getCamera().combined);

        weaponGraphics = new HashMap<Integer, Texture>();

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        a = new WeaponBox(weaponGraphics, 0, sr);
        b = new WeaponBox(weaponGraphics, 1, sr);
        healthUI = new HealthBar(sr, player);
    }

    public void draw(Batch sb, OrthographicCamera staticCam) {
        sb.setProjectionMatrix(staticCam.combined);
        sr.setProjectionMatrix(staticCam.combined);
        a.draw(sb);
        b.draw(sb);
        healthUI.draw(sb);
    }

    public void update(Viewport staticView) {
        a.updateLocation(staticView.getScreenHeight());
        b.updateLocation(staticView.getScreenHeight());
        healthUI.updateLocation(staticView.getScreenHeight());
    }

    @Override
    public void dispose() {

    }
}
