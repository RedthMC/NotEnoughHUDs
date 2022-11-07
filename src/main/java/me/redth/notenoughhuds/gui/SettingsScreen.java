package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.gui.widget.GuiList;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsScreen extends GuiScreen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    public BaseHud current;
    public ArrayList<OptionWidget> settings = new ArrayList<>();
    public GuiList<HudButton> hudList;
//    public GuiList<OptionWidget> settings;


    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public SettingsScreen(GuiScreen parent, BaseHud hud) {
        this.parent = parent;
        current = hud;
    }

    public SettingsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.add(new GuiButtonExt(0, width / 2 - 100, height - 22, 200, 20, "Back"));
        buttonList.add(hudList = new GuiList<>(1, width / 2 - 256, 0, 124, height));
//        buttonList.add(settings = new GuiList<>(2, width / 2 - 128, 150, 384, height - 384));

        int y = 16;
        for (BaseHud hud : neh.hudManager.getHuds()) {
            hudList.addEntry(new HudButton(hudList.xPosition, y, hud));
            y += 32;
        }

        if (current != null) loadSettings();
    }

    public void loadSettings() {
        for (OptionWidget setting : settings) {
            buttonList.remove(setting);
        }
        settings.clear();
        int x = width / 2 - 128;
        int y = height / 2;
        for (NehOption<?> option : current.options) {
            if (option.isHidden()) continue;
            OptionWidget settingWidget = option.getOptionWidget(x, y);
            buttonList.add(settingWidget);
            settings.add(settingWidget);
            y += 16;
        }
//        settings.clearEntries();
//        settings.displayString = I18n.format(current.getTranslationKey());
//        int x = settings.xPosition;
//        int y = 150;
//        for (NehOption<?> option : current.options) {
//            if (option.isHidden()) continue;
//            OptionWidget settingWidget = option.getOptionWidget(x, y);
//            settings.addEntry(settingWidget);
//            y += 16;
//        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
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
//            else if (settings.isMouseOver()) settings.scrollBy(scroll);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);
        if (current != null) current.renderPlaceholder(width / 2 + 36, height / 4);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (OptionWidget option : settings) {
            option.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public void updateScreen() {
        for (GuiButton b : buttonList) {
            if (b instanceof OptionWidget) {
                ((OptionWidget) b).tick();
            }
        }
//        for (OptionWidget o : settings.getEntries()) {
//            o.tick();
//        }
    }


    public final class HudButton extends GuiList.GuiListEntry {
        public final BaseHud hud;

        public HudButton(int x, int y, BaseHud hud) {
            super(x, y, 120, 28, I18n.format(hud.getTranslationKey()));
            this.hud = hud;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (visible) {
                hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
                mouseDragged(mc, mouseX, mouseY);

                if (hovered) drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0x2FFFFFFF); // bg

                DrawUtils.drawScaledTexture(hud.icon, xPosition + 4, yPosition + 4, 0, 0, 20, 20, 20, 20);

                int y1 = yPosition + 4;
                for (String s : displayString.split(" ")) {
                    drawString(mc.fontRendererObj, s, xPosition + 28, y1, 0xFFFFFF);
                    y1 += 9;
                }

                drawRect(xPosition + width - 16, yPosition, xPosition + width, yPosition + height, 0xFF5555FF);
                drawCenteredString(mc.fontRendererObj, "\u22ee", xPosition + width - 8, yPosition + height / 2 - 4, 0xFFFFFF);
                DrawUtils.drawOutline(xPosition, yPosition, xPosition + width, yPosition + height, hud.isEnabled() ? 0xFF55FF55 : 0xFFFF5555);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                if (mouseX >= xPosition + width - 16) {
                    current = hud;
                    loadSettings();
                } else {
                    hud.toggleEnabled();
                }
                return true;
            }
            return false;
        }
    }


}
