package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class SettingsScreen extends Screen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    private final BaseHud hud;
    public ArrayList<OptionWidget> settings = new ArrayList<>();


    public SettingsScreen(Screen parent, BaseHud hud) {
        super(Text.of("Settings"));
        this.parent = parent;
        this.hud = hud;
    }

    @Override
    protected void init() {
        client.keyboard.setRepeatEvents(true);

        addDrawableChild(new ButtonWidget(width / 2 - 105, height - 22, 100, 20, Text.translatable("controls.reset").formatted(Formatting.RED), b -> hud.reset()));
        addDrawableChild(new ButtonWidget(width / 2 + 5, height - 22, 100, 20, ScreenTexts.DONE, b -> client.setScreenAndRender(parent)));

        for (OptionWidget setting : settings) {
            remove(setting);
        }
        settings.clear();
        int x = width / 2 - 128;
        int y = height / 2;
        for (NehOption<?> option : hud.options) {
            if (option.isHidden()) continue;
            OptionWidget setting = option.getOptionWidget(x, y);
            addDrawableChild(setting);
            settings.add(setting);
            y += 16;
        }
    }

    @Override
    public void removed() {
        client.keyboard.setRepeatEvents(false);
        neh.config.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        renderBackground(matrix);
        hud.renderPlaceholder(matrix, width / 2, height / 4);
        super.render(matrix, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Element element : children()) {
            if (element.mouseClicked(mouseX, mouseY, button))
                this.setFocused(element);
        }
//        if (button == 0) {
//            this.setDragging(true);
//        }

        return true;
    }

    @Override
    public void tick() {
//        for (OptionWidget o : settings.getEntries()) {
//            o.tick();
//        }
        for (OptionWidget setting : settings) {
            setting.tick();
        }
        if (!hud.isEnabled()) hud.tick();
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (OptionWidget setting : settings) {
            setting.onRelease(mouseX, mouseY);
        }
        return true;
    }
}
