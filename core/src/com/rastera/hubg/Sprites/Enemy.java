package com.rastera.hubg.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;

import static java.lang.Math.abs;

public class Enemy extends Sprite {
    public String name;
    private int id;

    public World world;
    public Body b2body;
    private TextureRegion playerStand;
    private Texture playerImage;
    private float travelx, travely, travelr, distx, disty, distr;
    public Weapon weapon;

    public Enemy(World world, HUBGGame screen, String name, long[] info){
        super(new Texture(Gdx.files.internal("penguin.png")));
        this.weapon = new Weapon(screen);
        this.world = world;

        this.name = name;
        this.id = (int) info[3];

        this.setBounds(0, 0, 100 / HUBGMain.PPM, 100 / HUBGMain.PPM);

        this.defineEnemy(info);
        this.updateLocation(info);

        this.updateSprite();
    }

    public void updateLocation(long[] newLocation) {
        this.travelx = (float) newLocation[0] / 1000f - this.b2body.getPosition().x;
        this.travely = (float) newLocation[1] / 1000f - this.b2body.getPosition().y;

        float touchAngle = MathUtils.radiansToDegrees * (newLocation[2] / 1000f) + 180;
        float angle = MathUtils.radiansToDegrees * this.b2body.getAngle() + 180;


        if(angle < touchAngle) {
            if(abs(angle - touchAngle)<180)
                travelr = abs(angle - touchAngle);
            else travelr = abs(angle - touchAngle) - 360;
        } else {
            if (abs(angle - touchAngle) < 180)
                travelr = touchAngle - angle;
            else travelr = abs(touchAngle - angle + 360);
        }

        this.distx = (float) newLocation[0] / 1000f;
        this.disty = (float) newLocation[1] / 1000f;
        this.distr = (float) newLocation[2] / 1000f;
    }

    public void step (float dt) {
        if (this.travelx == 0 && this.travely == 0 && this.travelr == 0) {
            return;
        }
        float xstep = this.travelx / HUBGMain.SYNC_INTERVAL * dt;
        float ystep = this.travely / HUBGMain.SYNC_INTERVAL * dt;
        float rstep = this.travelr / HUBGMain.SYNC_INTERVAL * dt;

        if (xstep > 0 && this.b2body.getPosition().x + xstep < this.distx) {
            xstep = this.b2body.getPosition().x + xstep;
        } else if (xstep < 0 && this.b2body.getPosition().x + xstep > this.distx) {
            xstep = this.b2body.getPosition().x + xstep;
        } else {
            this.travelx = 0;
            xstep = this.distx;
        }

        if (ystep > 0 && this.b2body.getPosition().y + ystep < this.disty) {
            ystep = this.b2body.getPosition().y + ystep;
        } else if (ystep < 0 && this.b2body.getPosition().y + ystep > this.disty) {
            ystep = this.b2body.getPosition().y + ystep;
        } else {
            this.travely = 0;
            ystep = this.disty;
        }

        if (rstep > 0 && this.b2body.getAngle() + rstep < this.distr) {
            rstep = this.b2body.getAngle() + rstep;
        } else if (rstep < 0 && this.b2body.getAngle() + rstep > this.distr) {
            rstep = this.b2body.getAngle() + rstep;
        } else {
            this.travelr = 0;
            rstep = this.distr;
        }

        this.b2body.setTransform(xstep, ystep, rstep);
        this.updateSprite();
    }

    public void updateSprite() {
        this.setOrigin(this.getWidth() / 2, this.getHeight() / 2);
        this.setPosition(this.b2body.getPosition().x - this.getWidth() / 2, this.b2body.getPosition().y - this.getHeight() / 2);
        this.setRotation(MathUtils.radiansToDegrees * this.b2body.getAngle());
        weapon.update(this.b2body.getPosition().x, this.b2body.getPosition().y,this.b2body.getAngle());
    }

    public void defineEnemy(long[] location) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        this.b2body = this.world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(this.getWidth() / 2);

        fdef.shape = shape;

        this.b2body.createFixture(fdef);
        for (Fixture e : this.b2body.getFixtureList()) {
            e.setUserData(this.id);
        }

        this.b2body.setTransform((float) location[0] / 1000f, (float) location[1] / 1000f, (float) location[2] / 1000f);
    }

    public int getId() {
        return this.id;
    }

    public void draw(Batch sb) {
        if (this.weapon.active) {
            weapon.draw(sb);
        }

        super.draw(sb);
    }
}
