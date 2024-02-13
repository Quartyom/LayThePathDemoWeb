package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.quartyom.LayThePath;

public class Notification {
    public boolean isActive;
    private float centerX, centerY;
    private float notificationX, notificationY, notificationW;
    private float textX, textY, textW, textH;

    public Vector2 offset;

    public String string;

    private LayThePath game;
    private BitmapFont font;

    private TextureRegion texture;


    public Notification(LayThePath game) {
        this.game = game;
        offset = new Vector2();
        texture = game.slidersAtlas.findRegion("regular_notification");
    }

    public void setString(String string) {
        if (string != this.string) {
            this.string = string;
            if (string == null) { return; }

            font = game.fontHolder.get((int) notificationW, FontType.LOCALIZED_WITH_LATIN);    // шрифт для теста
            game.glyphLayout.setText(font, string);

            textW = game.glyphLayout.width;
            textH = game.glyphLayout.height;

            float wRatio = notificationW / (float) Math.sqrt(textW * textW + textH * textH);  // диаметр уведомления / диагональ текста
            if (wRatio > 1) {
                wRatio = 1;
            }

            int fontSize = (int) (notificationW * wRatio * 0.8f);

            if (fontSize > 200) {
                fontSize = 200;
            }

            font = game.fontHolder.get(fontSize, FontType.LOCALIZED_WITH_LATIN); // окончательный шрифт

            game.glyphLayout.setText(font, string);

            textW = game.glyphLayout.width;
            textH = game.glyphLayout.height;

            textX = centerX - textW / 2;
            textY = centerY + textH / 2;
        }
    }

    public void draw() {
        if (string != null) {
            game.batch.draw(texture, notificationX, notificationY, notificationW, notificationW);
            font.draw(game.batch, string, textX + offset.x, textY + offset.y);
        }
    }

    public void resize(float x, float y, float w) {
        notificationW = w;
        centerX = x + notificationW / 4;
        centerY = y - notificationW / 4;
        notificationX = centerX - notificationW / 2;
        notificationY = centerY - notificationW / 2;
    }


}
