package me.redth.notenoughhuds.gui;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

public class EditorScreen extends GuiScreen {
    public static final int HOVERING_COLOR = 0x80FFFFFF;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    public BaseHud hovering;
    public BaseHud dragging;
    private final Set<Integer> edgeXs = new LinkedHashSet<>();
    private final Set<Integer> centerXs = new LinkedHashSet<>();
    private final Set<Integer> edgeYs = new LinkedHashSet<>();
    private final Set<Integer> centerYs = new LinkedHashSet<>();
    private int xOffset;
    private int yOffset;

    public EditorScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(new GuiButtonExt(0, width / 2 - 100, height / 2 + 12, 200, 20, "Settings"));
        buttonList.add(new GuiButtonExt(1, width / 2 - 100, height / 2 + 34, 200, 20, "Back"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0:
                mc.displayGuiScreen(new SettingsScreen(this));
                break;
            case 1:
                mc.displayGuiScreen(parent);
                break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        neh.config.save();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float delta) {
        hovering = null;
        for (BaseHud hud : neh.hudManager.getEnabledHuds()) {
            if (hud.getX() <= mouseX && mouseX < hud.getX() + hud.getScaledWidth() && hud.getY() <= mouseY && mouseY < hud.getY() + hud.getScaledHeight()) {
                hovering = hud;
                break;
            }
        }
        if (dragging != null) {
            dragging.setX(getSnappedX(mouseX));
            dragging.setY(getSnappedY(mouseY));
            dragging.drawPad(HOVERING_COLOR);
        } else if (hovering != null) {
            hovering.drawPad(HOVERING_COLOR);
            drawHoveringText(ImmutableList.of(I18n.format(hovering.getTranslationKey())), mouseX, mouseY);
            GlStateManager.disableLighting();
        }
        drawCenteredString(fontRendererObj, "NotEnoughHUDs", width / 2, height / 2 - 26, 16777215);
        drawCenteredString(fontRendererObj, "Left Click to Drag", width / 2, height / 2 + 78, 16777215);
        drawCenteredString(fontRendererObj, "Right Click to Open Settings", width / 2, height / 2 + 87, 16777215);
        drawCenteredString(fontRendererObj, "Middle Click to Disable", width / 2, height / 2 + 96, 16777215);
        super.drawScreen(mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);

        if (hovering != null) {
            switch (button) {
                case 0:
                    dragging = hovering;
                    initSnap(mouseX, mouseY);
                    break;
                case 1:
                    mc.displayGuiScreen(new SettingsScreen(this, hovering));
                    hovering = null;
                    break;
                case 2:
                    hovering.setEnabled(false);
                    hovering = null;
                    break;
            }
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (button == 0) {
            dragging = null;
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (dragging == null && hovering != null) {
            switch (keyCode) {
                case Keyboard.KEY_UP:
                    hovering.setY(hovering.getY() - 1);
                    break;
                case Keyboard.KEY_DOWN:
                    hovering.setY(hovering.getY() + 1);
                    break;
                case Keyboard.KEY_LEFT:
                    hovering.setX(hovering.getX() - 1);
                    break;
                case Keyboard.KEY_RIGHT:
                    hovering.setX(hovering.getX() + 1);
                    break;
            }
        }
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

    public int getSnappedX(int mouseX) {
        mouseX += xOffset;

        if (GuiScreen.isShiftKeyDown()) return mouseX;

        int width = dragging.getScaledWidth();

        for (int centerX : centerXs) {
            if (snapsWith(mouseX + width / 2, centerX)) return drawLineX(centerX, centerX - width / 2);
        }
        for (int edgeX : edgeXs) {
            if (snapsWith(mouseX, edgeX)) return drawLineX(edgeX, edgeX);
            if (snapsWith(mouseX + width, edgeX)) return drawLineX(edgeX, edgeX - width);
        }

        return mouseX;
    }

    public int getSnappedY(int mouseY) {
        mouseY += yOffset;

        if (GuiScreen.isShiftKeyDown()) return mouseY;

        int height = dragging.getScaledHeight();

        for (int centerY : centerYs) {
            if (snapsWith(mouseY + height / 2, centerY)) return drawLineY(centerY, centerY - height / 2);
        }
        for (int edgeY : edgeYs) {
            if (snapsWith(mouseY, edgeY)) return drawLineY(edgeY, edgeY);
            if (snapsWith(mouseY + height, edgeY)) return drawLineY(edgeY, edgeY - height);
        }

        return mouseY;
    }

    public static boolean snapsWith(int pos, int snap) {
        return pos >= snap - 4 && pos < snap + 4;
    }

    public int drawLineX(int lineX, int snapX) {
        if (snapX >= 0 && snapX + dragging.getScaledWidth() < width)
            drawVerticalLine(lineX, 0, height, 0xFF00FFAA);
        return snapX;
    }

    public int drawLineY(int lineY, int snapY) {
        if (snapY >= 0 && snapY + dragging.getScaledHeight() < height)
            drawHorizontalLine(0, width, lineY, 0xFF00FFAA);
        return snapY;
    }
}
