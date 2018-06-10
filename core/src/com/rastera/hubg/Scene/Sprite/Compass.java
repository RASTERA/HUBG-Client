package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.rastera.hubg.Sprites.Player;
import com.rastera.hubg.Util.Rah;

import java.util.HashMap;

public class Compass extends Sprite {
    private int width = 344;
    public int hoffset = 24;

    private ShapeRenderer sr;
    private Player player;
    private BitmapFont font;

    public Compass (ShapeRenderer sr, Player player, BitmapFont font) {
        this.sr = sr;
        this.player = player;
        this.font = font;
    }

    public void updateLocation (int screenHeight) {
        setPosition(-this.width / 2, screenHeight / 2 - hoffset);
    }

    public void draw (Batch sb) {
        int rotation = MathUtils.round(MathUtils.radiansToDegrees * player.getRotation() + 180);
    }
}
