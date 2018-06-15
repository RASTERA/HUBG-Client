package com.rastera.hubg.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Screens.HUBGGame;
import com.rastera.hubg.Util.ItemList;

public class Item extends Sprite {

    public Body body;
    private int itemType;

    public Item(float x, float y, int itemType, World world) {
        super(ItemList.itemGraphics.get(itemType));

        Texture image = ItemList.itemGraphics.get(itemType);

        float scale = Math.max(image.getWidth() / (100 / HUBGMain.PPM), image.getHeight() / (100 / HUBGMain.PPM));

        this.setBounds(x - image.getWidth() / scale / 2, y - image.getHeight() / scale / 2, image.getWidth() / scale, image.getHeight() / scale);
        this.setOrigin(image.getWidth() / scale / 2, image.getHeight() / scale / 2);
        this.setRotation(MathUtils.random(360));
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
