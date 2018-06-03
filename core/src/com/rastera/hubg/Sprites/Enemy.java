package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.Main;
import com.rastera.hubg.Screens.HUBGGame;

public class Enemy extends Sprite {
    public String name;
    private int id;

    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private Texture playerImage;
    private float travelx, travely, travelr, distx, disty, distr;

    public Enemy(World world, HUBGGame screen, String name, float[] info){
        super(new Texture(Gdx.files.internal("penguin.png")));

        this.world = world;

        this.name = name;
        this.id = (int) info[3];

        setBounds(0, 0, 100, 100);

        defineEnemy(info);
        updateLocation(info);

        updateSprite();
    }

    public void updateLocation(float[] newLocation) {
        travelx = newLocation[0] - b2body.getPosition().x;
        travely = newLocation[1] - b2body.getPosition().y;
        travelr = newLocation[2] - b2body.getAngle();

        distx = newLocation[0];
        disty = newLocation[1];
        distr = newLocation[2];
    }

    public void step (float dt) {
        if (travelx == 0 && travely == 0 && travelr == 0) {
            return;
        }

        float xstep = travelx / Main.SYNC_INTERVAL * dt;
        float ystep = travely / Main.SYNC_INTERVAL * dt;
        float rstep = travelr / Main.SYNC_INTERVAL * dt;

        if (xstep > 0 && b2body.getPosition().x + xstep < distx) {
            xstep = b2body.getPosition().x + xstep;
        } else if (xstep < 0 && b2body.getPosition().x + xstep > distx) {
            xstep = b2body.getPosition().x + xstep;
        } else {
            travelx = 0;
            xstep = distx;
        }

        if (ystep > 0 && b2body.getPosition().y + ystep < disty) {
            ystep = b2body.getPosition().y + ystep;
        } else if (ystep < 0 && b2body.getPosition().y + ystep > disty) {
            ystep = b2body.getPosition().y + ystep;
        } else {
            travely = 0;
            ystep = disty;
        }

        if (rstep > 0 && b2body.getAngle() + rstep < distr) {
            rstep = b2body.getAngle() + rstep;
        } else if (rstep < 0 && b2body.getAngle() + rstep > distr) {
            rstep = b2body.getAngle() + rstep;
        } else {
            travelr = 0;
            rstep = distr;
        }

        b2body.setTransform(xstep, ystep, rstep);
        updateSprite();
    }

    public void updateSprite() {
        setOrigin(50, 50);
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRotation(MathUtils.radiansToDegrees * b2body.getAngle());
    }

    public void defineEnemy(float[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50);

        fdef.shape = shape;

        b2body.createFixture(fdef);
        for (Fixture e : b2body.getFixtureList()) {
            e.setUserData(this.id);
        }

        b2body.setTransform(location[0], location[1], location[2]);
    }

    public int getId() {
        return id;
    }
}
