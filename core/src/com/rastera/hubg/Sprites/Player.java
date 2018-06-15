package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;

import com.rastera.hubg.desktop.Main;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private float health = 100;
    private float energy = 100;
    private TextureRegion marioStand;
    private Texture playerImage;
    public Weapon weapon;
    public int ammo = 0;
    public int[] gunAmmo = {0, 0};
    public int[] playerWeapons = {-1001, -1002};


    public Player(World world, HUBGGame screen, long[] location){
        super(HUBGMain.getSkin(Main.session.getSkin())); //penguin.png")));

        this.world = world;
        this.weapon = new Weapon(screen);

        this.definePlayer(location);

        this.setBounds(0, 0, 100 / HUBGMain.PPM, 100 / HUBGMain.PPM);

        this.update(1);
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
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        this.setPosition(this.b2body.getPosition().x - this.getWidth() / 2, this.b2body.getPosition().y - this.getHeight() / 2);
        this.setRotation(MathUtils.radiansToDegrees * this.b2body.getAngle());
        this.weapon.update(this.b2body.getPosition().x, this.b2body.getPosition().y, this.getAngle());
    }

    public void definePlayer(long[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        this.b2body = this.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50 / HUBGMain.PPM);
        fdef.shape = shape;

        this.b2body.createFixture(fdef);
        // User is -1000
        this.b2body.getFixtureList().get(0).setUserData(-1000);

        this.b2body.setTransform((float) location[0] / 1000f, (float) location[1] / 1000f, (float) location[2] / 1000f);
    }

    public boolean damage(float damage) {
        this.health -= damage;

        System.out.println(this.health);

        if (this.health <= 0) {
            System.out.print("Player dead");
            return true;
        }
        return false;
    }

    @Override
    public void draw (Batch batch) {
        if (weapon.active) {
            this.weapon.draw(batch);
        }
        super.draw(batch);
    }

    public Vector2 getLocation() {
        return new Vector2(this.b2body.getPosition().x, this.b2body.getPosition().y);
    }

    public float getAngle() {
        return this.b2body.getAngle();
    }

    public float getHealth() {
        return this.health;
    }

    public float getEnergy() {
        return this.energy;
    }
}
