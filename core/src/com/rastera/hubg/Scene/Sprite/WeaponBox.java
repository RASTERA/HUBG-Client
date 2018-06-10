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

    private int width = 170;

    private HashMap<Integer, Texture> weaponLibrary;

    private ShapeRenderer sr;

    public WeaponBox (HashMap<Integer, Texture> weapons, int ID, ShapeRenderer sr) {
        this.boxID = ID;
        weaponLibrary = weapons;
        this.sr = sr;
    }

    public void updateLocation (int screenHeight) {
        setPosition(-this.width + (this.width+2) * boxID, screenHeight / -2 + offseth);
    }

    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setColor(255, 255, 255, 0.3f);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(getX(), getY(), width - 2, 50);
        sr.end();

        sr.setColor(255, 255, 255, 0.7f);
        sr.setColor(Color.WHITE);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(getX(), getY(), width - 2, 50);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }
}
