package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;

public class Weapon extends Sprite {
    private HUBGGame game;
    private float playerSize;
    private boolean active = false;

    public Weapon(HUBGGame game, float playerSize){
        super(game.getWeaponAtlas().findRegion("AK47"));

        this.game = game;
        this.playerSize = playerSize;
    }

    public void setCurrentWeapon(String Weapon) {
        setRegion(game.getWeaponAtlas().findRegion(Weapon));
        setSize(this.getWidth() / HUBGMain.PPM, this.getHeight() / HUBGMain.PPM);
    }

    public void update(float x, float y, float r) {

        setOrigin(0, getHeight() / 2);
        setRotation(MathUtils.radiansToDegrees * r + 180);
        setPosition(x, y);

        System.out.println(getOriginX() + " " + getOriginY() + " " + r);
    }
}
