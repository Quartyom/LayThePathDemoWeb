package com.quartyom.game_elements;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.quartyom.LayThePath;
import com.quartyom.screens.Level.MoveResult;

public abstract class GameBoard {
    public final LayThePath game;

    public BoardDrawer boardDrawer;

    protected float boardX, boardY, boardW, boardH;
    protected float squareW, squareH;
    protected float wallOffsetX, wallOffsetY;

    protected float actualSize;

    public Gameplay gameplay;

    protected Scroller scroller;
    protected PressTimer pressTimer;

    protected InputState inputState;
    protected Vector2 touchPos;

    protected Sound moveSound, moveBackSound, bodyShortenedSound, victorySound;

    protected GameBoard(LayThePath game) {
        this.game = game;

        gameplay = new Gameplay();
        boardDrawer = new BoardDrawer(game, gameplay);

        scroller = new Scroller(game);
        pressTimer = new PressTimer(game);

        inputState = InputState.UNTOUCHED;
        touchPos = new Vector2();

        moveSound = game.soundHolder.get("low_put");
        moveBackSound = game.soundHolder.get("low_put_1");
        bodyShortenedSound = game.soundHolder.get("body_shortened");
        victorySound = game.soundHolder.get("victory");
    }

    public void resize(float topPanelHeight) {
        // выбираем наименьшее расстояние (граница экрана слева или панель сверху)
        actualSize = game.HALF_HEIGHT - topPanelHeight;

        scroller.resize(-game.HALF_WIDTH, -actualSize, game.WIDTH, 2 * actualSize);
        pressTimer.resize(-game.HALF_WIDTH, -actualSize, game.WIDTH, 2 * actualSize);

        if (game.HALF_WIDTH < actualSize) {
            actualSize = game.HALF_WIDTH;
        }

        boardX = -actualSize;
        boardY = -actualSize;
        boardW = actualSize * 2;
        boardH = actualSize * 2;

        squareW = boardW / gameplay.field_size;
        squareH = boardH / gameplay.field_size;

        // толщина стены 2 / 8 px
        wallOffsetX = squareW / 16.0f;
        wallOffsetY = squareH / 16.0f;

        boardDrawer.boardX = boardX;
        boardDrawer.boardY = boardY;
        boardDrawer.boardW = boardW;
        boardDrawer.boardH = boardH;

        boardDrawer.squareW = squareW;
        boardDrawer.squareH = squareH;

        boardDrawer.wallOffsetX = wallOffsetX;
        boardDrawer.wallOffsetY = wallOffsetY;
    }

    public void show() {
        gameplay.headIsCaptured = false;
    }

    public void update() {
        if (game.userData.abstract_input_is_on) {
            abstractUpdate();
        } else {
            touchUpdate();
        }
    }

    public abstract void victoryAction();

    public void touchUpdate() {
        if (game.isTouched()) {
            touchPos.x = game.touchPos.x;
            touchPos.y = game.touchPos.y;

            if (touchPos.x > boardX && touchPos.y > boardY && touchPos.x < boardX + boardW && touchPos.y < boardY + boardH) {
                inputState = InputState.TOUCHED;
                touchPos.x = (touchPos.x - boardX) / squareW;
                touchPos.y = (touchPos.y - boardY) / squareH;

                MoveResult result;
                // если нажатие произошло только что, нужно захватить голову
                if (game.inputState == InputState.JUST_TOUCHED) {
                    result = gameplay.justTouchedMakeMove((int) touchPos.x, (int) touchPos.y);
                } else {
                    result = gameplay.touchedMakeMove((int) touchPos.x, (int) touchPos.y);
                }

                switch (result) {
                    // нейтральные исходы
                    case NO_MOVEMENT:
                    case HEAD_IS_NOT_CAPTURED:
                    case OTHER_GOOD:
                        break;
                    // хорошие исходы
                    case HEAD_IS_SET:
                    case BODY_VISITED:
                    case SIMPLE_MOVEMENT:
                    case HEAD_IS_DESTROYED:
                    case VICTORY:
                    case BODY_IS_SHORTENED:
                        gameplay.falsePath.clear();
                        moveSound.play(game.userData.volume);
                        break;
                    case MOVE_BACK:
                        gameplay.falsePath.clear();
                        moveBackSound.play(game.userData.volume);
                        break;
                    // плохие исходы
                    case HEAD_IS_NOT_SET:
                    case MOVE_INTO_BOX:
                    case NOT_A_NEIGHBOR:
                    case MOVE_THROUGH_SLASH_WALL:
                    case MOVE_THROUGH_BACKSLASH_WALL:
                    case MOVE_THROUGH_POINT:
                    case MOVE_THROUGH_CROSSROAD:
                    case MOVE_THROUGH_VERTICAL_WALL:
                    case MOVE_THROUGH_HORIZONTAL_WALL:
                    case BODY_NOT_VISITED:
                    case OTHER_BAD:
                        if (gameplay.falsePath.isEmpty()) {
                        }
                        gameplay.falsePath.add(new Vector2((int) touchPos.x, (int) touchPos.y));
                        break;
                    case OUT_OF_BOUNDS:
                        if (gameplay.falsePath.isEmpty()) {
                        }
                        break;
                }
            }
        }
        // палец только убрали, НО палец до этого касался доски
        else if (inputState == InputState.TOUCHED) {
            inputState = InputState.UNTOUCHED;

            MoveResult result = gameplay.justUntouchedMakeMove((int) touchPos.x, (int) touchPos.y);
            if (result == MoveResult.VICTORY) {
                victorySound.play(game.userData.volume);   // потому что действие при победе более ресурсозатратное
                victoryAction();
            } else if (result == MoveResult.BODY_IS_SHORTENED) {
                gameplay.falsePath.clear();
                bodyShortenedSound.play(game.userData.volume);
            }
        }

    }

    public void abstractUpdate() {
        scroller.update();
        pressTimer.update();

        float sensitivity = actualSize * 2 / 5;
        Vector2 abstractInputCursor = gameplay.abstractInputCursor;

        if (pressTimer.handle_double_tap()) {
            pressTimer.reset();
            MoveResult result = gameplay.doubleTapMakeMove();
            if (result == MoveResult.HEAD_IS_NOT_SET) {
                if (gameplay.falsePath.isEmpty()) {
                }
                gameplay.falsePath.add(new Vector2((int) abstractInputCursor.x, (int) abstractInputCursor.y));
            } else if (result == MoveResult.BODY_IS_SHORTENED) {
                gameplay.falsePath.clear();
                bodyShortenedSound.play(game.userData.volume);
            }
            scroller.inputState = InputState.UNTOUCHED; // чтобы не было одновременно слайда и тапа
            return;
        }

        if (scroller.inputState == InputState.JUST_TOUCHED) {
            scroller.value.x = abstractInputCursor.x * sensitivity;
            scroller.value.y = abstractInputCursor.y * sensitivity;
        } else if (scroller.inputState == InputState.TOUCHED) {

            if (scroller.value.x < 0) {
                scroller.value.x = 0;
            } else if (Math.round(scroller.value.x / sensitivity) >= gameplay.field_size) {
                scroller.value.x = (gameplay.field_size - 1) * sensitivity;
            }

            if (scroller.value.y < 0) {
                scroller.value.y = 0;
            } else if (Math.round(scroller.value.y / sensitivity) >= gameplay.field_size) {
                scroller.value.y = (gameplay.field_size - 1) * sensitivity;
            }

            // выравнивание вдоль осей
            if (abstractInputCursor.x != Math.round(scroller.value.x / sensitivity)) {
                abstractInputCursor.x = Math.round(scroller.value.x / sensitivity);
                scroller.value.y = abstractInputCursor.y * sensitivity;
            } else if (abstractInputCursor.y != Math.round(scroller.value.y / sensitivity)) {
                scroller.value.x = abstractInputCursor.x * sensitivity;
                abstractInputCursor.y = Math.round(scroller.value.y / sensitivity);
            }

            MoveResult result = gameplay.slideTouchedMakeMove();

            switch (result) {
                // нейтральные исходы
                case NO_MOVEMENT:
                case HEAD_IS_NOT_CAPTURED:
                case OTHER_GOOD:
                    gameplay.falsePath.clear();
                    break;
                // хорошие исходы
                case HEAD_IS_SET:
                case BODY_VISITED:
                case SIMPLE_MOVEMENT:
                case HEAD_IS_DESTROYED:
                case VICTORY:
                case BODY_IS_SHORTENED:
                    gameplay.falsePath.clear();
                    moveSound.play(game.userData.volume);
                    break;
                case MOVE_BACK:
                    gameplay.falsePath.clear();
                    moveBackSound.play(game.userData.volume);
                    break;
                // плохие исходы
                case HEAD_IS_NOT_SET:
                case MOVE_INTO_BOX:
                case NOT_A_NEIGHBOR:
                case MOVE_THROUGH_SLASH_WALL:
                case MOVE_THROUGH_BACKSLASH_WALL:
                case MOVE_THROUGH_POINT:
                case MOVE_THROUGH_CROSSROAD:
                case MOVE_THROUGH_VERTICAL_WALL:
                case MOVE_THROUGH_HORIZONTAL_WALL:
                case BODY_NOT_VISITED:
                case OTHER_BAD:
                    if (gameplay.falsePath.isEmpty()) {
                    }
                    gameplay.falsePath.add(new Vector2((int) abstractInputCursor.x, (int) abstractInputCursor.y));
                    break;
                case OUT_OF_BOUNDS:
                    if (gameplay.falsePath.isEmpty()) {
                    }
                    break;
            }
        } else if (scroller.inputState == InputState.JUST_UNTOUCHED) {
            MoveResult result = gameplay.slideJustUntouchedMakeMove();
            if (result == MoveResult.VICTORY) {
                victorySound.play(game.userData.volume);   // потому что действие при победе более ресурсозатратное
                victoryAction();
            } else if (result == MoveResult.BODY_IS_SHORTENED) {
                gameplay.falsePath.clear();
                bodyShortenedSound.play(game.userData.volume);
            }

        }
    }
}
