package me.redth.notenoughhuds.utils;

import net.minecraft.client.Minecraft;

public class NehUtils {
    private static final Minecraft mc = Minecraft.getMinecraft();

    public static String shrinkWithEllipse(String text, int width) {
        if (mc.fontRendererObj.getStringWidth(text) < width) return text;
        text = mc.fontRendererObj.trimStringToWidth(text, width - mc.fontRendererObj.getStringWidth("..."));
        return text + "...";
    }
}
