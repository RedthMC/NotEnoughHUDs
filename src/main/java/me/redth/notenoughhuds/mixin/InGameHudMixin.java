package me.redth.notenoughhuds.mixin;

import me.redth.notenoughhuds.NotEnoughHUDs;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Inject(method = "renderStatusEffectOverlay", at = @At("HEAD"), cancellable = true)
    public void renderStatusEffect(MatrixStack matrices, CallbackInfo callback) {
        if (NotEnoughHUDs.getInstance().effectHud.isEnabled()) callback.cancel();
    }

    @Inject(method = "renderScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    public void renderScoreboardSidebar(MatrixStack matrices, ScoreboardObjective objective, CallbackInfo callback) {
        if (NotEnoughHUDs.getInstance().scoreboardHud.isEnabled()) callback.cancel();
    }
}
