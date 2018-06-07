package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
    private float health = 100;
    private TextureRegion marioStand;
    private Texture playerImage;


    public Player(World world, HUBGGame screen, float[] location){
        super(HUBGMain.getSkin(Main.session.getSkin())); //penguin.png")));

        this.world = world;

        definePlayer(location);

        setBounds(0, 0, 100 / HUBGMain.PPM, 100 / HUBGMain.PPM);

        update(1);
    }

    public void update(float dt) {
        setOrigin(getWidth() / 2, getHeight() / 2);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation(MathUtils.radiansToDegrees * b2body.getAngle());
    }

    public void definePlayer(float[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50 / HUBGMain.PPM);

        fdef.shape = shape;

        b2body.createFixture(fdef);

        b2body.setTransform(location[0], location[1], location[2]);
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

    public Vector2 getLocation() {
        return new Vector2(b2body.getPosition().x, b2body.getPosition().y);
    }

    public float getAngle() {
        return b2body.getAngle();
    }
}
