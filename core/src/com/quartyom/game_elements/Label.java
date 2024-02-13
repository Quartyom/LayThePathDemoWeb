package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;

// Текст размещается внутри заданной рамки, в одну строку, отцентрированный, либо к левому краю
public class Label {
    private float labelX, labelY, labelW, labelH;
    private float textX, textY, textW, textH;
    public Vector2 offset;

    private LayThePath game;
    private BitmapFont font;

    public String string;
    public String targetString;    // для фиксированного размера изменяющегося лэйбла

    public FontType fontType = FontType.LOCALIZED_LIGHT;

    public Label(LayThePath game) {
        this(game, new String());
    }

    public Label(LayThePath game, String string) {
        this.game = game;
        this.string = string;
        offset = new Vector2();
    }

    public void setString(String string) {
        setString(string, Align.center);
    }

    public void setString(String string, int halign) {
        if (string != this.string) {
            this.string = string;
            resize(labelX, labelY, labelW, labelH, halign);
        }
    }

    public void draw() {
        font.draw(game.batch, string, textX + offset.x, textY + offset.y);
    }

    public void resize(float x, float y, float w, float h, int halign) {
        labelX = x;
        labelY = y;
        labelW = w;
        labelH = h;

        font = game.fontHolder.get((int) h, fontType);    // шрифт для теста

        if (targetString == null) {
            game.glyphLayout.setText(font, string);
        } else {
            game.glyphLayout.setText(font, targetString);
        }

        float wRatio = w / game.glyphLayout.width;
        if (wRatio > 1) {
            wRatio = 1;
        }

        int fontSize = (int) (h * wRatio * 0.8f);

        if (fontSize > 200) {
            fontSize = 200;
        }  // чтобы не было проблем при отрисовке (хотя теперь и не должно)

        font = game.fontHolder.get(fontSize, fontType); // окончательный шрифт
        game.glyphLayout.setText(font, string);

        textW = game.glyphLayout.width;
        textH = game.glyphLayout.height;

        if (halign == Align.center) {
            textX = x + w / 2 - textW / 2;
        } else {
            textX = x;
        }
        textY = y + h / 2 + textH / 2;

    }

    public float getLowerY() {
        return labelY - labelH;
    }

}
