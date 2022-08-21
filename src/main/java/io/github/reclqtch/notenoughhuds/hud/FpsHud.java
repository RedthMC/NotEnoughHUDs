package io.github.reclqtch.notenoughhuds.hud;

import net.minecraft.client.Minecraft;

public class FpsHud extends TextHud {

    public FpsHud() {
        super("fps", "%fps% FPS");
    }

    @Override
    protected String getText() {
        return this.format.get().replaceAll("%fps%", String.valueOf(Minecraft.getDebugFPS()));
    }

}
