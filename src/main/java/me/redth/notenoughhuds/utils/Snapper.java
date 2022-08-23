package me.redth.notenoughhuds.utils;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.LinkedHashSet;
import java.util.Set;

public class Snapper extends DrawUtils {
    public static final int LINE_COLOR = 0xFF00FFAA;
    public static final int DISTANCE = 4;
    public static final int RANGE = 20;
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final int screenWidth;
    private final int screenHeight;
    private final Set<Integer> edgeXs = new LinkedHashSet<>();
    private final Set<Integer> centerXs = new LinkedHashSet<>();
    private final Set<Integer> edgeYs = new LinkedHashSet<>();
    private final Set<Integer> centerYs = new LinkedHashSet<>();
    private BaseHud current;
    private int snappedX;
    private int snappedY;
    private int lineX;
    private int lineY;

    public Snapper(GuiScreen screen) {
        screenWidth = screen.width;
        screenHeight = screen.height;
        snappedX = 0;
        snappedY = 0;
        lineX = -100;
        lineY = -100;
    }

    public static boolean snapsWith(int pos, int snap) {
        return pos >= snap - DISTANCE && pos < snap + DISTANCE;
    }

    public void setCurrent(BaseHud current) {
        this.current = current;

        edgeXs.clear();
        centerXs.clear();
        edgeYs.clear();
        centerYs.clear();

        centerXs.add(screenWidth / 2);
        centerYs.add(screenHeight / 2);

        for (BaseHud hud : NotEnoughHUDs.getInstance().hudManager.getEnabledHuds()) {
            if (hud.equals(current)) continue;

            int hx = hud.getX();
            int hy = hud.getY();
            int hw = hud.getScaledWidth();
            int hh = hud.getScaledHeight();

            edgeXs.add(hx);
            edgeXs.add(hx + hw);
            centerXs.add(hx + hw / 2);

            edgeYs.add(hy);
            edgeYs.add(hy + hh);
            centerYs.add(hy + hh / 2);
        }
    }

    public void updateSnaps(int x, int y) {

        if (GuiScreen.isShiftKeyDown()) {
            snappedX = x;
            snappedY = y;
            lineX = -100;
            lineY = -100;
            return;
        }

        int width = current.getScaledWidth();
        int height = current.getScaledHeight();
        int curCenX = x + width / 2;
        int curCenY = y + height / 2;

        snappedX = -100;

        for (int centerX : centerXs) {
            if (snapsWith(x + width / 2, centerX)) {
                snappedX = centerX - width / 2;
                lineX = centerX;
                break;
            }
        }
        if (snappedX == -100) {
            for (int edgeX : edgeXs) {
                if (snapsWith(x, edgeX)) {
                    snappedX = edgeX;
                    lineX = edgeX;
                    break;
                }
                if (snapsWith(x + width, edgeX)) {
                    snappedX = edgeX - width;
                    lineX = edgeX;
                    break;
                }
            }
        }
        if (snappedX < 0 || snappedX + width >= mc.displayWidth) {
            lineX = -100;
        }
        if (snappedX == -100) {
            snappedX = x;
        }

        snappedY = -100;
        for (int centerY : centerYs) {
            if (snapsWith(y + height / 2, centerY)) {
                snappedY = centerY - height / 2;
                lineY = centerY;
                break;
            }
        }
        if (snappedY == -100) {
            for (int edgeY : edgeYs) {
                if (snapsWith(y, edgeY)) {
                    snappedY = edgeY;
                    lineY = edgeY;
                    break;
                }
                if (snapsWith(y + height, edgeY)) {
                    snappedY = edgeY - height;
                    lineY = edgeY;
                    break;
                }
            }
        }
        if (snappedY < 0 || snappedY + height >= mc.displayHeight) {
            lineY = -100;
        }
        if (snappedY == -100) {
            snappedY = y;
        }
    }

    public int getSnappedX() {
        return snappedX;
    }

    public int getSnappedY() {
        return snappedY;
    }

    public void drawLines() {
        if (GuiScreen.isShiftKeyDown()) return;

        if (lineX != -100) drawVerticalLine(lineX, 0.0F, screenHeight, LINE_COLOR);
        if (lineY != -100) drawHorizontalLine(0.0F, screenWidth, lineY, LINE_COLOR);
    }

}
