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

        if (mc.thePlayer != null) {
            double dx = mc.thePlayer.posX - mc.thePlayer.prevPosX;
            double dz = mc.thePlayer.posZ - mc.thePlayer.prevPosZ;
            speed = Math.sqrt(dx * dx + dz * dz) * 20;
        } else {
            speed = 1.23456d;
        }

        return this.format.get().replaceAll("%speed%", String.format("%." + precision.get() + "f", speed));
    }

}
