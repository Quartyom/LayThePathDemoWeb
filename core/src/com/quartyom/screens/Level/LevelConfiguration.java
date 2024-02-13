package com.quartyom.screens.Level;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

// хранит состояние уровня, используется для json
public class LevelConfiguration {
    public int field_size;
    public ArrayList<Vector2> vertical_walls, horizontal_walls, slash_walls, backslash_walls, boxes, points, crossroads;
    public ArrayList<Vector2> hint;

    public LevelConfiguration() {
        vertical_walls = new ArrayList<>();
        horizontal_walls = new ArrayList<>();
        slash_walls = new ArrayList<>();
        backslash_walls = new ArrayList<>();
        boxes = new ArrayList<>();
        points = new ArrayList<>();
        crossroads = new ArrayList<>();
        hint = new ArrayList<>();
    }

    public LevelConfiguration(LevelConfiguration other){
        field_size = other.field_size;
        vertical_walls = copyArrayListOfVector(other.vertical_walls);
        horizontal_walls = copyArrayListOfVector(other.horizontal_walls);
        slash_walls = copyArrayListOfVector(other.slash_walls);
        backslash_walls = copyArrayListOfVector(other.backslash_walls);
        boxes = copyArrayListOfVector(other.boxes);
        points = copyArrayListOfVector(other.points);
        crossroads = copyArrayListOfVector(other.crossroads);
        hint = copyArrayListOfVector(other.hint);
    }

    // лучше сделать обобщение
    private ArrayList<Vector2> copyArrayListOfVector(ArrayList<Vector2> source){
        ArrayList<Vector2> result = new ArrayList<>();
        for (Vector2 item : source){
            result.add(new Vector2(item));
        }
        return result;
    }

    public void setEmpty() {
        vertical_walls.clear();
        horizontal_walls.clear();
        slash_walls.clear();
        backslash_walls.clear();
        boxes.clear();
        points.clear();
        crossroads.clear();
        hint.clear();
    }

}
