package com.rastera.hubg.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;

public class Coin extends InteractiveTileObject {

    public Coin(World world, TiledMap map, Rectangle bounds) {
        super(world, map, bounds);

    }
}
