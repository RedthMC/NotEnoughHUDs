package me.redth.notenoughhuds.gui;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.widget.HudMenu;
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

public class EditorRevamp extends GuiScreen {
    public static final int HOVERING_COLOR = 0x80FFFFFF;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    private BaseHud hovering;
    private BaseHud selecting;
    private boolean dragging;
    private int xOffset;
    private int yOffset;

    public EditorRevamp(GuiScreen parent) {
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
        if (selecting != null) selecting.drawPad(HOVERING_COLOR);
        if (dragging && selecting != null) {
            selecting.setX(mouseX + xOffset);
            selecting.setY(mouseY + yOffset);
        } else if (hovering != null) {
            hovering.drawPad(HOVERING_COLOR);
            drawHoveringText(ImmutableList.of(I18n.format(hovering.getTranslationKey())), mouseX, mouseY);
            GlStateManager.disableLighting();
        }
        drawCenteredString(fontRendererObj, "NotEnoughHUDs", width / 2, height / 2 - 26, 16777215);
        drawCenteredString(fontRendererObj, "Left Click to Drag", width / 2, height / 2 + 78, 16777215);
        super.drawScreen(mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (button != 0) return;
        if (hovering != null) {
            selecting = hovering;
            xOffset = selecting.getX() - mouseX;
            yOffset = selecting.getY() - mouseY;
            dragging = true;
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        } else {
            selecting = null;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        if (button != 0) return;
        dragging = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (selecting == null) return;
        int speed = isShiftKeyDown() ? 2 : 1;
        switch (keyCode) {
            case Keyboard.KEY_UP:
                hovering.setY(hovering.getY() - speed);
                break;
            case Keyboard.KEY_DOWN:
                hovering.setY(hovering.getY() + speed);
                break;
            case Keyboard.KEY_LEFT:
                hovering.setX(hovering.getX() - speed);
                break;
            case Keyboard.KEY_RIGHT:
                hovering.setX(hovering.getX() + speed);
                break;
        }
    }
}
