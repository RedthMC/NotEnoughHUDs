package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;

public class SettingsScreen extends GuiScreen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final GuiScreen parent;
    public final BaseHud hud;
    public ArrayList<OptionWidget> settings = new ArrayList<>();

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public SettingsScreen(GuiScreen parent, BaseHud hud) {
        this.parent = parent;
        this.hud = hud;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        buttonList.add(new GuiButtonExt(1, width / 2 - 105, height - 22, 100, 20, "\u00a7cReset"));
        buttonList.add(new GuiButtonExt(0, width / 2 + 5, height - 22, 100, 20, "Back"));

        for (OptionWidget setting : settings) {
            buttonList.remove(setting);
        }
        settings.clear();
        int x = width / 2 - 128;
        int y = height / 2;
        for (NehOption<?> option : hud.options) {
            if (option.isHidden()) continue;
            OptionWidget settingWidget = option.getOptionWidget(x, y);
            buttonList.add(settingWidget);
            settings.add(settingWidget);
            y += 16;
        }
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
        } else if (button.id == 1) {
            hud.reset();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        hud.renderPlaceholder(width / 2, height / 4);
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
        if (!hud.isEnabled()) hud.tick();
    }

}
