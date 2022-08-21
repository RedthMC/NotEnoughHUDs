package io.github.redth.notenoughhuds.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.redth.notenoughhuds.NotEnoughHUDs;
import io.github.redth.notenoughhuds.config.option.NehOption;
import io.github.redth.notenoughhuds.gui.widget.FlatButton;
import io.github.redth.notenoughhuds.gui.widget.GuiList;
import io.github.redth.notenoughhuds.gui.widget.OptionWidget;
import io.github.redth.notenoughhuds.hud.BaseHud;
import io.github.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SettingsScreen extends Screen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    private BaseHud current;
    public GuiList<HudButton> hudList;
    public GuiList<OptionWidget> settings;


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
        addDrawableChild(hudList = new GuiList<>(0, 0, 268, height, "Huds"));
        addDrawableChild(settings = new GuiList<>(hudList.getWidth(), 0, 304, height, ""));

        int i = 0;
        for (BaseHud hud : neh.hudManager.getHuds()) {
            hudList.addEntry(new HudButton((i % 2) * 132 + 4, (i >> 1) * 40 + 16, hud));
            ++i;
        }

        if (current != null) openSettings(current);
    }

    public void openSettings(BaseHud hud) {
        current = hud;
        settings.clearEntries();
        settings.setMessage(Text.translatable(hud.getTranslationKey()));
        int x = settings.x;
        int y = 16;
        for (NehOption<?> option : hud.options) {
            if (option.isHidden()) continue;
            OptionWidget settingWidget = option.getOptionWidget(x, y);
            settings.addEntry(settingWidget);
            y += 16;
        }
    }

    @Override
    public void removed() {
        client.keyboard.setRepeatEvents(false);
        neh.config.save();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (super.mouseScrolled(mouseX, mouseY, amount)) return true;

        if (hudList.isMouseOver(mouseX, mouseY)) hudList.scrollBy((int) amount);
        else if (settings.isMouseOver(mouseX, mouseY)) settings.scrollBy((int) amount);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        setFocused(settings);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        renderBackground(matrix);
        if (current != null) current.renderPlaceholder(matrix, width / 4 * 3, height / 2);
        super.render(matrix, mouseX, mouseY, delta);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (super.charTyped(chr, modifiers)) return true;
        settings.charTyped(chr, modifiers);
        return false;
    }

    @Override
    public void tick() {
        for (OptionWidget o : settings.getEntries()) {
            o.tick();
        }
        if (current != null) current.tick();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (OptionWidget o : settings.getEntries()) o.onMiss();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        setDragging(false);
        return settings.mouseReleased(mouseX, mouseY, button);
    }

    public final class HudButton extends GuiList.GuiListEntry {
        public static final Identifier DEFAULT_ICON = new Identifier("notenoughhuds", "textures/icons/default.png");
        public final BaseHud hud;
        private final Identifier icon;

        public HudButton(int x, int y, BaseHud hud) {
            super(x, y, 128, 36, Text.translatable(hud.getTranslationKey()));
            this.hud = hud;
            this.icon = new Identifier("notenoughhuds", "textures/icons/" + hud.id + ".png");
        }

        @Override
        public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
            fill(matrix, x, y, x + width, y + height, hovered ? 0x80555555 : 0x80000000); // bg

            RenderSystem.setShaderTexture(0, icon);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexture(matrix, x + 2, y + 2, 0, 0, 0, 32, 32, 32, 32);
            RenderSystem.disableBlend();

            drawTextWithShadow(matrix, textRenderer, getMessage(), x + 36, y + 4, 0xFFFFFF);
            fill(matrix, x + width - 16, y + height - 16, x + width, y + height, 0xFF5555FF);
            drawCenteredText(matrix, textRenderer, "...", x + width - 8, y + height - 12, 0xFFFFFF);
            DrawUtils.drawOutline(matrix, x, y, x + width, y + height, hud.isEnabled() ? 0xFF55FF55 : 0xFFFF5555);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX >= x + width - 16 && mouseY >= y + height - 16 && mouseX < x + width && mouseY < y + height) {
                openSettings(hud);
            } else {
                hud.toggleEnabled();
            }
        }

    }
}
