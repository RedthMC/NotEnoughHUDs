package io.github.reclqtch.notenoughhuds.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TpsHud extends TextHud {
    private long prevTime = 0L;
    private long time = 20000L;

    public TpsHud() {
        super("tps", "%tps% TPS");
        options.add(format);
    }

    @Override
    protected String getText() {
        double tps = MathHelper.clamp_double(20000.0D / (time - prevTime), 0.0D, 20.0D);
        return format.get().replaceAll("%tps%", String.format("%.1f", tps));
    }

    public void onTimeUpdate() {
        prevTime = time;
        time = Minecraft.getSystemTime();
    }
}
