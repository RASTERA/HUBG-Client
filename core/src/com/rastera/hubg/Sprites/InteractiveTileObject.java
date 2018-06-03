package com.rastera.hubg.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.Main;

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
        bdef.position.set((float) (bounds.getX() + bounds.getWidth() / 2), (float) (bounds.getY() + bounds.getHeight() / 2));

        body = world.createBody(bdef);

        shape.setAsBox((float)bounds.getWidth(), (float) bounds.getHeight());
        fdef.shape = shape;

        body.createFixture(fdef);

        for (Fixture f : body.getFixtureList()) {
            f.setUserData(1);
        }
    }
}