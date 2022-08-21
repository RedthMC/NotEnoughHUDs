package io.github.redth.notenoughhuds.gui.widget;

import io.github.redth.notenoughhuds.config.option.NehOption;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;

public abstract class OptionWidget extends GuiList.GuiListEntry {
    public static final int HOVERED = 0x80FFFFFF;
    protected final Rect2i editBox;
    protected final Rect2i resetButton;
    private final NehOption<?> option;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(x, y, 300, 16, Text.translatable(option.getTranslationKey()));
        this.option = option;
        editBox = new Rect2i(x + width - 116, y + height / 2 - 6, 100, 12);
        resetButton = new Rect2i(x + width - 14, y + height / 2 - 6, 12, 12);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (resetButton.contains((int) mouseX, (int) mouseY)) {
            option.reset();
            syncValue();
            setFocused(false);
            onMiss();
        } else {
            setFocused(true);
            onEditClick((int) mouseX, (int) mouseY);
        }
    }

    public void onEditClick(int mouseX, int mouseY) {
    }

    public void syncValue() {
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        if (hovered) fill(matrix, x, y, x + width, y + height, HOVERED);
        drawTextWithShadow(matrix, mc.textRenderer, getMessage(), x + 2, y + height / 2 - 4, 0xFFFFFF);
        drawCenteredText(matrix, mc.textRenderer, "\u21B6", resetButton.getX() + resetButton.getWidth() / 2, resetButton.getY() + resetButton.getHeight() / 2 - 4, 0xFFFFFF);
        drawEditButton(matrix, mouseX, mouseY);
    }

    public abstract void drawEditButton(MatrixStack matrix, int mouseX, int mouseY);

    public void tick() {

    }

    public void onMiss() {

    }

}
