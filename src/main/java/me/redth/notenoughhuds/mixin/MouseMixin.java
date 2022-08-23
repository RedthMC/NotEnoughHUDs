package me.redth.notenoughhuds.mixin;

import me.redth.notenoughhuds.hud.CpsHud;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {
    @Inject(method = "onMouseButton", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/option/KeyBinding;setKeyPressed(Lnet/minecraft/client/util/InputUtil$Key;Z)V"))
    public void onClick(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action != 1) return;
        if (button == 0) CpsHud.onLeftClick();
        else if (button == 1) CpsHud.onRightClick();
    }
}
