package io.github.redth.notenoughhuds.gui.widget;

import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.util.math.MatrixStack;

public class BooleanWidget extends OptionWidget {
    private final NehBoolean option;
    private boolean value;

    public BooleanWidget(int x, int y, NehBoolean option) {
        super(x, y, option);
        this.option = option;
        value = option.get();
    }

    @Override
    public void onEditClick(int mouseX, int mouseY) {
        value = this.option.toggleValue();
    }

    @Override
    public void syncValue() {
        value = option.get();
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
        int x1 = editBox.getX() + editBox.getWidth() - 10;
        int y1 = editBox.getY() + editBox.getHeight() / 2 - 5;
        DrawUtils.drawOutline(matrix, x1, y1, x1 + 10, y1 + 10, 0xFFFFFFFF);
        if (value) fill(matrix, x1 + 2, y1 + 2, x1 + 8, y1 + 8, 0xFFFFFFFF);
    }
}
