package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;

public class Weapon extends Sprite {
    private HUBGGame game;
    private boolean active = false;
    private String name;

    public Weapon(HUBGGame game){
        super(game.getWeaponAtlas().findRegion("AK47"));

        this.name = "AK47";
        this.game = game;
    }

    public void setCurrentWeapon(String Weapon) {
        this.name = Weapon;
        this.setRegion(this.game.getWeaponAtlas().findRegion(Weapon));
        this.setSize(this.getWidth() / 5/ HUBGMain.PPM, this.getHeight() /5/ HUBGMain.PPM);
    }

    public String getCurrentWeapon() {
        return this.name;
    }

    public void update(float x, float y, float r) {

        this.setOrigin(0 , this.getHeight() / 2);
        this.setPosition(x, y - this.getHeight() / 2);
        this.setRotation(MathUtils.radiansToDegrees * r);

    }

    public int getScopeSize() {
        int scopeSize = 5;
        return scopeSize;
    }
}
