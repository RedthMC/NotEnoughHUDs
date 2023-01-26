package me.redth.notenoughhuds.gui;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.gui.widget.GuiList;
import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class HudsScreen extends Screen {
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    private final Screen parent;
    public GuiList<HudButton> hudList;

    public HudsScreen(Screen parent) {
        super(Text.of("HUDs"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        addDrawableChild(hudList = new GuiList<>(this, width / 2 - 188, 20, 390, height - 44));

        int x = hudList.x;
        int y = hudList.y + 8;
        for (BaseHud hud : neh.hudManager.getHuds()) {
            hudList.addEntry(new HudsScreen.HudButton(x, y, hud));
            x += 128; // spacing 8

            if (x + 128 > hudList.x + hudList.getWidth()) {
                x = hudList.x;
                y += 88;
            }
        }

        addDrawableChild(new ButtonWidget(width / 2 - 100, height - 22, 200, 20, ScreenTexts.DONE, b -> client.setScreenAndRender(parent)));
    }

    @Override
    public void removed() {
        neh.config.save();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void render(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        renderBackground(matrix);
        super.render(matrix, mouseX, mouseY, delta);
        drawCenteredText(matrix, textRenderer, title, width / 2, 6, 0xFFFFFFFF);
    }




    public final class HudButton extends GuiList.GuiListEntry {
        public static final Identifier HUD_BUTTON = new Identifier("notenoughhuds", "textures/hud_button.png");
        public final BaseHud hud;
        private final Identifier icon;

        public HudButton(int x, int y, BaseHud hud) {
            super(x, y, 120, 80, hud.getTranslated());
            this.hud = hud;
            this.icon = new Identifier("notenoughhuds", "textures/icons/" + hud.id + ".png");
        }

        @Override
        public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
            DrawUtils.drawTexture(matrix, HUD_BUTTON, x, y, hud.isEnabled() ? 120 : 0, hovered ? 80 : 0, 120, 80, 240, 160);

            DrawUtils.drawTexture(matrix, icon, x + width / 2 - 16, y + 6, 0, 0, 32, 32, 32, 32);

            matrix.push();
            matrix.translate(x + width / 2F, y + 43, 0);
            matrix.scale(1.5f, 1.5f, 1.5f);
            drawCenteredText(matrix, textRenderer, getMessage(), 0, 0, 0xFFFFFFFF);
            matrix.pop();
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseY >= y + height - 20) {
                hud.toggleEnabled();
            } else {
                client.setScreenAndRender(new SettingsScreen(HudsScreen.this, hud));
            }
        }

    }
}
