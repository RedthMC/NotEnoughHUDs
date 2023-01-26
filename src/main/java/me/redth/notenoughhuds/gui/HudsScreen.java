package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.widget.GuiList;
import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HudsScreen extends GuiScreen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    public GuiList<HudButton> hudList;


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public HudsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {

        buttonList.add(hudList = new GuiList<>(this, 1, width / 2 - 188, 20, 390, height - 44));

        int x = hudList.xPosition;
        int y = hudList.yPosition+ 8;
        for (BaseHud hud : neh.hudManager.getHuds()) {
            hudList.addEntry(new HudsScreen.HudButton(x, y, hud));
            x += 128; // spacing 8

            if (x + 128 > hudList.xPosition + hudList.width) {
                x = hudList.xPosition;
                y += 88;
            }
        }

        buttonList.add(new GuiButtonExt(0, width / 2 - 100, height - 22, 200, 20, "Back"));
    }

    @Override
    public void onGuiClosed() {
        neh.config.save();
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(parent);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int scroll = Mouse.getEventDWheel();
        if (scroll != 0) {
            if (hudList.isMouseOver()) hudList.scrollBy(scroll);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawCenteredString(fontRendererObj, "HUDs", width / 2, 6, 0xFFFFFFFF);
    }

    public final class HudButton extends GuiList.GuiListEntry {
        public final ResourceLocation HUD_BUTTON = new ResourceLocation("notenoughhuds", "textures/hud_button.png");
        public final BaseHud hud;


        public HudButton(int x, int y, BaseHud hud) {
            super(x, y, 120, 80, I18n.format(hud.getTranslationKey()));
            this.hud = hud;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
                hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;

                DrawUtils.drawScaledTexture(HUD_BUTTON, xPosition, yPosition, hud.isEnabled() ? 120 : 0, hovered ? 80 : 0, 120, 80, 240, 160);

                DrawUtils.drawScaledTexture(hud.icon, xPosition + width / 2 - 16, yPosition + 6, 0, 0, 32, 32, 32, 32);

                GlStateManager.pushMatrix();
                GlStateManager.translate(xPosition + width / 2F, yPosition + 43, 0);
                GlStateManager.scale(1.5f, 1.5f, 1.5f);
                drawCenteredString(fontRendererObj, displayString, 0, 0, 0xFFFFFFFF);
                GlStateManager.popMatrix();

            }
//                hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
//                mouseDragged(mc, mouseX, mouseY);
//
//                if (hovered) drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x2FFFFFFF); // bg
//
//                DrawUtils.drawScaledTexture(hud.icon, xPosition + 4, yPosition + 4, 0, 0, 20, 20, 20, 20);
//
//                int y1 = yPosition + 4;
//                for (String s : displayString.split(" ")) {
//                    drawString(mc.fontRendererObj, s, xPosition + 28, y1, 0xFFFFFF);
//                    y1 += 9;
//                }
//
//                drawRect(xPosition + width - 16, yPosition, xPosition + width, yPosition + height, 0xFF5555FF);
//                drawCenteredString(mc.fontRendererObj, "\u22ee", xPosition + width - 8, yPosition + height / 2 - 4, 0xFFFFFF);
//                DrawUtils.drawOutline(xPosition, yPosition, xPosition + width, yPosition + height, hud.isEnabled() ? 0xFF55FF55 : 0xFFFF5555);
//            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                if (mouseY >= yPosition + height - 20) {
                    hud.toggleEnabled();
                } else {
                    mc.displayGuiScreen(new SettingsScreen(HudsScreen.this, hud));
                }
                return true;
            }
            return false;
        }
    }


}
