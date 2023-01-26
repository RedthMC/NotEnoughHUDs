package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehInteger;

public class SpeedHud extends TextHud {
    public final NehInteger precision = new NehInteger("precision", 2, 0, 5);

    public SpeedHud() {
        super("speed", "%speed% blocks");
    }

    @Override
    protected String getText() {
        double speed;

        if (mc.player != null) {
            double dx = mc.player.getX() - mc.player.prevX;
            double dz = mc.player.getZ() - mc.player.prevZ;
            speed = Math.sqrt(dx * dx + dz * dz) * 20;
        } else {
            speed = 1.23456d;
        }

        return this.format.get().replaceAll("%speed%", String.format("%." + precision.get() + "f", speed));
    }

}
