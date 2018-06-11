package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Item;
import com.rastera.hubg.Util.ItemLoader;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemPickUp extends Sprite {
    private int weaponID = 0;

    private int width = 150;
    private int itemHeight = 50;

    public ArrayList<Fixture> items;

    private ShapeRenderer sr;
    private BitmapFont font;

    private int screenHeight;
    private int screenWidth;
    private HUBGGame game;

    public ItemPickUp (ArrayList<Fixture> items, BitmapFont font, HUBGGame game, ShapeRenderer sr) {
        this.sr = sr;
        this.items = items;
        this.font = font;
        this.game = game;
    }

    public void updateLocation (int screenHeight, int screenWidth) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        setPosition(screenWidth / -2 + 5, 0);
    }

    public void processKeyDown (int x, int y, int mb) {
        System.out.println(x + " " + y);
        x -= screenWidth / 2;
        y = screenHeight / 2 - y;

        for (int i = 0; i < items.size(); i++) {
            System.out.println("clicking " + x + " " + y + " " +(getX() + 2) + " " + (getX() + width - 2 + " " + (getY() + 2 - (itemHeight + 4)*(i+1)) + " " + (getY() + 2 - (itemHeight + 4)*(i+1) + itemHeight)));

            if (x > getX() + 2 && x < getX() + width - 2  && y > getY() + 2 - (itemHeight + 4)*(i+1) && y < getY() + 2 - (itemHeight + 4)*(i+1) + itemHeight){
                game.pickupItem(items.get(i));
            }
        }

    }

    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        sr.setColor(0.2f, 0.2f, 0.2f, 0.4f);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(getX(), -(itemHeight + 4) * items.size(), width, (itemHeight + 4) * items.size());

        sr.setColor(0.1f, 0.1f, 0.1f, 1f);
        for (int i = 0; i < items.size(); i++) {
            sr.rect(getX() + 2, getY() + 2 - (itemHeight + 4)*(i+1), width - 4, itemHeight);
        }
        sr.end();

        sb.begin();

        for (int i = 0; i < items.size(); i++) {
            sb.draw(ItemLoader.itemGraphics.get((int)(items.get(i).getUserData())), getX() + 2 ,getY() + 2 - (itemHeight + 4)*(i+1), itemHeight, itemHeight);
        }

        sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
}
