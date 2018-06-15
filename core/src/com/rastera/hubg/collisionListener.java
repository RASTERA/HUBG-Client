package com.rastera.hubg;

import com.badlogic.gdx.physics.box2d.*;
import com.rastera.hubg.Scene.HUD;

import java.util.ArrayList;

public class collisionListener implements ContactListener {

    public ArrayList<Fixture> objectsInRange;
    public HUD gameHUD;

    public collisionListener (HUD gameHUD) {
        this.objectsInRange = new ArrayList<>();
        gameHUD.setItemArray(this.objectsInRange);
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.getUserData() != null && (Integer) a.getUserData() == -1000){
            if (b.getUserData() != null && (Integer) b.getUserData() < -1000){
                this.objectsInRange.add(b);
            }
        } else if (b.getUserData() != null && (Integer) b.getUserData() == -1000){
            if (a.getUserData() != null && (Integer) a.getUserData() < -1000){
                this.objectsInRange.add(a);
            }
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        if (a.getUserData() != null && (Integer) a.getUserData() == -1000){
            if (b.getUserData() != null && (Integer) b.getUserData() < -1000){
                this.objectsInRange.remove(b);
            }
        } else if (b.getUserData() != null && (Integer) b.getUserData() == -1000){
            if (a.getUserData() != null && (Integer) a.getUserData() < -1000){
                this.objectsInRange.remove(a);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
