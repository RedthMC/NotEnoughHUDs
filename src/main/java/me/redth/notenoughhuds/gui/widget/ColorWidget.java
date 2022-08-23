package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import org.apache.commons.lang3.StringUtils;

public class ColorWidget extends TextWidget {
    private final Rect2i colorBox;

    public ColorWidget(int x, int y, NehColor option) {
        super(x, y, option, 8, NehColor::isValidChar, NehColor::filterValidChars);
        editBox.setWidth(editBox.getWidth() - editBox.getHeight() - 2);
        colorBox = new Rect2i(editBox.getX() + editBox.getWidth() + 2, editBox.getY(), editBox.getHeight(), editBox.getHeight());
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
        if (visible) {
            DrawUtils.fill(matrix, editBox, background);
            DrawUtils.fill(matrix, colorBox, NehColor.asColor(text));
            int j = selectionStart - firstCharIndex;
            int k = selectionEnd - firstCharIndex;
            String s = tr.trimToWidth(text.substring(firstCharIndex), editBox.getWidth());
            boolean inbound = j >= 0 && j <= s.length();
            int x = editBox.getX() + 2;
            int y = editBox.getY() + editBox.getHeight() / 2;

            tr.drawWithShadow(matrix, "#" + StringUtils.rightPad(text, 8, '0'), x, y - 4, 0xAAAAAA);
            x += tr.getWidth("#");

            int j1 = x;


            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = inbound ? s.substring(0, j) : s;
                j1 = tr.drawWithShadow(matrix, s1, x, y - 4, 0xFFFFFF);
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
                tr.drawWithShadow(matrix, s.substring(j), j1, y - 4, 0xFFFFFF);
            }

            if (isFocused() && focusedTicks / 6 % 2 == 0 && inbound) {
                if (outbound)
                    fill(matrix, k1, y - 5, k1 + 1, y + 5, 0xFFFFFFFF);
                else
                    tr.drawWithShadow(matrix, "_", k1, y - 4, 0xFFFFFF);
            }

            if (k != j) {
                int l1 = x + tr.getWidth(s.substring(0, k));
                drawSelectionHighlight(k1, y - 5, l1 - 1, y + 5);
            }
        }
    }

    @Override
    public void onEditClick(int mouseX, int mouseY) {
        if (this.isFocused() && editBox.contains(mouseX, mouseY)) {
            int i = mouseX - (editBox.getX() + 2 + tr.getWidth("#"));
            String string = tr.trimToWidth(this.text.substring(firstCharIndex), this.editBox.getWidth());
            this.setCursor(tr.trimToWidth(string, i).length() + firstCharIndex);
        }
    }

}
