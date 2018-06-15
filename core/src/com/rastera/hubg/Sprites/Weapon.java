package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Util.WeaponList;

import java.util.HashMap;

public class Weapon extends Sprite {
    private HUBGGame game;
    public boolean active = false;
    private int gun;

    public Weapon(HUBGGame game){
        super(WeaponList.blank);
        this.game = game;
    }

    public void setCurrentWeapon(int weapon) {

        this.gun = weapon;

        if (weapon != 0) {

            System.out.println("switched");
            this.setTexture(WeaponList.graphics.get(weapon));
            this.setSize(this.getTexture().getWidth() / 4 / HUBGMain.PPM, this.getTexture().getHeight() / 4 / HUBGMain.PPM);
            active = true;
        } else {
            active = false;
        }
    }

    public void update(float x, float y, float r) {
        this.setOrigin(0 , this.getHeight() / 2);
        this.setPosition(x, y - this.getHeight() / 2);
        this.setRotation(MathUtils.radiansToDegrees * r);
    }

    public int getCurrentWeapon() {
        return gun;
    }

    public int getScopeSize() {
        if (active) {
            return WeaponList.scope.get(gun);
        } else {
            return 2000+2;
        }
    }

    public int getFireRate() {
        if (active) {
            return WeaponList.rof.get(gun);
        } else {
            return 0;
        }
    }

    public int getAccuracy() {
        if (active) {
            return WeaponList.accuracy.get(gun);
        } else {
            return 0;
        }
    }
}