package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.gui.widget.GuiList;
import me.redth.notenoughhuds.hud.Hud;
import me.redth.notenoughhuds.util.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class HudsScreen extends EditingScreen {
    public GuiList<HudButton> hudList;

    public HudsScreen(GuiScreen parent) {
        super(parent);
    }

    @Override
    public void initGui() {

        buttonList.add(hudList = new GuiList<>(this, 1, width / 2 - 188, 20, 390, height - 44));

        int x = hudList.xPosition;
        int y = hudList.yPosition + 8;
        for (Hud hud : neh.hudManager.getHuds()) {
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
            displayParent();
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
        public final Hud hud;


        public HudButton(int x, int y, Hud hud) {
            super(x, y, 120, 80, I18n.format(hud.getTranslationKey()));
            this.hud = hud;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
                hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
                boolean switchHovered = mouseY >= yPosition + height - 20;

                DrawUtils.drawTexture(HUD_BUTTON, xPosition, yPosition, (hovered && !switchHovered) ? 120 : 0, 0, 120, 60, 240, 100);
                DrawUtils.drawTexture(HUD_BUTTON, xPosition, yPosition + 60, (hovered && switchHovered) ? 120 : 0, hud.isEnabled() ? 80 : 60, 120, 20, 240, 100);

                DrawUtils.drawTexture(hud.icon, xPosition + width / 2 - 16, yPosition + 6, 0, 0, 32, 32, 32, 32);

                GlStateManager.pushMatrix();
                GlStateManager.translate(xPosition + width / 2F, yPosition + 43, 0);
                GlStateManager.scale(1.5f, 1.5f, 1.5f);
                drawCenteredString(fontRendererObj, displayString, 0, 0, 0xFFFFFFFF);
                GlStateManager.popMatrix();

            }
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
