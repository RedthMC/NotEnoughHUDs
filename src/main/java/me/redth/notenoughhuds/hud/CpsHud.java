package me.redth.notenoughhuds.hud;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class CpsHud extends TextHud {
    private static final List<Long> leftClicks = new ArrayList<>();
    private static final List<Long> rightClicks = new ArrayList<>();

    public CpsHud() {
        super("cps", "%left% | %right% CPS");
    }

    @Override
    protected String getText() {
        int leftCps = getLeftCps();
        int rightCps = getRightCps();
        return this.format.get().replaceAll("%left%", String.valueOf(leftCps)).replaceAll("%right%", String.valueOf(rightCps));
    }

    public static void updateCps(int keycode) {
        if (keycode == mc.gameSettings.keyBindAttack.getKeyCode())
            leftClicks.add(Minecraft.getSystemTime());
        else if (keycode == mc.gameSettings.keyBindUseItem.getKeyCode())
            rightClicks.add(Minecraft.getSystemTime());
    }

    public static int getLeftCps() {
        leftClicks.removeIf(l -> Minecraft.getSystemTime() - l > 1000L);
        return leftClicks.size();
    }

    public static int getRightCps() {
        rightClicks.removeIf(l -> Minecraft.getSystemTime() - l > 1000L);
        return rightClicks.size();
    }

}
