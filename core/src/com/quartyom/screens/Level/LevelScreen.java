package com.quartyom.screens.Level;

import com.badlogic.gdx.Gdx;
import com.quartyom.LayThePath;
import com.quartyom.game_elements.AttentionScreenWithBackButton;
import com.quartyom.game_elements.QuScreen;

public class LevelScreen extends QuScreen {
    final LayThePath game;

    LevelTopPanel levelTopPanel;
    LevelBottomPanel levelBottomPanel;
    LevelTransformBottomPanel levelTransformBottomPanel;
    LevelBoard levelBoard;

    public LevelScreen(final LayThePath game) {
        this.game = game;

        game.add("level_skip", new AttentionScreenWithBackButton(game,
                "skip_level", "level"));
        game.add("level_hint", new AttentionScreenWithBackButton(game,
                "hints_are_over", "level"));
        game.add("levels_are_over", new LevelsAreOver(this));

        levelTopPanel = new LevelTopPanel(this);
        levelBottomPanel = new LevelBottomPanel(this);
        levelTransformBottomPanel = new LevelTransformBottomPanel(this);
        levelBoard = new LevelBoard(this);

    }

    @Override
    public void show() {
        Gdx.gl20.glClearColor(0.25f, 0.25f, 0.25f, 1);
        levelBoard.show();

        if (levelBoard.currentLevel > levelBoard.howManyLevels) {
            game.setScreen("levels_are_over");
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);

        levelBoard.draw();
        levelTopPanel.draw();
        levelBottomPanel.draw();
        levelTransformBottomPanel.draw();

        levelBoard.update();
        levelTopPanel.update();
        levelBottomPanel.update();
        levelTransformBottomPanel.update();

        if (game.isBackButtonPressed) {
            game.isBackButtonPressed = false;
            game.setScreen("menu_classic");
        }

    }

    @Override
    public void resize(int width, int height) {
        levelTopPanel.resize();
        levelBottomPanel.resize();
        levelTransformBottomPanel.resize();
        levelBoard.resize();
    }

}
