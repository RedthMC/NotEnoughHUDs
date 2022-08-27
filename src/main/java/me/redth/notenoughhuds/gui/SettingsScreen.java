package me.redth.notenoughhuds.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.gui.widget.FlatButton;
import me.redth.notenoughhuds.gui.widget.GuiList;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class SettingsScreen extends Screen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    private BaseHud current;
    public GuiList<HudButton> hudList;
    public ArrayList<OptionWidget> settings = new ArrayList<>();
//    public GuiList<OptionWidget> settings;


    public SettingsScreen(Screen parent, BaseHud hud) {
        super(Text.of("Settings"));
        this.parent = parent;
        this.current = hud;
    }

    public SettingsScreen(Screen parent) {
        this(parent, null);
    }

    @Override
    protected void init() {
        client.keyboard.setRepeatEvents(true);
        addDrawableChild(new FlatButton(width / 2 - 100, height - 22, 200, 20, "back", b -> client.setScreenAndRender(parent)));
        addDrawableChild(hudList = new GuiList<>(width / 2 - 256, 0, 124, height));
//        addDrawableChild(settings = new GuiList<>(hudList.getWidth(), 0, 304, height, ""));

        int y = 16;
        for (BaseHud hud : neh.hudManager.getHuds()) {
            hudList.addEntry(new HudButton(hudList.x, y, hud));
            y += 32;
        }

        if (current != null) loadSettings();
    }

//    public void openSettings(BaseHud hud) {
//        current = hud;
//        settings.clearEntries();
//        settings.setMessage(Text.translatable(hud.getTranslationKey()));
//        int x = settings.x;
//        int y = 16;
//        for (NehOption<?> option : hud.options) {
//            if (option.isHidden()) continue;
//            OptionWidget settingWidget = option.getOptionWidget(x, y);
//            settings.addEntry(settingWidget);
//            y += 16;
//        }
//    }

    public void loadSettings() {
        for (OptionWidget setting : settings) {
            remove(setting);
        }
        settings.clear();
        int x = width / 2 - 128;
        int y = height / 2;
        for (NehOption<?> option : current.options) {
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

//    @Override
//    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
//        setFocused(settings);
//        return super.keyPressed(keyCode, scanCode, modifiers);
//    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        renderBackground(matrix);
        if (current != null) current.renderPlaceholder(matrix, width / 2 + 36, height / 4);
        super.render(matrix, mouseX, mouseY, delta);
    }

//    @Override
//    public boolean charTyped(char chr, int modifiers) {
//        if (super.charTyped(chr, modifiers)) return true;
//        settings.charTyped(chr, modifiers);
//        return false;
//    }

    @Override
    public void tick() {
//        for (OptionWidget o : settings.getEntries()) {
//            o.tick();
//        }
        if (current != null && !current.isEnabled()) current.tick();
    }

//    @Override
//    public boolean mouseClicked(double mouseX, double mouseY, int button) {
//        for (OptionWidget o : settings.getEntries()) o.onMiss();
//        return super.mouseClicked(mouseX, mouseY, button);
//    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        hudList.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
//        return settings.mouseReleased(mouseX, mouseY, button);
    }

    public final class HudButton extends GuiList.GuiListEntry {
        public final BaseHud hud;
        private final Identifier icon;

        public HudButton(int x, int y, BaseHud hud) {
            super(x, y, 120, 28, hud.getTranslated());
            this.hud = hud;
            this.icon = new Identifier("notenoughhuds", "textures/icons/" + hud.id + ".png");
        }

        @Override
        public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
            fill(matrix, x, y, x + width, y + height, hovered ? 0x80555555 : 0x80000000); // bg

            RenderSystem.setShaderTexture(0, icon);
            DrawUtils.resetGl();
            drawTexture(matrix, x + 4, y + 4, 0, 0, 0, 20, 20, 20, 20);
            RenderSystem.disableBlend();

            int y1 = y + 4;
            for (String s : getMessage().getString().split(" ")) {
                mc.textRenderer.drawWithShadow(matrix, s, x + 28, y1, 0xFFFFFF);
                y1 += 9;
            }

//            drawTextWithShadow(matrix, textRenderer, getMessage(), x + 28, y + 4, 0xFFFFFF);
            fill(matrix, x + width - 16, y, x + width, y + height, 0xFF5555FF);
            drawCenteredText(matrix, textRenderer, "\u22ee", x + width - 8, y + height / 2 - 4, 0xFFFFFF);
            DrawUtils.drawOutline(matrix, x, y, x + width, y + height, hud.isEnabled() ? 0xFF55FF55 : 0xFFFF5555);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX >= x + width - 16) {
                current = hud;
                loadSettings();
            } else {
                hud.toggleEnabled();
            }
        }

    }
}
