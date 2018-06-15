package com.rastera.hubg.Scene.Sprite;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Sprites.Item;
import com.rastera.hubg.Util.ItemList;

import java.util.ArrayList;

public class ItemPickUp extends Sprite {
    private int width = 200;  // Declaring the basic bounds for the item pickup box
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

    /**
     * Updates the location fo the UI according to the screen size
     *
     * @param screenHeight The height of the screen
     * @param screenWidth The Width of the screen
     */
    public void updateLocation (int screenHeight, int screenWidth) {
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        this.setPosition(screenWidth / -2 + 5, 0);
    }

    /**
     * Process mouse click
     *
     * @param x The x coordinate of the click
     * @param y The y coordinate of the click
     * @param mb The mouse button
     * @return A boolean that describes if a item was clicked or not
     */
    public boolean processKeyDown (int x, int y, int mb) {
        System.out.println(x + " " + y);
        x -= this.screenWidth / 2;
        y = this.screenHeight / 2 - y;

        for (int i = 0; i < this.items.size(); i++) {
            if (x > this.getX() + 2 && x < this.getX() + this.width - 2  && y > this.getY() + 2 - (this.itemHeight + 4)*(i+1) && y < this.getY() + 2 - (this.itemHeight + 4)*(i+1) + this.itemHeight){

                this.game.pickupItem(this.items.get(i));  // Calling the main item pickup method to send message to server
                return true;
            }
        }

        return false;

    }

    /**
     * Drawing the UI
     * @param sb The batcher used to draw
     */
    public void draw (Batch sb) {
        // Enabling transparency in opengl
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Drawing the boxes and the different items

        this.sr.setColor(0.2f, 0.2f, 0.2f, 0.4f);
        this.sr.begin(ShapeRenderer.ShapeType.Filled);
        this.sr.rect(this.getX(), -(this.itemHeight + 4) * this.items.size(), this.width, (this.itemHeight + 4) * this.items.size());

        this.sr.setColor(0.1f, 0.1f, 0.1f, 1f);
        for (int i = 0; i < this.items.size(); i++) {
            this.sr.rect(this.getX() + 2, this.getY() + 2 - (this.itemHeight + 4)*(i+1), this.width - 4, this.itemHeight);
        }
        this.sr.end();

        sb.begin();

        Texture g;
        float scale;
        Item it;

        font.getData().setScale(0.2f);

        // Drawing the icons and the description/names
        for (int i = 0; i < this.items.size(); i++) {
            g = ItemList.itemGraphics.get(this.items.get(i).getUserData());
            scale = Math.max(g.getWidth() / (this.itemHeight), g.getHeight() / (this.itemHeight));
            sb.draw(g, this.getX() + 4 + (this.itemHeight - g.getWidth() / scale) / 2 , this.getY() + 2 - (this.itemHeight + 4)*(i+1) - (-this.itemHeight + g.getHeight() / scale) / 2, g.getWidth() / scale, g.getHeight() / scale);

            it = (Item) this.items.get(i).getBody().getUserData();
            font.draw(sb, ItemList.itemName.get(it.getItemType()), this.getX() + 4 + itemHeight, this.getY() - 10 - (this.itemHeight + 4)*(i));
            font.draw(sb, ItemList.itemDescription.get(it.getItemType()), this.getX() + 4 + itemHeight, this.getY() - 35 - (this.itemHeight + 4)*(i));

        }
        sb.end();

        // Disabling to allow normal drawing
        Gdx.gl.glDisable(GL20.GL_BLEND);

    }
}
