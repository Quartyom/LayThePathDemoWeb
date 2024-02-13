package com.quartyom.screens.Level;

import com.badlogic.gdx.math.Vector2;

import java.sql.*;

public class LevelsHolder {

    public static LevelConfiguration get(int k){
        switch (k) {
            case 1:
                return level1();
            case 2:
                return level2();
            case 3:
                return level3();
            case 4:
                return level4();
            case 5:
                return level5();
            case 6:
                return level6();
            case 7:
                return level7();
            case 8:
                return level8();
            case 9:
                return level9();
            case 10:
                return level10();
            default:
                return new LevelConfiguration();
        }
    }

    private static LevelConfiguration level1(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.hint.add(new Vector2(0,2));
        conf.hint.add(new Vector2(2,0));
        return conf;
    }

    private static LevelConfiguration level2(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.boxes.add(new Vector2(0,2));
        conf.boxes.add(new Vector2(1,2));
        conf.boxes.add(new Vector2(2,2));
        conf.boxes.add(new Vector2(2,0));
        conf.boxes.add(new Vector2(1,0));
        conf.boxes.add(new Vector2(0,0));
        conf.hint.add(new Vector2(0,1));
        conf.hint.add(new Vector2(2,1));
        return conf;
    }

    private static LevelConfiguration level3(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.vertical_walls.add(new Vector2(0,1));
        conf.horizontal_walls.add(new Vector2(1,0));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(2,0));
        return conf;
    }

    private static LevelConfiguration level4(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.horizontal_walls.add(new Vector2(1,1));
        conf.backslash_walls.add(new Vector2(0,0));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(1,1));
        return conf;
    }

    private static LevelConfiguration level5(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.vertical_walls.add(new Vector2(0,1));
        conf.horizontal_walls.add(new Vector2(1,0));
        conf.backslash_walls.add(new Vector2(1,1));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(2,0));
        return conf;
    }

    private static LevelConfiguration level6(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 3;
        conf.slash_walls.add(new Vector2(0,2));
        conf.boxes.add(new Vector2(2,0));
        conf.points.add(new Vector2(1,0));
        conf.points.add(new Vector2(1,2));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(0,0));
        return conf;
    }

    private static LevelConfiguration level7(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 4;
        conf.vertical_walls.add(new Vector2(1,0));
        conf.vertical_walls.add(new Vector2(1,3));
        conf.horizontal_walls.add(new Vector2(1,0));
        conf.backslash_walls.add(new Vector2(0,0));
        conf.boxes.add(new Vector2(3,3));
        conf.points.add(new Vector2(1,1));
        conf.hint.add(new Vector2(1,0));
        conf.hint.add(new Vector2(2,3));
        return conf;
    }
    private static LevelConfiguration level8(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 4;
        conf.vertical_walls.add(new Vector2(0,1));
        conf.vertical_walls.add(new Vector2(2,2));
        conf.horizontal_walls.add(new Vector2(1,0));
        conf.horizontal_walls.add(new Vector2(2,2));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(3,0));
        return conf;
    }
    private static LevelConfiguration level9(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 4;
        conf.horizontal_walls.add(new Vector2(2,2));
        conf.backslash_walls.add(new Vector2(0,0));
        conf.backslash_walls.add(new Vector2(2,1));
        conf.boxes.add(new Vector2(3,0));
        conf.boxes.add(new Vector2(0,2));
        conf.boxes.add(new Vector2(0,3));
        conf.points.add(new Vector2(1,3));
        conf.hint.add(new Vector2(2,2));
        conf.hint.add(new Vector2(0,1));
        return conf;
    }

    private static LevelConfiguration level10(){
        LevelConfiguration conf = new LevelConfiguration();
        conf.field_size = 4;
        conf.vertical_walls.add(new Vector2(1,3));
        conf.boxes.add(new Vector2(0,0));
        conf.boxes.add(new Vector2(0,1));
        conf.boxes.add(new Vector2(0,2));
        conf.boxes.add(new Vector2(0,3));
        conf.boxes.add(new Vector2(3,0));
        conf.points.add(new Vector2(1,2));
        conf.points.add(new Vector2(1,1));
        conf.points.add(new Vector2(2,1));
        conf.points.add(new Vector2(2,0));
        conf.hint.add(new Vector2(1,3));
        conf.hint.add(new Vector2(2,3));
        return conf;
    }
}
