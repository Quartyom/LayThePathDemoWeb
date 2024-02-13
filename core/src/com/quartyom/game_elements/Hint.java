package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.Drawable;

// arbitrary text, max size: half screen width, half screen height, hint position depends on touch position
public class Hint implements Drawable {

    private final Button parentButton;
    private float hintX, hintY, hintW, hintH;
    private float hintHalfW, hintHalfH;
    private float textX, textY, textW, textH;
    private float textHalfW, textHalfH;

    private float marginX, marginY;

    public final LayThePath game;
    private BitmapFont font;

    private final TextureRegion texture;

    private String string;

    Vector2 touchPos;

    Hint(Button parentButton) {
        this.parentButton = parentButton;
        this.game = parentButton.game;
        touchPos = new Vector2();
        texture = game.fieldAtlas.findRegion("light_square");
    }

    public void setString(String string) {
        this.string = string;
    }

    public void resize(int fontSize, float marginX, float marginY) {
        font = game.fontHolder.get(fontSize, FontType.LOCALIZED_WITH_LATIN);

        game.glyphLayout.setText(font, string, Color.WHITE, game.HALF_WIDTH, Align.left, true);

        textW = game.glyphLayout.width;
        textH = game.glyphLayout.height;
        textHalfW = textW / 2;
        textHalfH = textH / 2;

        this.marginX = marginX;
        this.marginY = marginY;

        hintW = textW + fontSize;
        hintH = textH + fontSize;
        hintHalfW = hintW / 2;
        hintHalfH = hintH / 2;
    }

    public void draw() {
        if (parentButton.inputState == InputState.UNTOUCHED) {
            return;
        }

        // do hint not to go out of screen borders
        if (touchPos.x - hintHalfW < -game.HALF_WIDTH) {    // horizontal
            touchPos.x = -game.HALF_WIDTH + hintHalfW;
        } else if (touchPos.x + hintHalfW > game.HALF_WIDTH) {
            touchPos.x = game.HALF_WIDTH - hintHalfW;
        }

        if (touchPos.y - hintHalfH < -game.HALF_HEIGHT) {      // vertical
            touchPos.y = -game.HALF_HEIGHT + hintHalfH;
        } else if (touchPos.y + hintHalfH > game.HALF_HEIGHT) {
            touchPos.y = game.HALF_HEIGHT - hintHalfH;
        }

        hintX = touchPos.x - hintHalfW;     // position equals center - half size
        hintY = touchPos.y - hintHalfH;

        textX = touchPos.x - textHalfW;
        textY = touchPos.y + textHalfH;

        game.batch.draw(texture, hintX, hintY, hintW, hintH);
        font.draw(game.batch, string, textX, textY, textW, Align.left, true);

    }

    public void update() {
        if (parentButton.inputState == InputState.UNTOUCHED) {
            return;
        }

        if (game.isTouched) {
            touchPos.x = game.touchPos.x + marginX;
            touchPos.y = game.touchPos.y + marginY;
        }
    }

}
