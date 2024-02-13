package com.quartyom.screens.Level;

import com.badlogic.gdx.utils.Align;
import com.quartyom.game_elements.GameTopPanel;
import com.quartyom.game_elements.Gameplay;
import com.quartyom.game_elements.InputState;
import com.quartyom.game_elements.Label;
import com.quartyom.game_elements.Scroller;

public class LevelTopPanel extends GameTopPanel {
    public final LevelScreen levelScreen;
    InputState inputState;

    Label levelLabel, progressLabel;
    Scroller scroller;

    String levelString, progressString;

    public LevelTopPanel(final LevelScreen levelScreen) {
        super(levelScreen.game, "menu_classic");
        this.levelScreen = levelScreen;

        inputState = InputState.UNTOUCHED;

        levelLabel = new Label(game);
        levelString = game.locale.get("Level ");
        progressString = game.locale.get("Progress ");
        levelLabel.targetString = levelString + "1000";
        progressLabel = new Label(game);
        progressLabel.targetString = progressString + "100 / 100";

        scroller = new Scroller(game);
    }

    @Override
    public void resize() {
        super.resize();

        scroller.resize(panelX + panelW * 0.025f, panelY, panelW * 0.75f, panelH * 0.75f);
        levelLabel.resize(panelX + panelW * 0.025f, panelY + panelH / 2, panelW * 0.75f, panelH / 2, Align.left);
        progressLabel.resize(panelX + panelW * 0.025f, panelY, panelW * 0.75f, panelH / 2, Align.left);
    }

    @Override
    public void draw() {
        super.draw();

        levelLabel.string = levelString + levelScreen.levelBoard.currentLevel;
        levelLabel.draw();

        Gameplay gameplay = levelScreen.levelBoard.gameplay; // просто для сокращения пути
        progressLabel.string = progressString + gameplay.howManyVisited + " / " + gameplay.howManyShouldBeVisited;
        progressLabel.draw();
    }

    @Override
    public void update() {
        super.update();

        scroller.update();

        LevelBoard levelBoard = levelScreen.levelBoard; // для сокращения кода снизу
        float sensitivity = 50; // количество пикселей, которые нужно пройти, чтобы переключить 1 уровень

        if (scroller.inputState == InputState.JUST_TOUCHED) {
            scroller.value.x = levelBoard.currentLevel * sensitivity;
        } else if (scroller.inputState == InputState.TOUCHED) {
            if ((Math.round(scroller.value.x / sensitivity)) != levelBoard.currentLevel) {
                levelBoard.currentLevel = (Math.round(scroller.value.x / sensitivity));

                boolean to_change_scroller = true;

                if (levelBoard.currentLevel < 1) {
                    levelBoard.currentLevel += game.userData.max_level_achieved;
                    if (levelBoard.currentLevel > levelBoard.howManyLevels) {
                        levelBoard.currentLevel = levelBoard.howManyLevels;
                    }
                } else if (levelBoard.currentLevel > levelBoard.howManyLevels) {
                    levelBoard.currentLevel = 1;
                } else if (levelBoard.currentLevel > game.userData.max_level_achieved) {
                    levelBoard.currentLevel -= game.userData.max_level_achieved;
                } else {
                    to_change_scroller = false;
                }
                if (to_change_scroller) {
                    scroller.value.x = levelBoard.currentLevel * sensitivity;
                }

                levelBoard.userData.current_level = levelBoard.currentLevel;
                // game.saveUserData();
                levelBoard.loadLevel();
            }
        }
    }

}
