package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.QuEvent;

public class GameTopPanel {
    public final LayThePath game;

    protected TextureRegion texture;

    protected float panelX, panelY, panelW, panelH;
    protected float firstButtonX, firstButtonY, buttonW, buttonH;

    protected Button menuButton;

    protected GameTopPanel(final LayThePath game, final String menu_screen) {
        this.game = game;
        texture = game.fieldAtlas.findRegion("top_panel");
        menuButton = new Button("menu", game, new QuEvent() {
            @Override
            public void execute() {
                game.setScreen(menu_screen);
            }
        });
    }

    protected GameTopPanel(final LayThePath game) {
        this(game, "menu");
    }

    public void resize() {
        panelX = -game.HALF_WIDTH;
        panelH = (0.5f / 4) * game.HEIGHT;
        panelY = game.HALF_HEIGHT - panelH;
        panelW = game.WIDTH;

        float buttonActualSize = panelH / 2;

        float firstButtonCenterX = panelX + panelW - buttonActualSize;
        float firstButtonCenterY = panelY + panelH / 2;

        buttonActualSize *= 0.9f; // Отступ кнопки от краёв

        firstButtonX = firstButtonCenterX - buttonActualSize;
        firstButtonY = firstButtonCenterY - buttonActualSize;

        buttonW = buttonActualSize * 2;
        buttonH = buttonActualSize * 2;

        menuButton.resize(firstButtonX, firstButtonY, buttonW, buttonH);
    }

    public void draw() {
        game.batch.draw(texture, panelX, panelY, panelW, panelH);
        menuButton.draw();
    }

    public void update() {
        menuButton.update();
    }

    public float getHeight() {
        return panelH;
    }
}
