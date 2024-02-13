package com.quartyom.game_elements;

import com.badlogic.gdx.math.Vector2;
import com.quartyom.screens.Level.LevelConfiguration;
import com.quartyom.screens.Level.MoveResult;

import java.util.ArrayList;
import java.util.Collections;

// реализует чистую игровую логику
public class Gameplay {
    public ArrayList<Vector2> vertical_walls, horizontal_walls, slash_walls, backslash_walls, boxes, points, crossroads;

    public ArrayList<Vector2> body, body_io;
    public ArrayList<Vector2> falsePath;
    public ArrayList<Vector2> hint;

    public Vector2 abstractInputCursor;

    public boolean headIsCaptured;    // держит ли палец на голове
    // когда только поставили голову - false, при ходе назад - false, если держит палец на голове или хвосте - true
    public boolean isTendingToDestroyTheHead;


    public int howManyVisited;
    public int howManyShouldBeVisited;

    public int field_size;

    public Gameplay() {
        body = new ArrayList<>();
        body_io = new ArrayList<>();
        falsePath = new ArrayList<>();

        abstractInputCursor = new Vector2();
    }

    public void setLevelConfiguration(LevelConfiguration levelConfiguration) {
        vertical_walls = levelConfiguration.vertical_walls;
        horizontal_walls = levelConfiguration.horizontal_walls;
        slash_walls = levelConfiguration.slash_walls;
        backslash_walls = levelConfiguration.backslash_walls;
        boxes = levelConfiguration.boxes;
        points = levelConfiguration.points;
        crossroads = levelConfiguration.crossroads;
        hint = levelConfiguration.hint;
        field_size = levelConfiguration.field_size;

        resetBody();
        howManyShouldBeVisited = field_size * field_size - boxes.size();
        isTendingToDestroyTheHead = false;
    }

    public void setHint() {
        if (body != null && body.size() > 0) {
            if (hint == null) {
                hint = new ArrayList<>();
            }
            if (!hint.isEmpty()) {
                hint.clear();
            }
            hint.add(body.get(0));
            hint.add(body.get(body.size() - 1));
        }
    }

    public LevelConfiguration getLevelConfiguration() {
        LevelConfiguration levelConfiguration = new LevelConfiguration();
        levelConfiguration.vertical_walls = vertical_walls;
        levelConfiguration.horizontal_walls = horizontal_walls;
        levelConfiguration.slash_walls = slash_walls;
        levelConfiguration.backslash_walls = backslash_walls;
        levelConfiguration.boxes = boxes;
        levelConfiguration.points = points;
        levelConfiguration.crossroads = crossroads;
        levelConfiguration.hint = hint;
        levelConfiguration.field_size = field_size;

        return levelConfiguration;
    }

    // сегменты dot dot_n... tail_n ... vertical, horizontal, turn ne, turn es...
    public static final int SEGMENT_BY_IO[][] = {
            {1, 11, 9, 14},
            {11, 2, 12, 10},
            {9, 12, 3, 13},
            {14, 10, 13, 4}
    };

    // [move_direction][visited_segment]
    private static final int ENTRANCE_TO_BODY[][] = {
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1}
    };

    // [input][output] = ответ на вопрос можно ли так походить
    private static final int SLASH_IO[][] = {
            {0, 0, 0, 1},
            {0, 0, 1, 0},
            {0, 1, 0, 0},
            {1, 0, 0, 0}
    };
    private static final int BACKSLASH_IO[][] = {
            {0, 1, 0, 0},
            {1, 0, 0, 0},
            {0, 0, 0, 1},
            {0, 0, 1, 0}
    };
    private static final int POINT_IO[][] = {
            {0, 1, 0, 1},
            {1, 0, 1, 0},
            {0, 1, 0, 1},
            {1, 0, 1, 0}
    };
    private static final int CROSSROAD_IO[][] = {
            {0, 0, 1, 0},
            {0, 0, 0, 1},
            {1, 0, 0, 0},
            {0, 1, 0, 0}
    };

    private static final int NEGATE_DIRECTION[] = {2, 3, 0, 1};

    private static int directionTo(int xFrom, int yFrom, int xTo, int yTo) {

        int xDiff = xTo - xFrom;
        int yDiff = yTo - yFrom;

        if (xDiff == 0 && yDiff == 1) {
            return 0;
        } else if (xDiff == 1 && yDiff == 0) {
            return 1;
        } else if (xDiff == 0 && yDiff == -1) {
            return 2;
        } else if (xDiff == -1 && yDiff == 0) {
            return 3;
        } else {
            return -1;
        }
    }

    boolean canStayHere(Vector2 xy_to) {
        return !slash_walls.contains(xy_to) && !backslash_walls.contains(xy_to) && !boxes.contains(xy_to) && !points.contains(xy_to) && !crossroads.contains(xy_to) && body.indexOf(xy_to) == (body.size() - 1);
    }

    public void updateHowManyVisited() {

        howManyVisited = 0;

        for (int x = 0; x < field_size; x++) {
            for (int y = 0; y < field_size; y++) {
                if (body.contains(new Vector2(x, y))) {
                    howManyVisited++;
                }
            }
        }
    }

    private void cutTail() {
        body.remove(body.size() - 1);
        body_io.remove(body_io.size() - 1);
        body_io.get(body_io.size() - 1).y = -1;

        updateHowManyVisited();
    }

    public void resetBody() {
        howManyVisited = 0;
        body.clear();
        body_io.clear();
        falsePath.clear();
        headIsCaptured = false; /// new one
    }

    public void normalizeCursor() {
        if (abstractInputCursor.x < 0) {
            abstractInputCursor.x = 0;
        } else if (abstractInputCursor.x >= field_size) {
            abstractInputCursor.x = field_size - 1;
        }
        if (abstractInputCursor.y < 0) {
            abstractInputCursor.y = 0;
        } else if (abstractInputCursor.y >= field_size) {
            abstractInputCursor.y = field_size - 1;
        }
    }

    private void normalizeObstacle(ArrayList<Vector2> arr) {
        if (field_size <= 1) {
            arr.clear();
        } else {
            ArrayList<Vector2> itemsToDelete = new ArrayList<>();
            for (Vector2 item : arr) {
                if (item.x < 0 || item.y < 0 || item.x >= field_size || item.y >= field_size) {
                    itemsToDelete.add(item);
                }
            }
            arr.removeAll(itemsToDelete);
        }
    }

    public void normalizeObstacles() {
        normalizeObstacle(vertical_walls);
        normalizeObstacle(horizontal_walls);
        normalizeObstacle(slash_walls);
        normalizeObstacle(backslash_walls);
        normalizeObstacle(boxes);
        normalizeObstacle(points);
        normalizeObstacle(crossroads);
    }

    public MoveResult doubleTapMakeMove() {
        Vector2 xy_to = new Vector2(abstractInputCursor);

        // либо тела нет, тогда нужно поставить голову
        if (body.size() == 0) {
            // можно ли ставить
            if (canStayHere(xy_to)) {
                body.add(xy_to);
                body_io.add(new Vector2(-1, -1));
                //System.out.println("Head is set on " + xy_to);
                howManyVisited = 1;
                headIsCaptured = true;
                isTendingToDestroyTheHead = false;
                return MoveResult.HEAD_IS_SET;
            } else {
                return MoveResult.HEAD_IS_NOT_SET;
            }
        }
        // если тело есть
        else {
            Vector2 tail = body.get(body.size() - 1);
            //System.out.println("Tail: " + tail);

            // курсор уже на хвосте
            if (xy_to.equals(tail)) {
                // курсор уже на хвосте, длина тела == 1, тогда разрушаем
                if (body.size() == 1) {
                    body.clear();
                    body_io.clear();
                    howManyVisited = 0;
                    //is_tending_to_destroy_the_head = false;
                    headIsCaptured = false;
                    return MoveResult.HEAD_IS_DESTROYED;
                }
                // курсор уже на хвосте, тогда переключаем на голову
                else {
                    abstractInputCursor = new Vector2(body.get(0));

                    boolean bodyShortened = false;
                    while (body.size() > 0) {
                        if (canStayHere(body.get(body.size() - 1))) {
                            break;
                        } else {
                            bodyShortened = true;
                            cutTail();
                        }
                    }

                    Collections.reverse(body);
                    Collections.reverse(body_io);
                    for (Vector2 item : body_io) {
                        float tmp = item.x;
                        item.x = item.y;
                        item.y = tmp;
                    }
                    //System.out.println(body);
                    //System.out.println(body_io);
                    //is_tending_to_destroy_the_head = true;
                    headIsCaptured = true;
                    falsePath.clear(); // чтобы стереть красную дорожку за курсором

                    if (bodyShortened) {
                        return MoveResult.BODY_IS_SHORTENED;
                    }

                    return MoveResult.OTHER_GOOD;
                }

            }
            // курсор не на хвосте, тогда переключаем на хвост
            else {
                abstractInputCursor = new Vector2(tail);
                headIsCaptured = true;
                falsePath.clear(); // чтобы стереть красную дорожку за курсором
                return MoveResult.OTHER_GOOD;
            }
        }
    }

    public MoveResult justTouchedMakeMove(int x_to, int y_to) {
        Vector2 xy_to = new Vector2(x_to, y_to);

        // либо тела нет, тогда нужно поставить голову
        if (body.size() == 0) {
            // можно ли ставить
            if (canStayHere(xy_to)) {
                body.add(xy_to);
                body_io.add(new Vector2(-1, -1));
                //System.out.println("Head is set on " + xy_to);
                howManyVisited = 1;
                headIsCaptured = true;
                isTendingToDestroyTheHead = false;
                return MoveResult.HEAD_IS_SET;
            } else {
                //System.out.println("Cant set the head here");
                return MoveResult.HEAD_IS_NOT_SET;
            }
        }
        // если тело есть
        else {
            Vector2 tail = body.get(body.size() - 1);
            //System.out.println("Tail: " + tail);

            // выбрал хвост
            if (xy_to.equals(tail)) {
                isTendingToDestroyTheHead = true;
                headIsCaptured = true;

                return MoveResult.OTHER_GOOD;
            }
            // выбрал голову
            if (xy_to.equals(body.get(0))) {

                boolean bodyShortened = false;
                while (body.size() > 0) {
                    if (canStayHere(body.get(body.size() - 1))) {
                        break;
                    } else {
                        bodyShortened = true;
                        cutTail();
                    }
                }

                // надо всё сделать задом наперёд
                //System.out.println("You chose head");
                Collections.reverse(body);
                Collections.reverse(body_io);
                for (Vector2 item : body_io) {
                    float tmp = item.x;
                    item.x = item.y;
                    item.y = tmp;
                }
                //System.out.println(body);
                //System.out.println(body_io);
                isTendingToDestroyTheHead = true;
                headIsCaptured = true;

                return MoveResult.OTHER_GOOD;

            }
            // иначе просто тап вникуда
            return MoveResult.OTHER_BAD;

        }
    }

    public MoveResult slideJustUntouchedMakeMove() {
        if (howManyVisited == howManyShouldBeVisited && canStayHere(body.get(body.size() - 1))) {
            return MoveResult.VICTORY;
        }

        return MoveResult.OTHER_GOOD;
    }

    public MoveResult justUntouchedMakeMove(int x_to, int y_to) {
        headIsCaptured = false;
        if (isTendingToDestroyTheHead) {
            if (body.size() == 1 && body.get(body.size() - 1).equals(new Vector2(x_to, y_to))) {
                // убрать тело
                if (body.size() == 1) {
                    //System.out.println("The head is destroyed");
                    body.clear();
                    body_io.clear();
                    howManyVisited = 0;
                    isTendingToDestroyTheHead = false;

                    return MoveResult.HEAD_IS_DESTROYED;
                }
            }
        }

        falsePath.clear();

        boolean bodyShortened = false;
        while (body.size() > 0) {
            if (canStayHere(body.get(body.size() - 1))) {
                break;
            } else {
                bodyShortened = true;
                cutTail();
            }
        }

        if (howManyVisited == howManyShouldBeVisited) {
            return MoveResult.VICTORY;
        }

        // сначала идёт проверка на победу на случай, если палец завёл тело слишком далеко и укорочение привело к победе
        if (bodyShortened) {
            return MoveResult.BODY_IS_SHORTENED;
        }

        return MoveResult.OTHER_GOOD;

    }

    public MoveResult slideTouchedMakeMove() {
        return touchedMakeMove((int) abstractInputCursor.x, (int) abstractInputCursor.y);
    }

    public MoveResult touchedMakeMove(int x_to, int y_to) {
        if (!headIsCaptured) {
            //System.out.println("Head is not captured");
            return MoveResult.HEAD_IS_NOT_CAPTURED;
        }
        //System.out.println("Make move to " + x_to + ", " + y_to);

        Vector2 xy_to = new Vector2(x_to, y_to);
        // граница карты
        if (x_to < 0 || y_to < 0 || x_to >= field_size || y_to >= field_size) {
            //System.out.println("Out of bounds");
            return MoveResult.OUT_OF_BOUNDS;
        }

        // коробка
        if (boxes.contains(xy_to)) {
            //System.out.println("Move on the box");
            return MoveResult.MOVE_INTO_BOX;
        }

        // если нет тела
        if (body.size() == 0) {
            return MoveResult.OTHER_BAD;
        } else {
            Vector2 tail = body.get(body.size() - 1);
            //System.out.println("Tail: " + tail);

            // остался ли на месте
            if (xy_to.equals(tail)) {
                //System.out.println("No movement");
                return MoveResult.NO_MOVEMENT;
            }

            // ход назад
            if (body.size() >= 2 && xy_to.equals(body.get(body.size() - 2))) {
                //System.out.println("Move back");
                cutTail();
                isTendingToDestroyTheHead = false;
                return MoveResult.MOVE_BACK;
            }

            // направление
            int moveDirection = directionTo((int) tail.x, (int) tail.y, x_to, y_to);
            //System.out.println("Move direction: " + move_direction);


            // сосед ли
            if (moveDirection == -1) {
                //System.out.println("Not a neighbor");
                return MoveResult.NOT_A_NEIGHBOR;
            }

            // диагональные стенки
            if (slash_walls.contains(tail)) {
                int canVisit = SLASH_IO[(int) body_io.get(body_io.size() - 1).x][moveDirection];
                if (canVisit == 0) {
                    //System.out.println("Movement through the slash wall");
                    return MoveResult.MOVE_THROUGH_SLASH_WALL;
                }

            }
            if (backslash_walls.contains(tail)) {
                int canVisit = BACKSLASH_IO[(int) body_io.get(body_io.size() - 1).x][moveDirection];
                if (canVisit == 0) {
                    //System.out.println("Movement through the backslash wall");
                    return MoveResult.MOVE_THROUGH_BACKSLASH_WALL;
                }

            }

            // точка
            if (points.contains(tail)) {
                int canVisit = POINT_IO[(int) body_io.get(body_io.size() - 1).x][moveDirection];
                if (canVisit == 0) {
                    //System.out.println("Movement through the point");
                    return MoveResult.MOVE_THROUGH_POINT;
                }
            }

            // перекрёсток
            if (crossroads.contains(tail)) {
                int canVisit = CROSSROAD_IO[(int) body_io.get(body_io.size() - 1).x][moveDirection];
                if (canVisit == 0) {
                    //System.out.println("Movement through the crossroad");
                    return MoveResult.MOVE_THROUGH_CROSSROAD;
                }
            }

            if (moveDirection == 0) {
                if (horizontal_walls.contains(tail)) {
                    //System.out.println("Movement through the horizontal_wall");
                    return MoveResult.MOVE_THROUGH_HORIZONTAL_WALL;
                }
            }
            else if (moveDirection == 2) {
                if (horizontal_walls.contains(xy_to)) {
                    //System.out.println("Movement through the horizontal_wall");
                    return MoveResult.MOVE_THROUGH_HORIZONTAL_WALL;
                }
            }
            else if (moveDirection == 1) {
                if (vertical_walls.contains(tail)) {
                    //System.out.println("Movement through the vertical_wall");
                    return MoveResult.MOVE_THROUGH_VERTICAL_WALL;
                }
            }
            else if (moveDirection == 3) {
                if (vertical_walls.contains(xy_to)) {
                    //System.out.println("Movement through the vertical_wall");
                    return MoveResult.MOVE_THROUGH_VERTICAL_WALL;
                }
            }

            int i = body.indexOf(xy_to);
            if (i != -1) {
                //System.out.println("Segment " + i + ": " + body.get(i) + " is at the same position");

                if (i == 0) {
                    //System.out.println("This is my tail");
                    return MoveResult.BODY_NOT_VISITED;
                }
                if (body.lastIndexOf(xy_to) != i) {
                    //System.out.println("Triple visited");
                    return MoveResult.BODY_NOT_VISITED;
                }

                int visitedSegment = SEGMENT_BY_IO[(int) body_io.get(i).x][(int) body_io.get(i).y];
                //System.out.println("Visited segment: " + visited_segment);

                int canVisit = ENTRANCE_TO_BODY[moveDirection][visitedSegment];

                if (canVisit > 0) {
                    //System.out.println("Move is done");
                    body.add(xy_to);
                    body_io.get(body_io.size() - 1).y = moveDirection;
                    body_io.add(new Vector2(NEGATE_DIRECTION[moveDirection], -1));
                    return MoveResult.BODY_VISITED;
                } else {
                    //System.out.println("Can't visit by rules");
                    return MoveResult.BODY_NOT_VISITED;
                }

            }

            //System.out.println("Simple movement");
            body.add(xy_to);
            body_io.get(body_io.size() - 1).y = moveDirection;
            body_io.add(new Vector2(NEGATE_DIRECTION[moveDirection], -1));

            howManyVisited++; //update_how_many_visited();

            return MoveResult.SIMPLE_MOVEMENT;
        }
    }

    private void clockwiseTurnItem(Vector2 item) {
        float newX = item.y;
        float newY = -item.x + (field_size - 1);
        item.x = newX;
        item.y = newY;
    }

    private void clockwiseTurnArray(ArrayList<Vector2> arr) {
        for (Vector2 item : arr) {
            clockwiseTurnItem(item);
        }
    }

    public void clockwiseTurn() {
        ArrayList<Vector2> tmp;

        clockwiseTurnArray(vertical_walls);
        for (Vector2 item : vertical_walls) {
            item.y--;
        }
        clockwiseTurnArray(horizontal_walls);
        // тк горизонтальные <--> вертикальные
        tmp = vertical_walls;
        vertical_walls = horizontal_walls;
        horizontal_walls = tmp;


        clockwiseTurnArray(slash_walls);
        clockwiseTurnArray(backslash_walls);
        // тк slash <--> backslash
        tmp = slash_walls;
        slash_walls = backslash_walls;
        backslash_walls = tmp;

        clockwiseTurnArray(boxes);
        clockwiseTurnArray(points);
        clockwiseTurnArray(crossroads);
        clockwiseTurnArray(hint);
        clockwiseTurnItem(abstractInputCursor);

        clockwiseTurnArray(body);
        for (Vector2 item : body_io) {
            item.x = (item.x + 1) % 4;
            item.y = (item.y + 1) % 4;
        }
    }

    private void counterclockwiseTurnItem(Vector2 item) {
        float newX = -item.y + (field_size - 1);
        float newY = item.x;
        item.x = newX;
        item.y = newY;
    }

    private void counterclockwiseTurnArray(ArrayList<Vector2> arr) {
        for (Vector2 item : arr) {
            counterclockwiseTurnItem(item);
        }
    }

    public void counterclockwiseTurn() {
        ArrayList<Vector2> tmp;

        counterclockwiseTurnArray(vertical_walls);
        counterclockwiseTurnArray(horizontal_walls);
        for (Vector2 item : horizontal_walls) {
            item.x -= 1;
        }
        tmp = vertical_walls;
        vertical_walls = horizontal_walls;
        horizontal_walls = tmp;

        counterclockwiseTurnArray(slash_walls);
        counterclockwiseTurnArray(backslash_walls);
        tmp = slash_walls;
        slash_walls = backslash_walls;
        backslash_walls = tmp;

        counterclockwiseTurnArray(boxes);
        counterclockwiseTurnArray(points);
        counterclockwiseTurnArray(crossroads);
        counterclockwiseTurnArray(hint);
        counterclockwiseTurnItem(abstractInputCursor);

        counterclockwiseTurnArray(body);
        for (Vector2 item : body_io) {
            item.x = (item.x + 3) % 4;  // +3 по модулю 4 равно -1
            item.y = (item.y + 3) % 4;
        }
    }

    private void mirrorTurnItem(Vector2 item) {
        item.x = field_size - 1 - item.x;
    }

    private void mirrorTurnArray(ArrayList<Vector2> arr) {
        for (Vector2 item : arr) {
            mirrorTurnItem(item);
        }
    }

    public void mirrorTurn() {
        ArrayList<Vector2> tmp;
        mirrorTurnArray(vertical_walls);
        for (Vector2 item : vertical_walls) {
            item.x--;
        }
        mirrorTurnArray(horizontal_walls);
        mirrorTurnArray(slash_walls);
        mirrorTurnArray(backslash_walls);

        tmp = slash_walls;
        slash_walls = backslash_walls;
        backslash_walls = tmp;

        mirrorTurnArray(boxes);
        mirrorTurnArray(points);
        mirrorTurnArray(crossroads);
        mirrorTurnArray(hint);
        mirrorTurnItem(abstractInputCursor);

        mirrorTurnArray(body);
        for (Vector2 item : body_io) {
            if (item.x % 2 != 0) {
                item.x = (item.x + 2) % 4;  // равносильно двум поворотам по часовой
            }
            if (item.y % 2 != 0) {
                item.y = (item.y + 2) % 4;
            }
        }
    }
}
