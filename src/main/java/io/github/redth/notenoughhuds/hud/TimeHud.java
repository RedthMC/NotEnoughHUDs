package io.github.redth.notenoughhuds.hud;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHud extends TextHud {

    public TimeHud() {
        super("time", Alignment.LEFT, Alignment.TOP, 2, 2, "h:mm a");
    }

    @Override
    protected String getText() {
        String t;
        try {
            t = new SimpleDateFormat(format.get()).format(new Date(System.currentTimeMillis()));
        } catch (IllegalArgumentException e) {
            t = format.get();
        }
        return t;
    }

}
