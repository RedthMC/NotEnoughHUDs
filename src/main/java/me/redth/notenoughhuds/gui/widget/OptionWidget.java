package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.util.Rectangle;

public abstract class OptionWidget extends GuiButtonExt {
    public static final int HOVERED = 0x80FFFFFF;
    protected final Rectangle editBox;
    protected final Rectangle resetButton;
    private final NehOption<?> option;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(87, x, y, 328, 16, I18n.format(option.getTranslationKey()));
        this.option = option;
        editBox = new Rectangle(x + width - 116, y + height / 2 - 6, 100, 12);
        resetButton = new Rectangle(x + width - 14, y + height / 2 - 6, 12, 12);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            if (resetButton.contains(mouseX, mouseY)) {
                option.reset();
                syncValue();
                onMissClick(mouseX, mouseY);
            } else {
                onClick(mouseX, mouseY);
            }
            return true;
        }
        onMissClick(mouseX, mouseY);
        return false;
    }

    public void onClick(int mouseX, int mouseY) {
    }

    public void onMissClick(int mouseX, int mouseY) {
    }

    public void syncValue() {

    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        if (hovered) drawRect(xPosition, yPosition, xPosition + width, yPosition + height, HOVERED);
        mc.fontRendererObj.drawStringWithShadow(displayString, xPosition + 2, yPosition + height / 2 - 4, 0xFFFFFF);
        drawCenteredString(mc.fontRendererObj, GuiUtils.UNDO_CHAR, resetButton.getX() + resetButton.getWidth() / 2, resetButton.getY() + resetButton.getHeight() / 2 - 4, 0xFFFFFF);
        drawEditButton(mc, mouseX, mouseY);
    }

    public abstract void drawEditButton(Minecraft mc, int mouseX, int mouseY);

    public void tick() {

    }

}
