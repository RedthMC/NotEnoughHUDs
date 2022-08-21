package io.github.redth.notenoughhuds.hud;

import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class TpsHud extends TextHud {
    private long prevTime = 0L;
    private long time = 20000L;

    public TpsHud() {
        super("tps", Alignment.LEFT, Alignment.TOP, 2, 2, "%tps% TPS");
    }

    @Override
    protected String getText() {
        double tps = MathHelper.clamp(20000.0D / (time - prevTime), 0.0D, 20.0D);
        return format.get().replaceAll("%tps%", String.format("%.1f", tps));
    }

    public void onTimeUpdate() {
        prevTime = time;
        time = Util.getMeasuringTimeMs();
    }
}
