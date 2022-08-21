package io.github.reclqtch.notenoughhuds.gui.widget;

import io.github.reclqtch.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class IntegerWidget extends OptionWidget {
    private final NehInteger option;
    private final int range;
    private boolean dragging;
    private int value;
    private int thumbOffset;

    public IntegerWidget(int x, int y, NehInteger option) {
        super(x, y, option);
        this.option = option;
        range = option.max - option.min;
        value = option.get();
        thumbOffset = getThumbOffset();
    }


    public int getIntValue() {
        return thumbOffset * range / editBox.getWidth() + option.min;
    }

    public int getThumbOffset() {
        return (value - option.min) * editBox.getWidth() / range;
    }

    private void setValueFromMouse(double d) {
        setValue((int) d - editBox.getX());
    }

    private void setValue(int d) {
        thumbOffset = MathHelper.clamp_int(d, 0, editBox.getWidth());
        value = getIntValue();
        option.set(value);
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        if (editBox.contains(mouseX, mouseY)) {
            setValueFromMouse(mouseX);
            dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = false;
    }

    @Override
    public void syncValue() {
        value = option.get();
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        if (dragging) setValueFromMouse(mouseX);
        thumbOffset = getThumbOffset();
        int x1 = editBox.getX();
        int y1 = editBox.getY() + editBox.getHeight() / 2;
        String label = String.valueOf(value);
        if (option.unit != null) label += " " + option.unit;
        mc.fontRendererObj.drawStringWithShadow(label, x1 - mc.fontRendererObj.getStringWidth(label) - 4, y1 - 4, 0xFFFFFF);
        drawRect(x1, y1 - 2, x1 + editBox.getWidth(), y1 + 2, 0xFFFFFFFF);
        int thumbX = x1 + thumbOffset;
        drawRect(thumbX - 3, y1 - 6, thumbX + 3, y1 + 6, 0xFFFFFFFF);
    }
}
