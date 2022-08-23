package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehEnum;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;

public class EnumWidget extends OptionWidget {
    private final NehEnum option;
    private NehEnum.EnumType enumType;

    public EnumWidget(int x, int y, NehEnum option) {
        super(x, y, option);
        this.option = option;
        this.enumType = option.get();
    }

    @Override
    public void onEditClick(int mouseX, int mouseY) {
        enumType = option.next();
    }

    @Override
    public void syncValue() {
        enumType = option.get();
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
        String text = I18n.translate(enumType.getTranslationKey());
        int x1 = editBox.getX() + editBox.getWidth() - mc.textRenderer.getWidth(text);
        int y1 = editBox.getY() + editBox.getHeight() / 2 - 4;
        mc.textRenderer.drawWithShadow(matrix, text, x1, y1, 0xFFFFFF);
    }
}
