package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.mixin.MinecraftClientAccessor;

public class FpsHud extends TextHud {

    public FpsHud() {
        super("fps", "%fps% FPS");
    }

    @Override
    protected String getText() {
        return this.format.get().replaceAll("%fps%", String.valueOf(MinecraftClientAccessor.getCurrentFps()));
    }

}
