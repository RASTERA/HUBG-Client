package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.rastera.hubg.Sprites.Player;

import java.util.HashMap;

public class HealthBar extends Sprite {
    private int width = 344;
    public int hoffset = 24;

    private ShapeRenderer sr;
    private Player player;

    public HealthBar (ShapeRenderer sr, Player player) {
        this.sr = sr;
        this.player = player;
    }

    public void updateLocation (int screenHeight) {
        setPosition(-this.width / 2, screenHeight / -2 + hoffset);
    }

    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setColor(200, 200, 200, 0.9f);
        sr.setColor(Color.WHITE);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.rect(getX(), getY()-1, width, 15);
        sr.end();

        if (player.getHealth() < 50) {
            sr.setColor(200, 0, 0, 0.9f);
        }

        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(getX(), getY(), width * player.getHealth() / 100, 14);
        sr.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


}
