package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Util.ItemLoader;

import java.util.ArrayList;

public class ItemPickUp extends Sprite {
    private int weaponID = 0;

    private int width = 150;
    private int itemHeight = 50;

    public ArrayList<Fixture> items;

    private ShapeRenderer sr;

    private int screenHeight;
    private int screenWidth;
    private HUBGGame game;
    private BitmapFont font;

    public ItemPickUp (ArrayList<Fixture> items, BitmapFont font, HUBGGame game, ShapeRenderer sr) {
        this.sr = sr;
        this.items = items;
        this.font = font;
        this.game = game;
    }

    public void updateLocation (int screenHeight, int screenWidth) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.setPosition(screenWidth / -2 + 5, 0);
    }

    public void processKeyDown (int x, int y, int mb) {
        System.out.println(x + " " + y);
        x -= this.screenWidth / 2;
        y = this.screenHeight / 2 - y;

        for (int i = 0; i < this.items.size(); i++) {
            System.out.println("clicking " + x + " " + y + " " +(this.getX() + 2) + " " + (this.getX() + this.width - 2 + " " + (this.getY() + 2 - (this.itemHeight + 4)*(i+1)) + " " + (this.getY() + 2 - (this.itemHeight + 4)*(i+1) + this.itemHeight)));

            if (x > this.getX() + 2 && x < this.getX() + this.width - 2  && y > this.getY() + 2 - (this.itemHeight + 4)*(i+1) && y < this.getY() + 2 - (this.itemHeight + 4)*(i+1) + this.itemHeight){
                this.game.pickupItem(this.items.get(i));
            }
        }

    }

    public void draw (Batch sb) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        this.sr.setColor(0.2f, 0.2f, 0.2f, 0.4f);
        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        this.sr.rect(this.getX(), -(this.itemHeight + 4) * this.items.size(), this.width, (this.itemHeight + 4) * this.items.size());

        this.sr.setColor(0.1f, 0.1f, 0.1f, 1f);
        for (int i = 0; i < this.items.size(); i++) {
            this.sr.rect(this.getX() + 2, this.getY() + 2 - (this.itemHeight + 4)*(i+1), this.width - 4, this.itemHeight);
        }
        this.sr.end();

        sb.begin();

        for (int i = 0; i < this.items.size(); i++) {
            sb.draw(ItemLoader.itemGraphics.get(this.items.get(i).getUserData()), this.getX() + 2 , this.getY() + 2 - (this.itemHeight + 4)*(i+1), this.itemHeight, this.itemHeight);
        }

        sb.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
}
