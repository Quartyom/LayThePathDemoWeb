package com.quartyom.game_elements;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.QuEvent;

import java.util.ArrayList;

public class SwitchButton {
    private float buttonX, buttonY, buttonW, buttonH;

    public ArrayList<TextureRegion> textures;

    public int state;
    public boolean recentlyChanged = false;
    public boolean toChangeStateOnClick = true;

    LayThePath game;
    public Sound click_sound;
    QuEvent onChangeStateAction;

    public SwitchButton(LayThePath game) {
        this.game = game;
        textures = new ArrayList<>();
        click_sound = game.soundHolder.get("click_0");
    }

    public SwitchButton(LayThePath game, QuEvent onChangeStateAction) {
        this.game = game;
        textures = new ArrayList<>();
        click_sound = game.soundHolder.get("click_0");
        this.onChangeStateAction = onChangeStateAction;
    }

    public SwitchButton add(String path) {
        textures.add(game.buttonsAtlas.findRegion(path));
        return this;
    }

    public SwitchButton setSound(String name) {
        click_sound = game.soundHolder.get(name);
        return this;
    }

    public void resize(float x, float y, float w, float h) {
        buttonX = x;
        buttonY = y;
        buttonW = w;
        buttonH = h;
    }

    public void draw() {
        game.batch.draw(textures.get(state), buttonX, buttonY, buttonW, buttonH);
    }

    // нажал и отпустил - клик
    public void update() {
        // если нажато
        if (game.inputState == InputState.JUST_TOUCHED) {

            // попали ли по кнопке
            if (game.touchPos.x > buttonX && game.touchPos.y > buttonY && game.touchPos.x < buttonX + buttonW && game.touchPos.y < buttonY + buttonH) {

                if (toChangeStateOnClick) {
                    state++;
                    if (state >= textures.size()) {
                        state = 0;
                    }
                }

                recentlyChanged = true;
                if (click_sound != null) { click_sound.play(game.userData.volume * 0.5f); }
                if (onChangeStateAction != null) { onChangeStateAction.execute(); }
                return;
            }
        }
        recentlyChanged = false;

    }
}
