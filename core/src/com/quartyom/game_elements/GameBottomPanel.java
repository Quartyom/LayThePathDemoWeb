package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.quartyom.LayThePath;

public class GameBottomPanel {
    protected final LayThePath game;

    protected TextureRegion texture;

    protected float panelX, panelY, panelW, panelH;
    protected float firstButtonX, firstButtonY, buttonW, buttonH;

    protected GameBottomPanel(final LayThePath game) {
        this.game = game;

        texture = game.fieldAtlas.findRegion("bottom_panel");
    }

    public void resize() {
        panelX = -game.HALF_WIDTH;
        panelY = -game.HALF_HEIGHT;
        panelW = game.WIDTH;
        panelH = (0.5f / 4) * game.HEIGHT;

        float firstButtonCenterX = panelX + panelW / 2 / 4; // тк 4 кнопки
        float firstButtonCenterY = panelY + panelH / 2;

        float buttonActualSize = panelH / 2;
        //System.out.println(button_actual_size);
        if (panelW / 2 / 4 < buttonActualSize) {
            buttonActualSize = panelW / 2 / 4;
        }
        buttonActualSize *= 0.9f; // Отступ кнопки от краёв

        firstButtonX = firstButtonCenterX - buttonActualSize;
        firstButtonY = firstButtonCenterY - buttonActualSize;

        buttonW = buttonActualSize * 2;
        buttonH = buttonActualSize * 2;
    }

    public void draw() {
        game.batch.draw(texture, panelX, panelY, panelW, panelH);
    }
}
