package io.github.reclqtch.notenoughhuds.gui.widget;

import io.github.reclqtch.notenoughhuds.config.option.NehColor;
import io.github.reclqtch.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.util.Rectangle;

public class ColorWidget extends TextWidget {
    private final NehColor option;
    private final Rectangle colorBox;

    public ColorWidget(int x, int y, NehColor option) {
        super(x, y, option, 8, NehColor::isValidChar, NehColor::filterValidChars);
        this.option = option;
        editBox.setWidth(editBox.getWidth() - editBox.getHeight() - 2);
        colorBox = new Rectangle(editBox.getX() + editBox.getWidth() + 2, editBox.getY(), editBox.getHeight(), editBox.getHeight());
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            DrawUtils.drawRect(editBox, background);
            DrawUtils.drawRect(colorBox, option.asInt());
            int j = selectionStart - firstCharIndex;
            int k = selectionEnd - firstCharIndex;
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), editBox.getWidth());
            boolean inbound = j >= 0 && j <= s.length();
            int x = editBox.getX() + 2;
            int y = editBox.getY() + editBox.getHeight() / 2;

            fr.drawStringWithShadow("#" + StringUtils.rightPad(text, 8, '0'), x, y - 4, 0xAAAAAA);
            x += fr.getCharWidth('#');

            int j1 = x;


            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = inbound ? s.substring(0, j) : s;
                j1 = fr.drawStringWithShadow(s1, x, y - 4, 0xFFFFFF);
            }

            boolean outbound = selectionStart < text.length() || text.length() >= maxLength;
            int k1 = j1;

            if (!inbound) {
                k1 = j > 0 ? x + editBox.getWidth() : x;
            } else if (outbound) {
                k1 = j1 - 1;
                --j1;
            }

            if (s.length() > 0 && inbound && j < s.length()) {
                fr.drawStringWithShadow(s.substring(j), j1, y - 4, 0xFFFFFF);
            }

            if (textFieldFocused && focusedTicks / 6 % 2 == 0 && inbound) {
                if (outbound) {
                    drawRect(k1, y - 5, k1 + 1, y + 5, -3092272);
                } else {
                    fr.drawStringWithShadow("_", k1, y - 4, 0xFFFFFF);
                }
            }

            if (k != j) {
                int l1 = x + fr.getStringWidth(s.substring(0, k));
                highlight(k1, y - 5, l1 - 1, y + 5);
            }
        }
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        boolean flag = editBox.contains(mouseX, mouseY);

        setFocused(flag);

        if (textFieldFocused && flag) {
            int i = mouseX - (editBox.getX() + 2 + fr.getCharWidth('#'));
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), editBox.getWidth());
            setSelectionStart(fr.trimStringToWidth(s, i).length() + firstCharIndex);
        }
    }
}
