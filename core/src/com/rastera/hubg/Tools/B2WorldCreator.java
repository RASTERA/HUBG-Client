package com.rastera.hubg.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Sprites.Brick;

public class B2WorldCreator {

    // Creates all the collision bodies for the map
    public B2WorldCreator(World world, TiledMap map) {

        // Getting the rectangles in the tilemap for collision. Layer 1 is our object layer with the rectangles
        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle(); // Getting the bounds

            rect.x = rect.x * (5 / HUBGMain.PPM);  // the position of the rectangle is determined from the center. Math is used to scale and place it into the proper place
            rect.y = rect.y * (5 / HUBGMain.PPM);
            rect.width = rect.width * (5 /HUBGMain.TILESIZE);
            rect.height = rect.height * (5 / HUBGMain.TILESIZE);

            new Brick(world, map, rect); // Creating the new brick object to make the box2d bodies
        }

    }
}
