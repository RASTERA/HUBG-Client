package com.rastera.hubg.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public abstract class InteractiveTileObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;

    public InteractiveTileObject(World world, TiledMap map, Rectangle bounds) {
        this.world = world;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type= BodyDef.BodyType.StaticBody;
        bdef.position.set(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight());

        this.body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth(), bounds.getHeight());
        fdef.shape = shape;

        this.body.createFixture(fdef);

        for (Fixture f : this.body.getFixtureList()) {
            f.setUserData(1);
        }
    }
}
