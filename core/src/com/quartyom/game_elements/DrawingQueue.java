package com.quartyom.game_elements;

import com.badlogic.gdx.utils.Queue;
import com.quartyom.interfaces.Drawable;

public class DrawingQueue {
    private Queue<Drawable> elements;

    public DrawingQueue() {
        elements = new Queue<>();
    }

    public void add(Drawable element) {
        elements.addLast(element);
    }

    public void draw() {
        for (Drawable item : elements) {
            item.draw();
        }
        elements.clear();
    }
}
