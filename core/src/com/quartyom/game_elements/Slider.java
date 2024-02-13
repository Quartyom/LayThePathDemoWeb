package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.quartyom.LayThePath;

// Хитбокс слайдэра задаётся, параметры элементов слайдэра вычисляются, чтобы поместиться в хитбокс
// Значение слайдэра изменяется в пределах от 0 до 1
public class Slider {
    private float sliderX, sliderY, sliderW, sliderH;   // хитбокс слайдэра
    private float scaleX, scaleY, scaleW, scaleH;       // размеры шкалы

    public float value;

    private TextureRegion normalTexture, pressedTexture, lineTexture;
    private NinePatch linePatch;

    public InputState inputState;
    LayThePath game;

    public Slider(LayThePath game) {
        this.game = game;

        inputState = InputState.UNTOUCHED;
    }

    public Slider(String name, LayThePath game) {
        this(game);

        normalTexture = game.slidersAtlas.findRegion(name + "_untouched");
        pressedTexture = game.slidersAtlas.findRegion(name + "_touched");
        lineTexture = game.slidersAtlas.findRegion(name + "_scale");


        linePatch = new NinePatch(lineTexture, 31, 31, 0, 0); // пока так

    }

    public void resize(float x, float y, float w, float h) {
        sliderX = x;
        sliderY = y;
        sliderW = w;
        sliderH = h;

        scaleH = sliderH / 3;
        scaleW = sliderW - sliderH; // 2 раза по половине (слева и справа)

        scaleX = sliderX + sliderH / 2;
        scaleY = sliderY + sliderH / 2 - scaleH / 2;

    }

    public void draw() {

        linePatch.draw(game.batch, scaleX, scaleY, scaleW, scaleH);

        if (inputState == InputState.UNTOUCHED) {
            game.batch.draw(normalTexture, sliderX + scaleW * value, sliderY, sliderH, sliderH);
        } else if (inputState == InputState.TOUCHED) {
            game.batch.draw(pressedTexture, sliderX + scaleW * value, sliderY, sliderH, sliderH);
        }


    }

    public void update() {
        // если нажато
        if (game.isTouched()) {

            if (game.inputState == InputState.JUST_TOUCHED) {
                // попали ли
                if (game.touchPos.x > sliderX && game.touchPos.y > sliderY && game.touchPos.x < sliderX + sliderW && game.touchPos.y < sliderY + sliderH) {
                    inputState = InputState.TOUCHED;
                } else {
                    inputState = InputState.UNTOUCHED;
                }
                // не попали по кнопке
            }
            if (inputState == InputState.TOUCHED) {
                game.isTouchRead = true;
                value = (game.touchPos.x - scaleX) / scaleW;
                if (value < 0) {
                    value = 0;
                } else if (value > 1) {
                    value = 1;
                }
            }
        } else {
            inputState = InputState.UNTOUCHED;
        }
    }
}
