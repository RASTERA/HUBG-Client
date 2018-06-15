package com.rastera.hubg;

import com.badlogic.gdx.InputAdapter;
import com.rastera.hubg.Scene.HUD;

public class customInputProcessor extends InputAdapter {
    public HUD gameHUD;

    public customInputProcessor(HUD gameHUD) {
        this.gameHUD = gameHUD;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        this.gameHUD.processKeyDown(screenX, screenY, button);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
