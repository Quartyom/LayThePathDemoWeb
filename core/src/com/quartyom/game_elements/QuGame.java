package com.quartyom.game_elements;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;
import java.util.Map;

public abstract class QuGame implements ApplicationListener, InputProcessor {

    protected QuScreen screen;
    protected QuScreen defaultScreen;
    private Map<String, QuScreen> screens;

    public InputState inputState;
    public boolean isTouched, isTouchRead;
    public Vector2 touchPos;

    // размеры экрана
    public float WIDTH, HALF_WIDTH, HEIGHT, HALF_HEIGHT;

    public QuGame() {
        screens = new HashMap<>();
        inputState = InputState.UNTOUCHED;
        touchPos = new Vector2();
    }

    @Override
    public void dispose() {
        for (Screen item : screens.values()) {
            item.dispose();
        }
        screens.clear();
    }

    public void setScreen(String name) {
        QuScreen screen = screens.get(name);
        if (this.screen != null) {
            this.screen.hide();
        }
        if (screen != null) {
            this.screen = screen;
        } else if (defaultScreen != null) {
            this.screen = defaultScreen;
            Gdx.app.log("QuGame", "Screen " + name + " is not found: set default Screen");
        } else {
            Gdx.app.error("QuGame", "Screen " + name + " is not found, default Screen is not set");
            return;
        }
        this.screen.show();
        if (this.screen.isResizeNeeded) {
            this.screen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            this.screen.isResizeNeeded = false;
        }
    }

    public Screen getScreen() {
        return screen;
    }

    public void add(String name, QuScreen screen) {
        if (screens.containsKey(name)) {
            screens.get(name).dispose();
            Gdx.app.log("QuGame", "Screen " + name + " is rewritten");
        }

        screens.put(name, screen);
    }

    public void addDefault(String name, QuScreen screen) {
        add(name, screen);
        defaultScreen = screen;
    }

    @Override
    public void pause() {
        if (screen != null) screen.pause();
    }

    @Override
    public void resume() {
        if (screen != null) screen.resume();
    }

    @Override
    public void render() {
        isTouchRead = false;
        if (Gdx.input.isTouched()) {
            if (inputState == InputState.TOUCHED) {
            } else if (inputState == InputState.JUST_TOUCHED) {
                inputState = InputState.TOUCHED;
            } else {
                inputState = InputState.JUST_TOUCHED;
            }
            isTouched = true;
            touchPos.x = Gdx.input.getX() - HALF_WIDTH;
            touchPos.y = HALF_HEIGHT - Gdx.input.getY();
        } else {
            if (inputState == InputState.UNTOUCHED) {
            } else if (inputState == InputState.JUST_UNTOUCHED) {
                inputState = InputState.UNTOUCHED;
            } else {
                inputState = InputState.JUST_UNTOUCHED;
            }
            isTouched = false;
        }

        if (screen != null) {
            screen.render(Gdx.graphics.getDeltaTime());
        }
    }

    public boolean isTouched() {
        if (isTouchRead) {
            return false;
        } else {
            return isTouched;
        }
    }

    @Override
    public void resize(int width, int height) {
        if (screen != null) {
            screen.resize(width, height);
            screen.isResizeNeeded = false;
        }
        for (QuScreen item : screens.values()) {
            item.isResizeNeeded = true;
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
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
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
