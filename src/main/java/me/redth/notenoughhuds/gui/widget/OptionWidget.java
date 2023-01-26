package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;

public abstract class OptionWidget extends ClickableWidget {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final int HOVERED = 0x33FFFFFF;
    protected final Rect2i editBox;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(x, y, 256, 16, Text.translatable(option.getTranslationKey()));
        editBox = new Rect2i(x + width - 102, y + height / 2 - 6, 100, 12);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && this.visible && isValidClickButton(button)) {
            if (clicked(mouseX, mouseY)) {
                playDownSound(MinecraftClient.getInstance().getSoundManager());
                onClick(mouseX, mouseY);
                return true;
            }
            onMiss();
        }
        return false;
    }

    public void syncValue() {
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        if (hovered) fill(matrix, x, y, x + width, y + height, HOVERED);
        drawTextWithShadow(matrix, mc.textRenderer, getMessage(), x + 4, y + height / 2 - 4, 0xFFFFFF);
        drawEditButton(matrix, mouseX, mouseY);
    }

    public abstract void drawEditButton(MatrixStack matrix, int mouseX, int mouseY);

    public void tick() {

    }

    public void onMiss() {

    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {

    }
}
