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
        setBounds(x, y, 60 / HUBGMain.PPM, 60 / HUBGMain.PPM);
        this.itemType = itemType;

        defineBody(x, y, world);
    }

    public void defineBody(float x, float y, World world) {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(100 / HUBGMain.PPM);

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(x, y);

        body = world.createBody(bdef);

        fdef.shape = shape;
        fdef.isSensor = true;

        body.createFixture(fdef);

        for (Fixture f : body.getFixtureList()) {
            f.setUserData(itemType);
        }

        body.setUserData(this);
    }

    public int getItemType() {
        return itemType;
    }
}
