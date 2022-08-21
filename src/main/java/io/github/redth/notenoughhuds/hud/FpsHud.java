package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.mixin.MinecraftClientAccessor;

public class FpsHud extends TextHud {

    public FpsHud() {
        super("fps", Alignment.LEFT, Alignment.TOP, 2, 2, "%fps% FPS");
    }

    @Override
    protected String getText() {
        return this.format.get().replaceAll("%fps%", String.valueOf(MinecraftClientAccessor.getCurrentFps()));
    }

}
