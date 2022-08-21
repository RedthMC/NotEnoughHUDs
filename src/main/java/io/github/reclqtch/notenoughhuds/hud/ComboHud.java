package io.github.reclqtch.notenoughhuds.hud;

import io.github.reclqtch.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ComboHud extends TextHud {
    public final NehInteger expire = new NehInteger("expire", 2, 1, 10, "ms");
    private static int combo;
    private long lastHit;

    public ComboHud() {
        super("combo", "%combo% Combos");
        options.add(expire);
    }

    public void updateCombo(LivingHurtEvent e) {
        if (e.source.getEntity() == null) return;
        if (e.source.getEntity().equals(mc.thePlayer)) {
            ++combo;
            lastHit = Minecraft.getSystemTime();
        } else if (e.entity.getUniqueID().equals(mc.thePlayer.getUniqueID())) {
            combo = 0;
        }
    }

    @Override
    protected String getText() {
        if (lastHit + 1000L * expire.get() < Minecraft.getSystemTime()) combo = 0;
        return format.get().replaceAll("%combo%", String.valueOf(combo));
    }
}
