package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;

public class WeaponBox extends Sprite {
    private int weaponID = 0;
    private int boxID;
    public int offseth = 79;
    public HashMap<Integer, Texture> weaponLibrary = new HashMap<>();

    private int width = 170;
    private ShapeRenderer sr;

    public WeaponBox (HashMap<Integer, Texture> weapons, int ID, ShapeRenderer sr) {
        this.boxID = ID;
        weaponLibrary = weapons;
        this.sr = sr;
    }

    public void updateLocation (int screenHeight) {
        this.setPosition(-this.width + (this.width+2) * this.boxID, screenHeight / -2 + this.offseth);
    }

    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        this.sr.setColor(255, 255, 255, 0.3f);
        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        this.sr.rect(this.getX(), this.getY(), this.width - 2, 50);
        this.sr.end();

        this.sr.setColor(255, 255, 255, 0.7f);
        this.sr.setColor(Color.WHITE);
        this.sr.begin(ShapeRenderer.ShapeType.Line);
        this.sr.rect(this.getX(), this.getY(), this.width - 2, 50);
        this.sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
