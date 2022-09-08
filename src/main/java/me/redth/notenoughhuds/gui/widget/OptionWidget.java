package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;

public abstract class OptionWidget extends ClickableWidget {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final int HOVERED = 0x80FFFFFF;
    protected final Rect2i editBox;
    protected final Rect2i resetButton;
    private final NehOption<?> option;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(x, y, 328, 16, Text.translatable(option.getTranslationKey()));
        this.option = option;
        resetButton = new Rect2i(x + 2, y + height / 2 - 6, 12, 12);
        editBox = new Rect2i(x + width - 102, y + height / 2 - 6, 100, 12);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (active && this.visible && isValidClickButton(button)) {
            if (clicked(mouseX, mouseY)) {
                if (resetButton.contains((int) mouseX, (int) mouseY)) {
                    option.reset();
                    syncValue();
                    onMiss();
                } else {
                    playDownSound(MinecraftClient.getInstance().getSoundManager());
                    onClick(mouseX, mouseY);
                }
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
        DrawUtils.drawOutline(matrix, resetButton, 0xFFFFFFFF);
        drawTextWithShadow(matrix, mc.textRenderer, getMessage(), resetButton.getX() + resetButton.getWidth() + 2, y + 2, 0xFFFFFF);
        drawCenteredText(matrix, mc.textRenderer, "\u21B6", resetButton.getX() + resetButton.getWidth() / 2, resetButton.getY() + resetButton.getHeight() / 2 - 4, 0xFFFFFF);
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
