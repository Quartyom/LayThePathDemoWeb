package com.quartyom.screens.Level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.TimeUtils;
import com.quartyom.UserData;
import com.quartyom.game_elements.GameBoard;

public class LevelBoard extends GameBoard {
    public final LevelScreen levelScreen;

    LevelsData levelsData;
    UserData userData;

    int howManyLevels, currentLevel;
    boolean wasHintUsed;

    public LevelBoard(LevelScreen levelScreen) {
        super(levelScreen.game);

        this.levelScreen = levelScreen;

        levelsData = new LevelsData();
        howManyLevels = levelsData.how_many_levels;

        userData = game.userData;
        currentLevel = userData.current_level;

        loadLevel();

    }

    public void resize() {
        super.resize(levelScreen.levelTopPanel.getHeight());
    }

    void draw() {
        boardDrawer.draw();
    }

    @Override
    public void victoryAction() {
        currentLevel++;
        userData.current_level = currentLevel;
        // game.saveUserData();
        game.sendStat();
        loadLevel();   // осторожно, уровень может не загрузиться
    }

    public void loadLevel() {
        // System.out.println("Loaded level " + currentLevel);

        if (currentLevel > userData.max_level_achieved) {
            if (game.random.nextInt(3) == 0) {
                userData.hints_amount++;
            }
            userData.max_level_achieved = currentLevel;
            // game.saveUserData();
        }

        if (currentLevel > howManyLevels) {
            game.setScreen("levels_are_over");
            return;
        }

        // String a = Gdx.files.internal("levels/" + currentLevel + ".json").readString();
        // LevelConfiguration levelConfiguration = game.json.fromJson(LevelConfiguration.class, a);
        LevelConfiguration levelConfiguration = LevelsHolder.get(currentLevel);
        gameplay.setLevelConfiguration(levelConfiguration);

        userData.when_to_skip_level = TimeUtils.millis() + (levelConfiguration.field_size - 2) * 60_000L - 1L;    // now + N minutes
        // game.saveUserData();

        resize(); // чтобы если размер уровня другой, адаптировать экран
        gameplay.normalizeCursor(); // чтобы курсор не выпрыгнул за поле
        boardDrawer.isHintShown = false;
        wasHintUsed = false;
    }

}
