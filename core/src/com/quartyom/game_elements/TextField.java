package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;

// Большой текст в несколько строк, заданный шрифт и ширина, возвращается размер объекта
public class TextField {

    private float textX, textY, textW, textH;
    public Vector2 offset;

    private int fontSize;

    private LayThePath game;
    private BitmapFont font;

    public String string;
    public FontType fontType = FontType.LOCALIZED_WITH_LATIN;

    public TextField(LayThePath game, String string) {
        this.game = game;
        this.string = string;
        offset = new Vector2();
    }

    public void resize(float x, float y, float w, int font_size) {
        this.fontSize = font_size;

        textW = w;

        font = game.fontHolder.get(font_size, fontType);
        game.glyphLayout.setText(font, string, Color.WHITE, textW, Align.left, true);
        textH = game.glyphLayout.height;

        textX = x;
        textY = y;
    }

    public float getHeight() {
        return textH;
    }

    public float getLowerY() {
        return textY - textH;
    }

    public void draw() {
        font.draw(game.batch, string, textX + offset.x, textY + offset.y, textW, Align.left, true);
    }

}
