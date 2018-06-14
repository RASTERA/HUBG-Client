package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Base64Coder;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.desktop.Communicator;
import org.json.JSONObject;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import com.rastera.hubg.desktop.Communicator;
import com.rastera.hubg.desktop.Main;
import com.rastera.hubg.desktop.Rah;

import javax.imageio.ImageIO;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private float health;
    private float energy = 100;
    private TextureRegion marioStand;
    private Texture playerImage;
    public Weapon weapon;
    public int[] playerWeapons = new int[2];


    public Player(World world, HUBGGame screen, long[] location){
        super(HUBGMain.getSkin(Main.session.getSkin())); //penguin.png")));

        this.world = world;
        weapon = new Weapon(screen, 50 / HUBGMain.PPM);

        definePlayer(location);

        weapon.setCurrentWeapon("1911");

        setBounds(0, 0, 100 / HUBGMain.PPM, 100 / HUBGMain.PPM);

        update(1);
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void decEnergy(float amount) {
        this.energy = this.energy - amount < 0 ? 0 : this.energy - amount;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public void update(float dt) {
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation(MathUtils.radiansToDegrees * b2body.getAngle());
        weapon.update(b2body.getPosition().x, b2body.getPosition().y, getAngle());
    }

    public void definePlayer(long[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50 / HUBGMain.PPM);
        fdef.shape = shape;

        b2body.createFixture(fdef);
        // User is -1000
        b2body.getFixtureList().get(0).setUserData(-1000);

        b2body.setTransform((float) location[0] / 1000f, (float) location[1] / 1000f, (float) location[2] / 1000f);
    }

    public boolean damage(float damage) {
        this.health -= damage;

        System.out.println(health);

        if (health <= 0) {
            System.out.print("Player dead");
            return true;
        }
        return false;
    }

    @Override
    public void draw (Batch batch) {
        weapon.draw(batch);
        super.draw(batch);
    }

    public Vector2 getLocation() {
        return new Vector2(b2body.getPosition().x, b2body.getPosition().y);
    }

    public float getAngle() {
        return b2body.getAngle();
    }

    public float getHealth() {
        return health;
    }

    public float getEnergy() {
        return energy;
    }
}
