package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.client.config.GuiUtils;
import org.lwjgl.util.Rectangle;

public abstract class OptionWidget extends GuiButtonExt {
    public static final int HOVERED = 0x33FFFFFF;
    protected final Rectangle editBox;

    public OptionWidget(int x, int y, NehOption<?> option) {
        super(87, x, y, 256, 16, I18n.format(option.getTranslationKey()));
        editBox = new Rectangle(x + width - 102, y + height / 2 - 6, 100, 12);
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (isMouseOver(mouseX, mouseY)) {
            onClick(mouseX, mouseY);
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
        drawString(mc.fontRendererObj, displayString, xPosition + 4, yPosition + height / 2 - 4, 0xFFFFFF);
        drawEditButton(mc, mouseX, mouseY);
    }

    public abstract void drawEditButton(Minecraft mc, int mouseX, int mouseY);

    public void tick() {

    }

}
