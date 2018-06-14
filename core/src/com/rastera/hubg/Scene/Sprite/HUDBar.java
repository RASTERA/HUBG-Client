package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Player;
import com.rastera.hubg.desktop.Rah;

public class HUDBar extends Sprite {
    private int width = 344;
    public int hoffset;

    private String caption;
    private ShapeRenderer sr;
    private Player player;

    public HUDBar (ShapeRenderer sr, Player player, int offset, String caption) {
        this.sr = sr;
        this.player = player;
        this.hoffset = offset;
        this.caption = caption;
    }

    public void updateLocation (int screenHeight) {
        this.setPosition(-this.width / 2, screenHeight / -2 + this.hoffset);
    }

    public void draw (Batch sb, float level) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        BitmapFont font = Rah.cloneFont(HUBGGame.latoFont);

        if (level < 50) {
            this.sr.setColor(200, 0, 0, 0.9f);
            font.setColor(200, 200, 200, 0.9f);
        } else {
            this.sr.setColor(200, 200, 200, 0.9f);
            font.setColor(200, 0, 0, 0.9f);
        }

        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        this.sr.rect(this.getX(), (int) this.getY(), this.width * level / 100, 16);
        this.sr.end();

        this.sr.setColor(200, 200, 200, 0.9f);
        this.sr.setColor(Color.WHITE);
        this.sr.begin(ShapeRenderer.ShapeType.Line);
        this.sr.rect(this.getX(), (int) this.getY(), this.width, 15);
        this.sr.end();

        sb.begin();
        Rah.centerText(sb, font, 0.2f, this.caption + " [ " + (int) level+ "% ]", (int) this.getX() + this.width / 2, (int) this.getY() + 8);
        sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }


}
