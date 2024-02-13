package com.quartyom.screens.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Align;
import com.quartyom.LayThePath;
import com.quartyom.game_elements.Button;
import com.quartyom.game_elements.Label;
import com.quartyom.game_elements.QuScreen;
import com.quartyom.game_elements.TextField;
import com.quartyom.interfaces.QuEvent;

public class LevelsAreOver extends QuScreen {
    final LayThePath game;
    public LevelScreen levelScreen;

    Label hintLabel;
    TextField infoField;
    Button startOverButton;

    public LevelsAreOver(final LevelScreen levelScreen) {
        this.levelScreen = levelScreen;
        game = levelScreen.game;

        hintLabel = new Label(game, levelScreen.game.locale.get("Attention"));

        infoField = new TextField(levelScreen.game, Gdx.files.internal("texts/" + game.userData.locale + "/levels_are_over.txt").readString());

        startOverButton = new Button("in_main_menu", game, new QuEvent() {
            @Override
            public void execute() {
                levelScreen.levelBoard.currentLevel = 1;
                game.userData.current_level = 1;
                // game.saveUserData();
                levelScreen.levelBoard.loadLevel();

                game.setScreen("level");
            }
        });
        startOverButton.setNinePatch(6).setLabel(levelScreen.game.locale.get("Start over"));

    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        hintLabel.draw();
        infoField.draw();
        startOverButton.draw();

        startOverButton.update();

        if (game.isBackButtonPressed) {
            game.isBackButtonPressed = false;
            game.setScreen("menu_classic");
        }
    }

    @Override
    public void resize(int width, int height) {
        hintLabel.resize(game.upperButtonCornerX, game.upperButtonCornerY, game.buttonW, game.buttonH, Align.center);

        int font_size = (int) (levelScreen.game.HEIGHT * (1.0f / 32.0f));
        infoField.resize(game.upperButtonCornerX, game.upperButtonCornerY - game.downMargin + game.buttonH, game.buttonW, font_size);
        // gap
        startOverButton.resize(game.upperButtonCornerX, game.upperButtonCornerY - game.downMargin * 5, game.buttonW, game.buttonH);
    }

}
