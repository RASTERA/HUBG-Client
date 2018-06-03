package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.PlayScreen;

public class Mario extends Sprite {
    public World world;
    public Body b2body;
    private TextureRegion marioStand;

    public Mario(World world, PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));

        this.world = world;
        defineMario();

        marioStand = new TextureRegion(getTexture(), 1, 11, 16, 16);
        setBounds(0, 0, 16 / HUBGMain.PPM, 16 / HUBGMain.PPM);
        setRegion(marioStand);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
    }

    public void defineMario() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / HUBGMain.PPM, 32 / HUBGMain.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(7 / HUBGMain.PPM);

        fdef.shape = shape;

        b2body.createFixture(fdef);
    }
}
