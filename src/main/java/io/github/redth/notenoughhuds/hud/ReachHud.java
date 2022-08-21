package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehInteger;
import io.github.redth.notenoughhuds.config.option.NehString;
import net.minecraft.util.Util;
import net.minecraft.util.hit.HitResult;

public class ReachHud extends TextHud {
    public final NehInteger precision = new NehInteger("precision", 2, 0, 5);
    public final NehString noHits = new NehString("no_hits", "No hits");
    private double reach = 0.0D;
    private long lastHit = 0L;

    public ReachHud() {
        super("reach", Alignment.LEFT, Alignment.TOP, 2, 2, "%reach% Blocks");
        options.add(precision);
        options.add(noHits);
    }

    public void updateReach(HitResult hitResult) {
        if (mc.player != null && hitResult != null) {
            reach = mc.player.getEyePos().distanceTo(hitResult.getPos());
            lastHit = Util.getMeasuringTimeMs();
        }
    }

    @Override
    protected String getText() {
        if (lastHit + 1000 < Util.getMeasuringTimeMs()) return noHits.get();
        return format.get().replaceAll("%reach%", String.format("%." + precision.get() + "f", reach));
    }

}
