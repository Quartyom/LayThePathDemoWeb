package com.quartyom.game_elements;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.quartyom.LayThePath;

import java.util.ArrayList;

// отрисовывает игровое поле
public class BoardDrawer {
    private LayThePath game;
    private Gameplay gameplay;

    // ИХ БУДЕТ ИЗМЕНЯТЬ ВЫСШИЙ КЛАСС
    public float boardX, boardY, boardW, boardH;
    public float squareW, squareH;
    public float wallOffsetX, wallOffsetY;

    public boolean isHintShown = false;
    public boolean isAbstractCursorVisible = true;

    public BoardDrawer(LayThePath game, Gameplay gameplay) {
        this.game = game;
        this.gameplay = gameplay;
    }

    public void draw() {
        // красим задник
        for (int i = 0; i < gameplay.body.size(); i++) {
            game.batch.draw(game.fieldAtlas.findRegion("light_square"),
                    boardX + gameplay.body.get(i).x * squareW,
                    boardY + gameplay.body.get(i).y * squareH,
                    squareW,
                    squareH
            );

        }
        if (gameplay.body.size() > 0 && gameplay.headIsCaptured) {
            game.batch.draw(game.fieldAtlas.findRegion("swamp_square"),
                    boardX + gameplay.body.get(gameplay.body.size() - 1).x * squareW,
                    boardY + gameplay.body.get(gameplay.body.size() - 1).y * squareH,
                    squareW,
                    squareH
            );
        }

        for (Vector2 item : gameplay.boxes) {
            game.batch.draw(game.fieldAtlas.findRegion("light_square"),
                    boardX + item.x * squareW,
                    boardY + item.y * squareH,
                    squareW,
                    squareH
            );
        }

        // hint
        if (isHintShown) {
            for (Vector2 item : gameplay.hint) {
                game.batch.draw(game.fieldAtlas.findRegion("bottom_panel"),
                        boardX + item.x * squareW,
                        boardY + item.y * squareH,
                        squareW,
                        squareH
                );
            }
        }

        // false path
        if (!game.userData.abstract_input_is_on) {
            for (Vector2 item : gameplay.falsePath) {
                game.batch.draw(game.fieldAtlas.findRegion("red_square"),
                        boardX + item.x * squareW,
                        boardY + item.y * squareH,
                        squareW,
                        squareH
                );
            }
        }

        // рисуем сетку
        for (int y = 0; y < gameplay.field_size; y++) {
            for (int x = 0; x < gameplay.field_size; x++) {
                game.batch.draw(game.fieldAtlas.findRegion("full_square"),
                        boardX + x * squareW,
                        boardY + y * squareH,
                        squareW,
                        squareH
                );
            }
        }

        //вертикальные стенки
        for (Vector2 item : gameplay.vertical_walls) {
            game.batch.draw(game.fieldAtlas.findRegion("vertical_wall"),
                    boardX + wallOffsetX + item.x * squareW,
                    boardY + item.y * squareH,
                    squareW,
                    squareH
            );
        }

        // горизонтальные стенки
        for (Vector2 item : gameplay.horizontal_walls) {
            game.batch.draw(game.fieldAtlas.findRegion("horizontal_wall"),
                    boardX + item.x * squareW,
                    boardY + wallOffsetY + item.y * squareH,
                    squareW,
                    squareH
            );
        }

        // препятствия внутри клеток
        drawObstacleInSquare(gameplay.slash_walls, game.fieldAtlas.findRegion("slash_wall"));
        drawObstacleInSquare(gameplay.backslash_walls, game.fieldAtlas.findRegion("backslash_wall"));
        drawObstacleInSquare(gameplay.boxes, game.fieldAtlas.findRegion("box"));
        drawObstacleInSquare(gameplay.points, game.fieldAtlas.findRegion("point"));
        drawObstacleInSquare(gameplay.crossroads, game.fieldAtlas.findRegion("crossroad"));

        // внешнее обрамление
        game.batch.draw(game.fieldAtlas.findRegion("full_square"),
                boardX,
                boardY,
                boardW,
                boardH
        );

        drawBody();

        // абстрактный курсор
        if (game.userData.abstract_input_is_on && isAbstractCursorVisible) {
            // небольшой костыль, чтобы не модифицировать игровую логику
            // если false_path не пуст, значит был совершён неправильный ход
            if (gameplay.falsePath.isEmpty()) {
                game.batch.draw(game.fieldAtlas.findRegion("cursor_white"),
                        boardX + gameplay.abstractInputCursor.x * squareW,
                        boardY + gameplay.abstractInputCursor.y * squareH,
                        squareW,
                        squareH
                );
            } else {
                game.batch.draw(game.fieldAtlas.findRegion("cursor_red"),
                        boardX + gameplay.abstractInputCursor.x * squareW,
                        boardY + gameplay.abstractInputCursor.y * squareH,
                        squareW,
                        squareH
                );
            }
        }
    }

    private void drawObstacleInSquare(ArrayList<Vector2> list, TextureRegion texture) {
        for (Vector2 item : list) {
            game.batch.draw(texture,
                    boardX + item.x * squareW,
                    boardY + item.y * squareH,
                    squareW,
                    squareH
            );
        }
    }

    private void drawBody() {
        if (gameplay.body.size() == 0) {
        } else if (gameplay.body.size() == 1) {
            game.batch.draw(game.fieldAtlas.findRegion("body", 0),
                    boardX + gameplay.body.get(0).x * squareW,
                    boardY + gameplay.body.get(0).y * squareH,
                    squareW,
                    squareH
            );
        } else {
            int head_segment = Gameplay.SEGMENT_BY_IO[(int) gameplay.body_io.get(0).y][(int) gameplay.body_io.get(0).y];
            game.batch.draw(game.fieldAtlas.findRegion("body", head_segment),
                    boardX + gameplay.body.get(0).x * squareW,
                    boardY + gameplay.body.get(0).y * squareH,
                    squareW,
                    squareH
            );
            int tail_segment = Gameplay.SEGMENT_BY_IO[(int) gameplay.body_io.get(gameplay.body_io.size() - 1).x][(int) gameplay.body_io.get(gameplay.body_io.size() - 1).x];
            if (gameplay.headIsCaptured) {
                tail_segment += 4;
            }
            game.batch.draw(game.fieldAtlas.findRegion("body", tail_segment),
                    boardX + gameplay.body.get(gameplay.body.size() - 1).x * squareW,
                    boardY + gameplay.body.get(gameplay.body.size() - 1).y * squareH,
                    squareW,
                    squareH
            );

            for (int i = 1; i < (gameplay.body.size() - 1); i++) {
                int body_segment = Gameplay.SEGMENT_BY_IO[(int) gameplay.body_io.get(i).x][(int) gameplay.body_io.get(i).y];
                game.batch.draw(game.fieldAtlas.findRegion("body", body_segment),
                        boardX + gameplay.body.get(i).x * squareW,
                        boardY + gameplay.body.get(i).y * squareH,
                        squareW,
                        squareH
                );
            }
        }
    }

}
