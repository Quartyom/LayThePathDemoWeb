package com.quartyom.game_elements;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import com.quartyom.LayThePath;

// двойной тап нажал отпустил через 100мс нажал через 100мс
public class PressTimer {
    private float buttonX, buttonY, buttonW, buttonH;
    public Vector2 offset;

    public InputState inputState;

    LayThePath game;

    Vector2 touchPos, prevTouchPos;

    private long whenJustTouched, whenJustUntouched;
    private boolean isDoubleTap;

    public PressTimer(LayThePath game) {
        this.game = game;

        inputState = InputState.UNTOUCHED;
        touchPos = new Vector2();
        prevTouchPos = new Vector2();
        offset = new Vector2();
    }

    public void resize(float x, float y, float w, float h) {
        buttonX = x;
        buttonY = y;
        buttonW = w;
        buttonH = h;
    }

    private boolean toReset = false;

    public void reset() {
        inputState = InputState.UNTOUCHED;
        whenJustTouched = 0;
        whenJustUntouched = 0;
    }

    // нажал и отпустил - клик
    public void update() {
        // если нажато
        if (game.isTouched) {
            touchPos.x = game.touchPos.x - offset.x;
            touchPos.y = game.touchPos.y - offset.y;

            // попали ли по кнопке
            if (touchPos.x > buttonX && touchPos.y > buttonY && touchPos.x < buttonX + buttonW && touchPos.y < buttonY + buttonH) {
                if (game.inputState == InputState.JUST_TOUCHED) {
                    inputState = InputState.TOUCHED;

                    long currentTime = TimeUtils.millis();

                    // двойное ли нажатие?
                    // куча магических чисел
                    if (currentTime - whenJustUntouched <= 200 && whenJustUntouched - whenJustTouched <= 200) {
                        if (touchPos.dst(prevTouchPos) <= game.WIDTH / 8) {
                            isDoubleTap = true;
                        }
                    }
                    prevTouchPos = new Vector2(touchPos);
                    whenJustTouched = TimeUtils.millis();

                } else {
                    if (touchPos.dst(prevTouchPos) >= game.WIDTH / 16) {
                        toReset = true;
                    }
                }
            } else {
                inputState = InputState.UNTOUCHED;
            }
            // не попали по кнопке
        }
        // не нажато
        else {
            if (toReset) {
                reset();
                toReset = false;
            }
            // но было нажато
            if (inputState == InputState.TOUCHED) {
                // палец убрали над кнопкой = клик
                if (touchPos.x > buttonX && touchPos.y > buttonY && touchPos.x < buttonX + buttonW && touchPos.y < buttonY + buttonH) {
                    whenJustUntouched = TimeUtils.millis();
                }
                inputState = InputState.UNTOUCHED;

            }
        }
    }

    public boolean handle_double_tap() {
        boolean result = isDoubleTap;
        isDoubleTap = false;
        return result;
    }
}
