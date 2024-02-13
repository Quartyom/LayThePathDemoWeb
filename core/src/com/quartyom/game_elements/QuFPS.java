package com.quartyom.game_elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Queue;

public class QuFPS {

    Queue<Integer> fpsQueue;

    public QuFPS() {
        fpsQueue = new Queue<>();
    }

    public void update() {

        fpsQueue.addLast(Gdx.graphics.getFramesPerSecond());

        if (fpsQueue.size > 60) {
            fpsQueue.removeFirst();
        }

        int sum = 0;
        int min = Integer.MAX_VALUE;

        for (int item : fpsQueue) {
            sum += item;
            if (item < min) {
                min = item;
            }
        }

        minFPS = min;
        avgFPS = (float) sum / fpsQueue.size;

    }

    public float minFPS, avgFPS;
}
