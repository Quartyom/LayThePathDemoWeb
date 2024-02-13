package com.quartyom.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.quartyom.LayThePath;

public class Scroller {
    private float viewX, viewY, viewW, viewH;

    private LayThePath game;

    public Vector2 value;

    private Vector2 prevTouchPos;

    public InputState inputState;

    public boolean physicsOn;
    private Vector2 scrollSpeed;

    public Scroller(LayThePath game) {
        this.game = game;

        value = new Vector2();

        prevTouchPos = new Vector2();
        scrollSpeed = new Vector2();

        inputState = InputState.UNTOUCHED;
    }

    public void resizeFull() {
        resize(-game.HALF_WIDTH, -game.HALF_HEIGHT, game.WIDTH, game.HEIGHT);
    }

    public void resize(float viewX, float viewY, float viewW, float viewH) {
        this.viewX = viewX;
        this.viewY = viewY;
        this.viewW = viewW;
        this.viewH = viewH;
    }

    public void update() {
        // если нажато и попали ли по области
        if (game.isTouched()
                && game.touchPos.x > viewX && game.touchPos.y > viewY && game.touchPos.x < viewX + viewW && game.touchPos.y < viewY + viewH) {
            game.isTouchRead = true;
            if (game.inputState == InputState.JUST_TOUCHED) {
                inputState = InputState.JUST_TOUCHED;
            } else if (inputState == InputState.JUST_TOUCHED || inputState == InputState.TOUCHED) {
                inputState = InputState.TOUCHED;
                scrollSpeed.x = game.touchPos.x - prevTouchPos.x;    // по факту, это не скорость, а разница
                scrollSpeed.y = game.touchPos.y - prevTouchPos.y;
                value.x += scrollSpeed.x;
                value.y += scrollSpeed.y;
            }
            prevTouchPos.x = game.touchPos.x;
            prevTouchPos.y = game.touchPos.y;
            return;

        }
        // иначе было ли нажато
        else if (inputState == InputState.JUST_TOUCHED || inputState == InputState.TOUCHED) {
            inputState = InputState.JUST_UNTOUCHED;

            if (physicsOn) {
                float coef = 1.25f;  // чтобы скроллинг был быстрее
                scrollSpeed.x = scrollSpeed.x / Gdx.graphics.getDeltaTime() * coef;  // до этого момента это была не скорость, а измененение
                scrollSpeed.y = scrollSpeed.y / Gdx.graphics.getDeltaTime() * coef;
            }
        } else {
            inputState = InputState.UNTOUCHED;
        }

        // прокрутка по инерции
        if (physicsOn) {
            float coef = game.HEIGHT * 2;

            if (scrollSpeed.x < 0) {
                scrollSpeed.x += coef * Gdx.graphics.getDeltaTime();
                if (scrollSpeed.x > 0) {
                    scrollSpeed.x = 0;
                }
            } else if (scrollSpeed.x > 0) {
                scrollSpeed.x -= coef * Gdx.graphics.getDeltaTime();
                if (scrollSpeed.x < 0) {
                    scrollSpeed.x = 0;
                }
            }

            if (scrollSpeed.y < 0) {
                scrollSpeed.y += coef * Gdx.graphics.getDeltaTime();
                if (scrollSpeed.y > 0) {
                    scrollSpeed.y = 0;
                }
            } else if (scrollSpeed.y > 0) {
                scrollSpeed.y -= coef * Gdx.graphics.getDeltaTime();
                if (scrollSpeed.y < 0) {
                    scrollSpeed.y = 0;
                }
            }

            value.x += scrollSpeed.x * Gdx.graphics.getDeltaTime();
            value.y += scrollSpeed.y * Gdx.graphics.getDeltaTime();
        }

    }

}
