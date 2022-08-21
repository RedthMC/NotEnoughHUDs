package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Util;

public class ComboHud extends TextHud {
    public final NehInteger expire = new NehInteger("expire", 2, 1, 10);
    private static int combo;
    private long lastHit;

    public ComboHud() {
        super("combo", Alignment.LEFT, Alignment.TOP, 2, 2, "%combo% Combos");
        options.add(expire);
    }

    public void updateCombo(PlayerEntity p, Entity e) {
        if (p.equals(mc.player)) {
            ++combo;
            lastHit = Util.getMeasuringTimeMs();
        } else if (e.equals(mc.player)) {
            combo = 0;
        }
    }

    @Override
    protected String getText() {
        if (lastHit + 1000L * expire.get() < Util.getMeasuringTimeMs()) combo = 0;
        return format.get().replaceAll("%combo%", String.valueOf(combo));
    }
}
