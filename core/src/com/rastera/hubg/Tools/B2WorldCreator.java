package com.rastera.hubg.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.HUBGMain;
import com.rastera.hubg.Sprites.Brick;

public class B2WorldCreator {

    public B2WorldCreator(World world, TiledMap map) {

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();

        for (MapObject object : map.getLayers().get(1).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            rect.x = rect.x * (5 / HUBGMain.PPM);
            rect.y = rect.y * (5 / HUBGMain.PPM);
            rect.width = rect.width * (5 /HUBGMain.TILESIZE);
            rect.height = rect.height * (5 / HUBGMain.TILESIZE);

            System.out.println(rect.toString());

            new Brick(world, map, rect);
        }

    }
}
