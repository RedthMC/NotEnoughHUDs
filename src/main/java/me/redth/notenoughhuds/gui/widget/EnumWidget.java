package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehEnum;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class EnumWidget extends OptionWidget {
    private final NehEnum option;
    private NehEnum.EnumType enumType;

    public EnumWidget(int x, int y, NehEnum option) {
        super(x, y, option);
        this.option = option;
        this.enumType = option.get();
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        enumType = option.next();
    }

    @Override
    public void syncValue() {
        enumType = option.get();
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        String text = I18n.format(enumType.getTranslationKey());
        int x1 = editBox.getX() + editBox.getWidth() - mc.fontRendererObj.getStringWidth(text);
        int y1 = editBox.getY() + editBox.getHeight() / 2 - 4;
        mc.fontRendererObj.drawStringWithShadow(text, x1, y1, 0xFFFFFF);
    }
}
