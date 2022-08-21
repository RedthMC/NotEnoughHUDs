package io.github.redth.notenoughhuds.hud;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.util.Util;

public class CpsHud extends TextHud {
    private static final LongList leftClicks = new LongArrayList();
    private static final LongList rightClicks = new LongArrayList();

    public CpsHud() {
        super("cps", Alignment.LEFT, Alignment.TOP, 2, 82, "%left% | %right% CPS");
    }

    @Override
    protected String getText() {
        return this.format.get().replaceAll("%left%", String.valueOf(getLeftCps())).replaceAll("%right%", String.valueOf(getRightCps()));
    }

    public static int getLeftCps() {
        leftClicks.removeIf(l -> Util.getMeasuringTimeMs() - l > 1000L);
        return leftClicks.size();
    }

    public static int getRightCps() {
        rightClicks.removeIf(l -> Util.getMeasuringTimeMs() - l > 1000L);
        return rightClicks.size();
    }

    public static void onLeftClick() {
        leftClicks.add(Util.getMeasuringTimeMs());
    }

    public static void onRightClick() {
        rightClicks.add(Util.getMeasuringTimeMs());
    }

}
