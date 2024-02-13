package com.quartyom.screens.Level;

import com.badlogic.gdx.utils.TimeUtils;
import com.quartyom.game_elements.Button;
import com.quartyom.game_elements.GameBottomPanel;
import com.quartyom.interfaces.QuEvent;

public class LevelBottomPanel extends GameBottomPanel {
    boolean isActive = true;

    public final LevelScreen levelScreen;
    Button resetButton, transformButton, hintButton, skipButton;

    public LevelBottomPanel(final LevelScreen levelScreen) {
        super(levelScreen.game);
        this.levelScreen = levelScreen;

        resetButton = new Button("reset", game, new QuEvent() {
            @Override
            public void execute() {
                levelScreen.levelBoard.gameplay.resetBody();
            }
        });
        resetButton.setHint(game.locale.get("reset level")).setSound("click_1");

        transformButton = new Button("transform", game, new QuEvent() {
            @Override
            public void execute() {
                isActive = false;
                levelScreen.levelTransformBottomPanel.isActive = true;
            }
        });
        transformButton.setHint(game.locale.get("transform the field")).setSound("click_1");

        hintButton = new Button("hint", game, new QuEvent() {
            @Override
            public void execute() {
                LevelBoard levelBoard = levelScreen.levelBoard;
                if (levelBoard.boardDrawer.isHintShown) {              // уже показана, выключаем
                    levelBoard.boardDrawer.isHintShown = false;
                    levelBoard.wasHintUsed = true;
                } else if (levelBoard.wasHintUsed) {                     // не показана, но была использована
                    levelBoard.boardDrawer.isHintShown = true;
                } else if (game.userData.hints_amount > 0) {               // значит, если они есть, то уменьшаем на 1
                    game.userData.hints_amount--;
                    levelBoard.boardDrawer.isHintShown = true;
                } else {
                    game.setScreen("level_hint");
                }
            }
        });
        hintButton.setHint(game.locale.get("show hint")).addNotification().setSound("click_1");

        skipButton = new Button("next", game, new QuEvent() {
            @Override
            public void execute() {
                LevelBoard levelBoard = levelScreen.levelBoard;
                if (levelBoard.currentLevel < game.userData.max_level_achieved ||
                        TimeUtils.millis() >= game.userData.when_to_skip_level ||
                        game.userData.premium_is_on) {

                    levelBoard.currentLevel++;
                    levelBoard.userData.current_level = levelBoard.currentLevel;
                    // game.saveUserData();
                    levelBoard.loadLevel();

                    game.setScreen("level");
                } else {
                    game.setScreen("level_skip");
                }
            }
        });
        skipButton.setHint(game.locale.get("skip level")).addNotification().setSound("click_1");
    }

    @Override
    public void resize() {
        super.resize();

        resetButton.resize(firstButtonX, firstButtonY, buttonW, buttonH);
        transformButton.resize(firstButtonX + panelW / 4 * 1, firstButtonY, buttonW, buttonH);
        hintButton.resize(firstButtonX + panelW / 4 * 2, firstButtonY, buttonW, buttonH);
        skipButton.resize(firstButtonX + panelW / 4 * 3, firstButtonY, buttonW, buttonH);
    }

    @Override
    public void draw() {
        if (!isActive) {
            return;
        }
        super.draw();
        resetButton.draw();
        transformButton.draw();
        hintButton.draw();
        skipButton.draw();
    }

    public void update() {
        if (!isActive) {
            return;
        }
        resetButton.update();
        transformButton.update();
        hintButton.update();
        skipButton.update();

        if (game.userData.hints_amount < 1_000) {
            hintButton.setNotification(String.valueOf(game.userData.hints_amount));
        } else {
            hintButton.setNotification("1k+");
        }

        long minutes_left = (game.userData.when_to_skip_level - TimeUtils.millis()) / 60_000L;

        if (levelScreen.levelBoard.currentLevel < game.userData.max_level_achieved || minutes_left < 0 || game.userData.premium_is_on) {
            skipButton.setNotification(null);
        } else {
            skipButton.setNotification("<" + (minutes_left + 1L) + "m");
        }
    }
}
