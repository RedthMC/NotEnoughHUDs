package io.github.reclqtch.notenoughhuds.gui.widget;

import io.github.reclqtch.notenoughhuds.config.option.NehBoolean;
import io.github.reclqtch.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;

public class BooleanWidget extends OptionWidget {
    private final NehBoolean option;
    private boolean value;

    public BooleanWidget(int x, int y, NehBoolean option) {
        super(x, y, option);
        this.option = option;
        value = option.get();
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        value = this.option.toggleValue();
    }

    @Override
    public void syncValue() {
        value = option.get();
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        int x1 = editBox.getX() + editBox.getWidth() - 10;
        int y1 = editBox.getY() + editBox.getHeight() / 2 - 5;
        DrawUtils.drawOutline(x1, y1, x1 + 10, y1 + 10, 0xFFFFFFFF);
        if (value) drawRect(x1 + 2, y1 + 2, x1 + 8, y1 + 8, 0xFFFFFFFF);
    }


}
