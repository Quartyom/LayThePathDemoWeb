package com.quartyom.game_elements;

import com.badlogic.gdx.utils.TimeUtils;
import com.quartyom.LayThePath;
import com.quartyom.interfaces.QuEvent;

public class Timer {

    private QuEvent action;

    private long whenToAct;

    public Timer(QuEvent action) {
        this.action = action;
    }

    public void set(long ms) {
        whenToAct = TimeUtils.millis() + ms;
    }

    public void update() {
        if (TimeUtils.millis() >= whenToAct) {
            action.execute();
        }
    }
}
