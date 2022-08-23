package me.redth.notenoughhuds.hud;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeHud extends TextHud {

    public TimeHud() {
        super("time", "h:mm a");
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
