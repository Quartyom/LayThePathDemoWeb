package com.quartyom.game_elements;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.Drawable;
import com.quartyom.interfaces.QuEvent;

public class Button implements Drawable {
    private float buttonX, buttonY, buttonW, buttonH;   // button position and size
    public Vector2 offset;                              // button offset for scrolling

    public TextureRegion normalTexture, pressedTexture;
    public NinePatch normalPatch, pressedPatch;
    private boolean hasNinePatch = false;

    Sound clickSound;

    private Label label;                    // text on button
    private Hint hint;                      // text, when you hold the button
    private Notification notification;      // tiny round icon with text on button's corner

    public InputState inputState;

    QuEvent onClickAction;
    public final LayThePath game;

    Vector2 touchPos, initialTouchPos;

    public Button(LayThePath game, QuEvent onClickAction) {
        this.onClickAction = onClickAction;
        this.game = game;

        inputState = InputState.UNTOUCHED;
        touchPos = new Vector2();
        initialTouchPos = new Vector2();
        offset = new Vector2();
    }

    public Button(String name, LayThePath game, QuEvent onClickAction) {
        this(game, onClickAction);
        normalTexture = game.buttonsAtlas.findRegion(name + "_normal");
        pressedTexture = game.buttonsAtlas.findRegion(name + "_pressed");
        clickSound = game.soundHolder.get("click_0");
    }

    public Button setNinePatch(int padding) {
        normalPatch = new NinePatch(normalTexture, padding, padding, padding, padding);
        pressedPatch = new NinePatch(pressedTexture, padding, padding, padding, padding);
        hasNinePatch = true;
        return this;
    }

    public Button setLabel(String string) {
        return setLabel(string, FontType.LOCALIZED_LIGHT);
    }

    public Button setLabel(String string, FontType fontType) {
        if (label == null) {
            label = new Label(game, string);
            label.offset = this.offset;
            label.fontType = fontType;
        } else {
            label.setString(string);
        }
        return this;
    }

    public Button setHint(String string) {
        hint = new Hint(this);
        hint.setString(string);
        return this;
    }

    public Button addNotification() {
        notification = new Notification(game);
        notification.offset = this.offset;
        return this;
    }

    public Button setNotification(String string) {
        notification.setString(string);
        return this;
    }

    public Button setSound(String name) {
        clickSound = game.soundHolder.get(name);
        return this;
    }

    public void resize(float x, float y, float w, float h) {
        buttonX = x;
        buttonY = y;
        buttonW = w;
        buttonH = h;

        if (label != null) {
            label.resize(x, y, w, h, Align.center);
        }

        if (hint != null) {
            hint.resize((int) (game.HEIGHT * (1.0f / 32.0f)), 0, game.HALF_HEIGHT * (1 / 4.0f));
        }

        if (notification != null) {
            notification.resize(buttonX, buttonY + buttonH, buttonW / 3);
        }
    }

    public void draw() {
        if (!hasNinePatch) {
            if (inputState == InputState.UNTOUCHED) {
                game.batch.draw(normalTexture, buttonX + offset.x, buttonY + offset.y, buttonW, buttonH);
            } else if (inputState == InputState.TOUCHED) {
                game.batch.draw(pressedTexture, buttonX + offset.x, buttonY + offset.y, buttonW, buttonH);
            }
        } else {
            if (inputState == InputState.UNTOUCHED) {
                normalPatch.draw(game.batch, buttonX + offset.x, buttonY + offset.y, buttonW, buttonH);
            } else if (inputState == InputState.TOUCHED) {
                pressedPatch.draw(game.batch, buttonX + offset.x, buttonY + offset.y, buttonW, buttonH);
            }
        }
        if (label != null) {
            label.draw();
        }
        if (hint != null) {
            if (game.userData.button_hints_are_on) {     // for case if hints turned off
                game.drawingQueue.add(hint);
            }
        }
        if (notification != null) {
            notification.draw();
        }
    }

    // tapped and released above the button, not sliding more than buttonH * 0.5f - click
    public void update() {
        if (game.isTouched()) {
            touchPos.x = game.touchPos.x - offset.x;
            touchPos.y = game.touchPos.y - offset.y;

            // tapped above the button
            if (touchPos.x > buttonX && touchPos.y > buttonY && touchPos.x < buttonX + buttonW && touchPos.y < buttonY + buttonH) {
                if (game.inputState == InputState.JUST_TOUCHED) {
                    inputState = InputState.TOUCHED;
                    initialTouchPos.x = game.touchPos.x;
                    initialTouchPos.y = game.touchPos.y;
                    if (clickSound != null) {
                        clickSound.play(game.userData.volume * 0.5f);
                    }
                } else if (game.touchPos.dst(initialTouchPos) > buttonH * 0.5f) {  // sliding too far - no click: untouched
                    inputState = InputState.UNTOUCHED;
                }
            } else {
                inputState = InputState.UNTOUCHED;
            }
        }
        else {
            // not touched, but was touched before
            if (inputState == InputState.TOUCHED) {
                // and finger was released above the button
                if (touchPos.x > buttonX && touchPos.y > buttonY && touchPos.x < buttonX + buttonW && touchPos.y < buttonY + buttonH) {
                    onClickAction.execute();
                }
                inputState = InputState.UNTOUCHED;
            }
        }

        if (hint != null) {
            hint.update();
        }

    }

}
