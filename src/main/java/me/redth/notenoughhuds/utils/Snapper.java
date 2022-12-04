package me.redth.notenoughhuds.utils;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.EditorScreen;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.gui.GuiScreen;

import java.util.LinkedHashSet;
import java.util.Set;

public class Snapper extends DrawUtils {
    public static final int LINE_COLOR = 0xFF00FFAA;
    public static final int DISTANCE = 4;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final EditorScreen screen;
    private final Set<Integer> edgeXs = new LinkedHashSet<>();
    private final Set<Integer> centerXs = new LinkedHashSet<>();
    private final Set<Integer> edgeYs = new LinkedHashSet<>();
    private final Set<Integer> centerYs = new LinkedHashSet<>();
    private int xOffset;
    private int yOffset;

    public Snapper(EditorScreen screen) {
        this.screen = screen;
    }

    public static boolean snapsWith(int pos, int snap) {
        return pos >= snap - DISTANCE && pos < snap + DISTANCE;
    }

    public void onClick(int mouseX, int mouseY) {
        xOffset = screen.dragging.getX() - mouseX;
        yOffset = screen.dragging.getY() - mouseY;

        edgeXs.clear();
        centerXs.clear();
        edgeYs.clear();
        centerYs.clear();

        centerXs.add(screen.width / 2);
        centerYs.add(screen.height / 2);

        for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
            if (hud.equals(screen.dragging)) continue;

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

    public int getSnappedX(int mouseX) {
        mouseX += xOffset;

        if (GuiScreen.isShiftKeyDown()) return mouseX;

        int width = screen.dragging.getScaledWidth();

        for (int centerX : centerXs) {
            if (snapsWith(mouseX + width / 2, centerX)) {
                return drawLineX(centerX, centerX - width / 2);
            }
        }
        for (int edgeX : edgeXs) {
            if (snapsWith(mouseX, edgeX)) {
                return drawLineX(edgeX, edgeX);
            }
            if (snapsWith(mouseX + width, edgeX)) {
                return drawLineX(edgeX, edgeX - width);
            }
        }

        return mouseX;
    }

    public int getSnappedY(int mouseY) {
        mouseY += yOffset;

        if (GuiScreen.isShiftKeyDown()) return mouseY;

        int height = screen.dragging.getScaledHeight();

        for (int centerY : centerYs) {
            if (snapsWith(mouseY + height / 2, centerY)) return drawLineY(centerY, centerY - height / 2);
        }
        for (int edgeY : edgeYs) {
            if (snapsWith(mouseY, edgeY)) return drawLineY(edgeY, edgeY);
            if (snapsWith(mouseY + height, edgeY)) return drawLineY(edgeY, edgeY - height);
        }

        return mouseY;
    }

    public int drawLineX(int lineX, int snapX) {
        if (snapX >= 0 && snapX + screen.dragging.getScaledWidth() < screen.width)
            drawVerticalLine(lineX, 0F, screen.height, LINE_COLOR);
        return snapX;
    }

    public int drawLineY(int lineY, int snapY) {
        if (snapY >= 0 && snapY + screen.dragging.getScaledHeight() < screen.height)
            drawHorizontalLine(0F, screen.width, lineY, LINE_COLOR);
        return snapY;
    }
}
