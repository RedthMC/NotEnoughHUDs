package me.redth.notenoughhuds.mixin;

import me.redth.notenoughhuds.NotEnoughHUDs;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class OnWorldTimeUpdated {
    public boolean once;

    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    public void calculateTps(WorldTimeUpdateS2CPacket packet, CallbackInfo callback) {
        this.once = !this.once;
        if (this.once) NotEnoughHUDs.onTimeUpdate();
    }
}
