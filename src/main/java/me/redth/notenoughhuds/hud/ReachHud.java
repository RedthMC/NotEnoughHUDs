package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehInteger;
import me.redth.notenoughhuds.config.option.NehString;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;

public class ReachHud extends TextHud {
    public final NehInteger precision = new NehInteger("precision", 2, 0, 5);
    public final NehString noHits = new NehString("no_hits", "No hits");
    public final NehInteger expireTime = new NehInteger("expire_time",  2, 1, 10, i -> i + " s");
    private double reach = 0.0D;
    private long lastHit = 0L;

    public ReachHud() {
        super("reach", "%reach% Blocks");
        options.add(precision);
        options.add(noHits);
        options.add(expireTime);
    }

    public void updateReach() {
        if (mc.player != null && mc.crosshairTarget != null) {
            reach = mc.player.getEyePos().distanceTo(mc.crosshairTarget.getPos());
            lastHit = Util.getMeasuringTimeMs();
        }
    }

    @Override
    protected String getText() {
        if (lastHit + expireTime.get() * 1000L < Util.getMeasuringTimeMs()) return noHits.get();
        return format.get().replaceAll("%reach%", String.format("%." + precision.get() + "f", reach));
    }

}
