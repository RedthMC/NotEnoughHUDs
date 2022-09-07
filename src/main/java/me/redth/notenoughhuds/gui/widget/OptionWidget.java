package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.util.Rectangle;

public abstract class OptionWidget extends GuiButtonExt {
    public static final int HOVERED = 0x2FFFFFFF;
    protected final Rectangle resetButton;
    protected final Rectangle editBox;
    private final NehOption<?> option;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(87, x, y, 328, 16, I18n.format(option.getTranslationKey()));
        this.option = option;
        resetButton = new Rectangle(x + 2, y + height / 2 - 6, 12, 12);
        editBox = new Rectangle(x + width - 102, y + height / 2 - 6, 100, 12);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
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

    public boolean isMouseOver(int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
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
        if (isMouseOver(mouseX, mouseY)) drawRect(xPosition, yPosition, xPosition + width, yPosition + height, HOVERED);
        DrawUtils.drawOutline(resetButton, 0xFFFFFFFF);
        drawCenteredString(mc.fontRendererObj, GuiUtils.UNDO_CHAR, resetButton.getX() + resetButton.getWidth() / 2, resetButton.getY() + resetButton.getHeight() / 2 - 4, 0xFFFFFF);
        mc.fontRendererObj.drawStringWithShadow(displayString, resetButton.getX() + resetButton.getWidth() + 2, yPosition + height / 2.0f - 4, 0xFFFFFF);
        drawEditButton(mc, mouseX, mouseY);
    }

    public abstract void drawEditButton(Minecraft mc, int mouseX, int mouseY);

    public void tick() {

    }

}
