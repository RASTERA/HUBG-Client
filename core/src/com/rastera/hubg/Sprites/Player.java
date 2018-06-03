package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.Main;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Screens.PlayScreen;

public class Player extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private Texture playerImage;

    public Player(World world, HUBGGame screen, float[] location){
        super(new Texture(Gdx.files.internal("penguin.png")));

        this.world = world;

        definePlayer(location);

        setBounds(0, 0, 100 / Main.PPM, 100 / Main.PPM);

        update(1);
    }

    public void update(float dt) {
        setOrigin(50 / Main.PPM, 50 / Main.PPM);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation(MathUtils.radiansToDegrees * b2body.getAngle());
    }

    public void definePlayer(float[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50 / Main.PPM);

        fdef.shape = shape;

        b2body.createFixture(fdef);

        b2body.setTransform(location[0], location[1], location[2]);
    }

    public Vector2 getLocation() {
        return new Vector2(b2body.getPosition().x, b2body.getPosition().y);
    }

    public float getAngle() {
        return b2body.getAngle();
    }
}
