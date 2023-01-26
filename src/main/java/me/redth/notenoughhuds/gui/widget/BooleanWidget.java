package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;

public class BooleanWidget extends OptionWidget {
    private final NehBoolean option;
    private boolean value;

    public BooleanWidget(int x, int y, NehBoolean option) {
        super(x, y, option);
        this.option = option;
        value = option.get();
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        value = this.option.toggleValue();
    }

    @Override
    public void syncValue() {
        value = option.get();
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {

        int x1 = editBox.getX() + editBox.getWidth() - 12;
        int y1 = editBox.getY() + editBox.getHeight() / 2 - 6;
        DrawUtils.drawOutline(matrix, x1, y1, x1 + 12, y1 + 12, 0xFFFFFFFF);
        if (value) fill(matrix, x1 + 2, y1 + 2, x1 + 10, y1 + 10, 0xFFFFFFFF);

    }
}
