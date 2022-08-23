package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehInteger;
import me.redth.notenoughhuds.config.option.NehString;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.entity.player.AttackEntityEvent;

public class ReachHud extends TextHud {
    public final NehInteger precision = new NehInteger("precision", 2, 0, 5);
    public final NehString noHits = new NehString("no_hits", "No hits");
    private double reach = 0.0D;
    private long lastHit = 0L;

    public ReachHud() {
        super("reach", "%reach% Blocks");
        options.add(precision);
        options.add(noHits);
    }

    public void updateReach(AttackEntityEvent e) {
        if (e.entityPlayer.equals(mc.thePlayer) && mc.objectMouseOver != null) {
            reach = mc.thePlayer.getPositionEyes(1.0F).distanceTo(mc.objectMouseOver.hitVec);
            lastHit = Minecraft.getSystemTime();
        }
    }

    @Override
    protected String getText() {
        if (lastHit + 1000 < Minecraft.getSystemTime()) return noHits.get();
        return format.get().replaceAll("%reach%", String.format("%." + precision.get() + "f", reach));
    }

}
