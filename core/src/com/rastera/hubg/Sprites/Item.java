package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Util.ItemList;

public class Item extends Sprite {

    public Body body;
    private int itemType;

    public Item(float x, float y, int itemType, World world) {
        super(ItemList.itemGraphics.get(itemType));
        this.setBounds(x, y, 80 / HUBGMain.PPM, 80 / HUBGMain.PPM);
        this.itemType = itemType;

        this.defineBody(x, y, world);
    }

    public void defineBody(float x, float y, World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(50 / HUBGMain.PPM);

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);

        this.body = world.createBody(bdef);

        fdef.shape = shape;
        fdef.isSensor = true;

        this.body.createFixture(fdef);

        for (Fixture f : this.body.getFixtureList()) {
            f.setUserData(this.itemType);
        }

        this.body.setUserData(this);
    }

    public int getItemType() {
        return this.itemType;
    }
}
