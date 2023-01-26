package me.redth.notenoughhuds.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class TextWidget extends OptionWidget {
    protected static final TextRenderer tr = mc.textRenderer;
    protected static final int background = 0x80000000;
    protected final NehOption<String> option;
    protected final int maxLength;
    protected final Predicate<Character> charValidator;
    protected final Function<String, String> stringFilter;
    protected String text;
    protected int focusedTicks;
    protected int selectionStart;
    protected int selectionEnd;
    protected int firstCharIndex;
    protected boolean selecting;

    public TextWidget(int x, int y, NehOption<String> option, int maxLength, Predicate<Character> charValidator, Function<String, String> stringFilter) {
        super(x, y, option);
        this.option = option;
        this.maxLength = maxLength;
        this.charValidator = charValidator;
        this.stringFilter = stringFilter;
        this.text = option.get();
    }

    @Override
    public void syncValue() {
        text = option.get();
        selecting = false;
        setCursor(0);
    }

    @Override
    public void onMiss() {
        setFocused(false);
        syncValue();
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
        if (visible) {
            DrawUtils.fill(matrix, editBox, background);
            int j = selectionStart - firstCharIndex;
            int k = selectionEnd - firstCharIndex;
            String s = tr.trimToWidth(text.substring(firstCharIndex), editBox.getWidth());
            boolean inbound = j >= 0 && j <= s.length();
            int x = editBox.getX() + 2;
            int y = editBox.getY() + editBox.getHeight() / 2;
            int j1 = x;

            if (k > s.length()) {
                k = s.length();
            }

            if (s.length() > 0) {
                String s1 = inbound ? s.substring(0, j) : s;
                j1 = tr.drawWithShadow(matrix, s1, x, y - 4, 0xFFFFFFFF);
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
                    fill(matrix, k1 - 1, y - 6, k1, y + 6, 0xFFFFFFFF);
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
    public void tick() {
        ++focusedTicks;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        if (editBox.contains((int) mouseX, (int) mouseY)) {
            setFocused(true);
            int i = (int) mouseX - editBox.getX();
            String string = tr.trimToWidth(text.substring(firstCharIndex), editBox.getWidth());
            setCursor(tr.trimToWidth(string, i).length() + firstCharIndex);
        }
    }


//    @Override
//    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
//        DrawUtils.fill(matrix, editBox, background);
//        int startToFirst = selectionStart - firstCharIndex;
//        int endToFirst = selectionEnd - firstCharIndex;
//        String string = tr.trimToWidth(text.substring(firstCharIndex), editBox.getWidth());
//        boolean bl = startToFirst >= 0 && startToFirst <= string.length();
//        boolean flashCursor = isFocused() && focusedTicks / 6 % 2 == 0 && bl;
//        int boxX = editBox.getX();
//        int boxY = editBox.getY();
//        int n = boxX;
//        if (endToFirst > string.length()) {
//            endToFirst = string.length();
//        }
//
//        if (!string.isEmpty()) {
//            String string2 = bl ? string.substring(0, startToFirst) : string;
//            n = tr.drawWithShadow(matrix, string2, boxX, boxY, 0xFFFFFF);
//        }
//
//        boolean bl3 = selectionStart < text.length() || text.length() >= maxLength;
//        int o = n;
//        if (!bl) {
//            o = startToFirst > 0 ? boxX + editBox.getWidth() : boxX;
//        } else if (bl3) {
//            o = --n;
//        }
//
//        if (!string.isEmpty() && bl && startToFirst < string.length()) {
//            tr.drawWithShadow(matrix, string.substring(startToFirst), n, boxY, 0xFFFFFF);
//        }
//
//        if (flashCursor) {
//            if (bl3) {
//                fill(matrix, o, boxY - 1, o + 1, boxY + 1 + 9, 0xFFFFFFFF);
//            } else {
//                tr.drawWithShadow(matrix, "_", o, boxY, 0xFFFFFF);
//            }
//        }
//
//        if (endToFirst != startToFirst) {
//            int p = boxX + tr.getWidth(string.substring(0, endToFirst));
//            drawSelectionHighlight(o, boxY - 1, p - 1, boxY + 1 + 9);
//        }
//    }

    public String getSelectedText() {
        int i = Math.min(selectionStart, selectionEnd);
        int j = Math.max(selectionStart, selectionEnd);
        return text.substring(i, j);
    }

    public void write(String text) {
        int i = Math.min(selectionStart, selectionEnd);
        int j = Math.max(selectionStart, selectionEnd);
        int k = maxLength - this.text.length() - (i - j);
        String string = stringFilter.apply(text);
        int l = string.length();
        if (k < l) {
            string = string.substring(0, k);
            l = k;
        }

        this.text = new StringBuilder(this.text).replace(i, j, string).toString();
        setSelectionStart(i + l);
        setSelectionEnd(selectionStart);
        option.set(this.text);
    }


    private void erase(int offset) {
        if (Screen.hasControlDown()) {
            eraseWords(offset);
        } else {
            eraseCharacters(offset);
        }

    }

    public void eraseWords(int wordOffset) {
        if (!text.isEmpty()) {
            if (selectionEnd != selectionStart) {
                write("");
            } else {
                eraseCharacters(getWordSkipPosition(wordOffset) - selectionStart);
            }
        }
    }

    public void eraseCharacters(int characterOffset) {
        if (!text.isEmpty()) {
            if (selectionEnd != selectionStart) {
                write("");
            } else {
                int i = getCursorPosWithOffset(characterOffset);
                int j = Math.min(i, selectionStart);
                int k = Math.max(i, selectionStart);
                if (j != k) {
                    text = new StringBuilder(text).delete(j, k).toString();
                    setCursor(j);
                }
            }
        }
    }

    public int getWordSkipPosition(int wordOffset) {
        return getWordSkipPosition(wordOffset, selectionStart);
    }

    private int getWordSkipPosition(int wordOffset, int cursorPosition) {
        int i = cursorPosition;
        boolean bl = wordOffset < 0;
        int j = Math.abs(wordOffset);

        for (int k = 0; k < j; ++k) {
            if (!bl) {
                int l = text.length();
                i = text.indexOf(32, i);
                if (i == -1) {
                    i = l;
                } else {
                    while (i < l && text.charAt(i) == ' ') {
                        ++i;
                    }
                }
            } else {
                while (i > 0 && text.charAt(i - 1) == ' ') {
                    --i;
                }

                while (i > 0 && text.charAt(i - 1) != ' ') {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursor(int offset) {
        setCursor(getCursorPosWithOffset(offset));
    }

    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(text, selectionStart, offset);
    }

    public void setCursor(int cursor) {
        setSelectionStart(cursor);
        if (!selecting) {
            setSelectionEnd(selectionStart);
        }
    }

    public void setSelectionStart(int cursor) {
        selectionStart = MathHelper.clamp(cursor, 0, text.length());
    }

    public void setCursorToEnd() {
        setCursor(text.length());
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isActive())
            return false;

        selecting = Screen.hasShiftDown();
        if (Screen.isSelectAll(keyCode)) {
            setCursorToEnd();
            setSelectionEnd(0);
            return true;
        } else if (Screen.isCopy(keyCode)) {
            mc.keyboard.setClipboard(getSelectedText());
            return true;
        } else if (Screen.isPaste(keyCode)) {
            if (active) {
                write(mc.keyboard.getClipboard());
            }

            return true;
        } else if (Screen.isCut(keyCode)) {
            mc.keyboard.setClipboard(getSelectedText());
            if (active) {
                write("");
            }

            return true;
        } else {
            switch (keyCode) {
                case 259:
                    if (active) {
                        selecting = false;
                        erase(-1);
                        selecting = Screen.hasShiftDown();
                    }
                    return true;
                case 260:
                case 264:
                case 265:
                case 266:
                case 267:
                default:
                    return false;
                case 261:
                    if (active) {
                        selecting = false;
                        erase(1);
                        selecting = Screen.hasShiftDown();
                    }
                    return true;
                case 262:
                    if (Screen.hasControlDown()) {
                        setCursor(getWordSkipPosition(1));
                    } else {
                        moveCursor(1);
                    }

                    return true;
                case 263:
                    if (Screen.hasControlDown()) {
                        setCursor(getWordSkipPosition(-1));
                    } else {
                        moveCursor(-1);
                    }
                    return true;
                case 268:
                    setCursor(0);
                    return true;
                case 269:
                    setCursorToEnd();
                    return true;
            }
        }
    }

    public boolean isActive() {
        return visible && isFocused() && active;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (!isActive()) {
            return false;
        } else if (charValidator.test(chr)) {
            if (active) {
                write(Character.toString(chr));
            }
            return true;
        } else {
            return false;
        }
    }

    protected void drawSelectionHighlight(int x1, int y1, int x2, int y2) {
         int i;
         if (x1 < x2) {
             i = x1;
             x1 = x2;
             x2 = i;
         }

         if (y1 < y2) {
             i = y1;
             y1 = y2;
             y2 = i;
         }

         if (x2 > editBox.getX() + editBox.getWidth()) {
             x2 = editBox.getX() + editBox.getWidth();
         }

         if (x1 > editBox.getX() + editBox.getWidth()) {
             x1 = editBox.getX() + editBox.getWidth();
         }

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(0.0F, 0.0F, 1.0F, 1.0F);
        RenderSystem.disableTexture();
        RenderSystem.enableColorLogicOp();
        RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        bufferBuilder.vertex(x1, y2, 0.0D).next();
        bufferBuilder.vertex(x2, y2, 0.0D).next();
        bufferBuilder.vertex(x2, y1, 0.0D).next();
        bufferBuilder.vertex(x1, y1, 0.0D).next();
        tessellator.draw();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableColorLogicOp();
        RenderSystem.enableTexture();
    }

    @Override
    public boolean changeFocus(boolean lookForwards) {
        return visible && active && super.changeFocus(lookForwards);
    }

    @Override
    protected void onFocusedChanged(boolean newFocused) {
        if (newFocused) focusedTicks = 0;
    }

    public void setSelectionEnd(int index) {
        int i = text.length();
        selectionEnd = MathHelper.clamp(index, 0, i);
        if (tr != null) {
            if (firstCharIndex > i) {
                firstCharIndex = i;
            }

            int j = editBox.getWidth();
            String string = tr.trimToWidth(text.substring(firstCharIndex), j);
            int k = string.length() + firstCharIndex;
            if (selectionEnd == firstCharIndex) {
                firstCharIndex -= tr.trimToWidth(text, j, true).length();
            }

            if (selectionEnd > k) {
                firstCharIndex += selectionEnd - k;
            } else if (selectionEnd <= firstCharIndex) {
                firstCharIndex -= firstCharIndex - selectionEnd;
            }

            firstCharIndex = MathHelper.clamp(firstCharIndex, 0, i);
        }

    }

}
