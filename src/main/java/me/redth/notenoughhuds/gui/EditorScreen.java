package me.redth.notenoughhuds.gui;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.widget.HudMenu;
import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class EditorScreen extends GuiScreen {
    public static final int HOVERING_COLOR = 0x80FFFFFF;
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    //    private final SnappingUtils snapper;
    private final HudMenu hudMenu = new HudMenu(new HudMenu.MenuEntry("Settings", hud -> {
        mc.displayGuiScreen(new SettingsScreen(this, hud));
        hovering = null;
    }), new HudMenu.MenuEntry("Disable", hud -> {
        hud.setEnabled(false);
        hovering = null;
    }));
    private BaseHud hovering;
    private BaseHud dragging;
    private int xOffset;
    private int yOffset;

    public EditorScreen(GuiScreen parent) {
        this.parent = parent;
//        snapper = new SnappingUtils(this);
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
        if (buttonList.contains(hudMenu)) {
            hudMenu.hud.drawPad(HOVERING_COLOR);
        } else if (dragging != null) {
            dragging.setX(mouseX + xOffset);
            dragging.setY(mouseY + yOffset);
            dragging.drawPad(HOVERING_COLOR);
        } else if (hovering != null) {
            hovering.drawPad(HOVERING_COLOR);
            drawHoveringText(ImmutableList.of(I18n.format(hovering.getTranslationKey())), mouseX, mouseY);
            GlStateManager.disableLighting();
        }
        drawCenteredString(fontRendererObj, "NotEnoughHUDs", width / 2, height / 2 - 26, 16777215);
        drawCenteredString(fontRendererObj, "Left Click to Drag", width / 2, height / 2 + 78, 16777215);
        drawCenteredString(fontRendererObj, "Right Click to Open Settings", width / 2, height / 2 + 87, 16777215);
        super.drawScreen(mouseX, mouseY, delta);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);

        buttonList.remove(hudMenu);

        if (hovering != null) {
            switch (button) {
                case 0:
                    dragging = hovering;
                    xOffset = dragging.getX() - mouseX;
                    yOffset = dragging.getY() - mouseY;
//                    updateSnaps(mouseX, mouseY);
                    break;
                case 1:
                    hudMenu.show(hovering, mouseX, mouseY, mouseX > width / 2, mouseY > height / 2);
                    buttonList.add(hudMenu);
                    break;
            }
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
        }
    }

//    public void mouseMoved(int mouseX, int mouseY) {
//        if (dragging != null) {
//            updateSnaps(mouseX, mouseY);
//            dragging.setX(snapper.getSnappedX());
//            dragging.setY(snapper.getSnappedY());
//            dragging.setX(mouseX + xOffset);
//            dragging.setY(mouseY + yOffset);
//        }
//    }

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

//    public void updateSnaps(double mouseX, double mouseY) {
//        snapper.updateSnaps(dragging, (int) mouseX + xOffset, (int) mouseY + yOffset);
//    }
}
