package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.widget.FlatButton;
import me.redth.notenoughhuds.gui.widget.HudMenu;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.LinkedHashSet;
import java.util.Set;

public class EditorScreen extends Screen {
    public static final int HOVERING_COLOR = 0x80FFFFFF;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    private final boolean dirtBackground;
    private BaseHud hovering;
    private BaseHud dragging;
    private HudMenu currentMenu;
    private final Set<Integer> edgeXs = new LinkedHashSet<>();
    private final Set<Integer> centerXs = new LinkedHashSet<>();
    private final Set<Integer> edgeYs = new LinkedHashSet<>();
    private final Set<Integer> centerYs = new LinkedHashSet<>();
    private int xOffset;
    private int yOffset;

    public EditorScreen(Screen parent) {
        super(Text.of("NotEnoughHUDs"));
        this.parent = parent;
        this.dirtBackground = MinecraftClient.getInstance().world == null;
    }

    @Override
    protected void init() {
        currentMenu = null;
        client.keyboard.setRepeatEvents(true);
        addDrawableChild(new FlatButton(width / 2 - 100, height / 2 + 12, 200, 20, "settings", b -> client.setScreenAndRender(new SettingsScreen(this))));
        addDrawableChild(new FlatButton(width / 2 - 100, height / 2 + 34, 200, 20, "back", b -> client.setScreenAndRender(parent)));
    }

    @Override
    public void removed() {
        client.keyboard.setRepeatEvents(false);
        neh.config.save();
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        if (dirtBackground) {
            renderBackground(matrix);
            BaseHud.screenWidth = width;
            BaseHud.screenHeight = height;
            for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
                hud.renderScaled(matrix);
            }
        }
        hovering = null;
        for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
            if (hud.getX() <= mouseX && mouseX < hud.getX() + hud.getScaledWidth() && hud.getY() <= mouseY && mouseY < hud.getY() + hud.getScaledHeight()) {
                hovering = hud;
                break;
            }
        }

        if (dragging != null) {
            dragging.setX(getSnappedX(matrix, mouseX));
            dragging.setY(getSnappedY(matrix, mouseY));
            dragging.drawPad(matrix, HOVERING_COLOR);
        } else if (hovering != null) {
            hovering.drawPad(matrix, HOVERING_COLOR);
            renderTextHoverEffect(matrix, Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hovering.getTranslated())), mouseX, mouseY);
        }

        drawCenteredText(matrix, textRenderer, "NotEnoughHUDs", width / 2, height / 2 - 26, 16777215);
        drawCenteredText(matrix, textRenderer, "Left Click to Drag", width / 2, height / 2 + 78, 16777215);
        drawCenteredText(matrix, textRenderer, "Right Click to Open Settings", width / 2, height / 2 + 87, 16777215);
        drawCenteredText(matrix, textRenderer, "Middle Click to Disable", width / 2, height / 2 + 96, 16777215);
        super.render(matrix, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        super.mouseClicked(mouseX, mouseY, button);
        if (currentMenu != null && !currentMenu.isMouseOver(mouseX, mouseY)) {
            remove(currentMenu);
            currentMenu = null;
        }
        if (hovering != null) {
            switch (button) {
                case 0 -> {
                    dragging = hovering;
                    initSnap((int) mouseX, (int) mouseY);
                }
                case 1 -> {
                    client.setScreenAndRender(new SettingsScreen(this, hovering));
                    hovering = null;
                }
                case 2 -> {
                    hovering.setEnabled(false);
                    hovering = null;
                }
            }

        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        if (button == 0) dragging = null;
        return false;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) return true;
        if (dragging == null && hovering != null) switch (keyCode) {
            case InputUtil.GLFW_KEY_UP -> hovering.setY(hovering.getY() - 1);
            case InputUtil.GLFW_KEY_DOWN -> hovering.setY(hovering.getY() + 1);
            case InputUtil.GLFW_KEY_LEFT -> hovering.setX(hovering.getX() - 1);
            case InputUtil.GLFW_KEY_RIGHT -> hovering.setX(hovering.getX() + 1);
        }
        return false;
    }

    public void initSnap(int mouseX, int mouseY) {
        xOffset = dragging.getX() - mouseX;
        yOffset = dragging.getY() - mouseY;

        edgeXs.clear();
        centerXs.clear();
        edgeYs.clear();
        centerYs.clear();

        centerXs.add(width / 2);
        centerYs.add(height / 2);

        for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
            if (hud.equals(dragging)) continue;

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

    public int getSnappedX(MatrixStack matrix, int mouseX) {
        mouseX += xOffset;

        if (Screen.hasShiftDown()) return mouseX;

        int width = dragging.getScaledWidth();

        for (int centerX : centerXs) {
            if (snapsWith(mouseX + width / 2, centerX)) return drawLineX(matrix, centerX, centerX - width / 2);
        }
        for (int edgeX : edgeXs) {
            if (snapsWith(mouseX, edgeX)) return drawLineX(matrix, edgeX, edgeX);
            if (snapsWith(mouseX + width, edgeX)) return drawLineX(matrix, edgeX, edgeX - width);
        }

        return mouseX;
    }

    public int getSnappedY(MatrixStack matrix, int mouseY) {
        mouseY += yOffset;

        if (Screen.hasShiftDown()) return mouseY;

        int height = dragging.getScaledHeight();

        for (int centerY : centerYs) {
            if (snapsWith(mouseY + height / 2, centerY)) return drawLineY(matrix, centerY, centerY - height / 2);
        }
        for (int edgeY : edgeYs) {
            if (snapsWith(mouseY, edgeY)) return drawLineY(matrix, edgeY, edgeY);
            if (snapsWith(mouseY + height, edgeY)) return drawLineY(matrix, edgeY, edgeY - height);
        }

        return mouseY;
    }

    public static boolean snapsWith(int pos, int snap) {
        return pos >= snap - 4 && pos < snap + 4;
    }

    public int drawLineX(MatrixStack matrix, int lineX, int snapX) {
        if (snapX >= 0 && snapX + dragging.getScaledWidth() < width)
            drawVerticalLine(matrix, lineX, 0, height, 0xFF00FFAA);
        return snapX;
    }

    public int drawLineY(MatrixStack matrix, int lineY, int snapY) {
        if (snapY >= 0 && snapY + dragging.getScaledHeight() < height)
            drawHorizontalLine(matrix, 0, width, lineY, 0xFF00FFAA);
        return snapY;
    }
}
