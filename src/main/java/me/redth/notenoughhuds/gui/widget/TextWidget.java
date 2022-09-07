package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class TextWidget extends OptionWidget {
    protected static final FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
    protected static final int background = 0x80000000;
    protected final NehOption<String> option;
    protected final int maxLength;
    protected final Predicate<Character> charValidator;
    protected final Function<String, String> stringFilter;
    protected String text;
    protected boolean textFieldFocused;
    protected int focusedTicks;
    protected int selectionStart;
    protected int selectionEnd;
    protected int firstCharIndex;

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
    }

    @Override
    public void onMissClick(int mouseX, int mouseY) {
        textFieldFocused = false;
        syncValue();
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        if (visible) {
            DrawUtils.drawRect(editBox, background);
            int j = selectionStart - firstCharIndex;
            int k = selectionEnd - firstCharIndex;
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), editBox.getWidth());
            boolean inbound = j >= 0 && j <= s.length();
            int x = editBox.getX() + 2;
            int y = editBox.getY() + editBox.getHeight() / 2;
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
//        int x1 = editBox.getX() + 2;
//        int y1 = editBox.getY() + editBox.getHeight() / 2;
//        int startToFirst = selectionStart - firstCharacterIndex;
//        int endToFirst = selectionEnd - firstCharacterIndex;
//        String string = fr.trimStringToWidth(text.substring(firstCharacterIndex), editBox.getWidth());
//        boolean bl = startToFirst >= 0 && startToFirst <= string.length();
//        boolean flashCursor = textFieldFocused && focusedTicks / 6 % 2 == 0 && bl;
//        int n = x1;
//        if (endToFirst > string.length()) {
//            endToFirst = string.length();
//        }
//
//        if (!string.isEmpty()) {
//            String string2 = bl ? string.substring(0, startToFirst) : string;
//            n = fr.drawStringWithShadow(string2, x1, y1 - 4, editableColor);
//        }
//
//        boolean bl3 = selectionStart < text.length() || text.length() >= maxLength;
//        int o = n;
//        if (!bl) {
//            o = startToFirst > 0 ? x1 + editBox.getWidth() : x1;
//        } else if (bl3) {
//            o = --n;
//        }
//
//        if (!string.isEmpty() && bl && startToFirst < string.length()) {
//            fr.drawStringWithShadow(string.substring(startToFirst), n, y1 - 4, editableColor);
//        }
//
//        if (flashCursor) {
//            if (bl3) {
//                drawRect(o, y1 - 5, o + 1, y1 + 5, -3092272);
//            } else {
//                fr.drawStringWithShadow("_", o, y1 - 4, editableColor);
//            }
//        }
//
//        if (endToFirst != startToFirst) {
//            int p = x1 + fr.getStringWidth(string.substring(0, endToFirst));
//            drawSelectionHighlight(o, y1 - 5, p - 1, y1 + 5);
//        }
    }

    public void setText(String str) {
        if (str.length() > maxLength) {
            text = str.substring(0, maxLength);
        } else {
            text = str;
        }
        option.set(text);
    }

    public String getSelectedText() {
        int i = Math.min(selectionStart, selectionEnd);
        int j = Math.max(selectionStart, selectionEnd);
        return text.substring(i, j);
    }

    public void writeText(String str) {
        String s = "";
        String s1 = stringFilter.apply(str);
        int i = Math.min(selectionStart, selectionEnd);
        int j = Math.max(selectionStart, selectionEnd);
        int k = maxLength - text.length() - (i - j);
        int l;

        if (text.length() > 0) {
            s = s + text.substring(0, i);
        }

        if (k < s1.length()) {
            s = s + s1.substring(0, k);
            l = k;
        } else {
            s = s + s1;
            l = s1.length();
        }

        if (text.length() > 0 && j < text.length()) {
            s = s + text.substring(j);
        }

        setText(s);
        moveCursorBy(i - selectionEnd + l);

    }

    public void deleteWords(int offset) {
        if (text.length() != 0) {
            if (selectionEnd != selectionStart) {
                writeText("");
            } else {
                deleteFromCursor(getNthWordFromCursor(offset) - selectionStart);
            }
        }
    }

    public void deleteFromCursor(int offset) {
        if (text.length() != 0) {
            if (selectionEnd != selectionStart) {
                writeText("");
            } else {
                boolean flag = offset < 0;
                int i = flag ? selectionStart + offset : selectionStart;
                int j = flag ? selectionStart : selectionStart + offset;
                String s = "";

                if (i >= 0) {
                    s = text.substring(0, i);
                }

                if (j < text.length()) {
                    s = s + text.substring(j);
                }

                setText(s);

                if (flag) {
                    moveCursorBy(offset);
                }

            }
        }
    }

    public int getNthWordFromCursor(int offset) {
        return getNthWordFromPos(offset, selectionStart);
    }

    public int getNthWordFromPos(int p_146183_1_, int p_146183_2_) {
        return func_146197_a(p_146183_1_, p_146183_2_, true);
    }

    public int func_146197_a(int p_146197_1_, int p_146197_2_, boolean p_146197_3_) {
        int i = p_146197_2_;
        boolean flag = p_146197_1_ < 0;
        int j = Math.abs(p_146197_1_);

        for (int k = 0; k < j; ++k) {
            if (!flag) {
                int l = text.length();
                i = text.indexOf(32, i);

                if (i == -1) {
                    i = l;
                } else {
                    while (p_146197_3_ && i < l && text.charAt(i) == 32) {
                        ++i;
                    }
                }
            } else {
                while (p_146197_3_ && i > 0 && text.charAt(i - 1) == 32) {
                    --i;
                }

                while (i > 0 && text.charAt(i - 1) != 32) {
                    --i;
                }
            }
        }

        return i;
    }

    public void moveCursorBy(int p_146182_1_) {
        setSelectionStart(selectionEnd + p_146182_1_);
    }

    public void setSelectionStart(int p_146190_1_) {
        selectionStart = p_146190_1_;
        int i = text.length();
        selectionStart = MathHelper.clamp_int(selectionStart, 0, i);
        setSelectionPos(selectionStart);
    }

    public void setCursorPositionZero() {
        setSelectionStart(0);
    }

    public void setCursorPositionEnd() {
        setSelectionStart(text.length());
    }

    public void keyTyped(char chr, int keyCode) {
        if (!textFieldFocused) {
        } else if (GuiScreen.isKeyComboCtrlA(keyCode)) {
            setCursorPositionEnd();
            setSelectionPos(0);
        } else if (GuiScreen.isKeyComboCtrlC(keyCode)) {
            GuiScreen.setClipboardString(getSelectedText());
        } else if (GuiScreen.isKeyComboCtrlV(keyCode)) {
            if (enabled) {
                writeText(GuiScreen.getClipboardString());
            }

        } else if (GuiScreen.isKeyComboCtrlX(keyCode)) {
            GuiScreen.setClipboardString(getSelectedText());

            if (enabled) {
                writeText("");
            }

        } else {
            switch (keyCode) {
                case 14:

                    if (GuiScreen.isCtrlKeyDown()) {
                        if (enabled) {
                            deleteWords(-1);
                        }
                    } else if (enabled) {
                        deleteFromCursor(-1);
                    }

                    return;
                case 199:

                    if (GuiScreen.isShiftKeyDown()) {
                        setSelectionPos(0);
                    } else {
                        setCursorPositionZero();
                    }

                    return;
                case 203:

                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(-1, selectionEnd));
                        } else {
                            setSelectionPos(selectionEnd - 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        setSelectionStart(getNthWordFromCursor(-1));
                    } else {
                        moveCursorBy(-1);
                    }

                    return;
                case 205:

                    if (GuiScreen.isShiftKeyDown()) {
                        if (GuiScreen.isCtrlKeyDown()) {
                            setSelectionPos(getNthWordFromPos(1, selectionEnd));
                        } else {
                            setSelectionPos(selectionEnd + 1);
                        }
                    } else if (GuiScreen.isCtrlKeyDown()) {
                        setSelectionStart(getNthWordFromCursor(1));
                    } else {
                        moveCursorBy(1);
                    }

                    return;
                case 207:

                    if (GuiScreen.isShiftKeyDown()) {
                        setSelectionPos(text.length());
                    } else {
                        setCursorPositionEnd();
                    }

                    return;
                case 211:

                    if (GuiScreen.isCtrlKeyDown()) {
                        if (enabled) {
                            deleteWords(1);
                        }
                    } else if (enabled) {
                        deleteFromCursor(1);
                    }

                    return;
                default:
                    if (charValidator.test(chr)) {
                        if (enabled) {
                            writeText(String.valueOf(chr));
                        }
                    }
            }
        }
    }

    public void onClick(int mouseX, int mouseY) {
        boolean flag = editBox.contains(mouseX, mouseY);

        setFocused(flag);

        if (textFieldFocused && flag) {
            int i = mouseX - (editBox.getX() + 2);
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), editBox.getWidth());
            setSelectionStart(fr.trimStringToWidth(s, i).length() + firstCharIndex);
        }
    }

    @Override
    public void tick() {
        ++focusedTicks;
    }

    public void highlight(int x1, int y1, int x2, int y2) {
//        if (x1 < x2) {
//            int i = x1;
//            x1 = x2;
//            x2 = i;
//        }
//
//        if (y1 < y2) {
//            int j = y1;
//            y1 = y2;
//            y2 = j;
//        }
//        int k = editBox.getX() + editBox.getWidth();
//        if (x2 > k) {
//            x2 = k;
//        }
//
//        if (x1 > k) {
//            x1 = k;
//        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.enableColorLogic();
        GlStateManager.colorLogicOp(5387);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(x1, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y2, 0.0D).endVertex();
        worldrenderer.pos(x2, y1, 0.0D).endVertex();
        worldrenderer.pos(x1, y1, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.disableColorLogic();
        GlStateManager.enableTexture2D();
    }

    public void setFocused(boolean focused) {
        if (focused && !textFieldFocused) {
            focusedTicks = 0;
        }

        textFieldFocused = focused;
    }

    public void setSelectionPos(int index) {
        int i = text.length();

        if (index > i) {
            index = i;
        }

        if (index < 0) {
            index = 0;
        }

        selectionEnd = index;

        if (fr != null) {
            if (firstCharIndex > i) {
                firstCharIndex = i;
            }

            int j = editBox.getWidth();
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), j);
            int k = s.length() + firstCharIndex;

            if (index == firstCharIndex) {
                firstCharIndex -= fr.trimStringToWidth(text, j, true).length();
            }

            if (index > k) {
                firstCharIndex += index - k;
            } else if (index <= firstCharIndex) {
                firstCharIndex -= firstCharIndex - index;
            }

            firstCharIndex = MathHelper.clamp_int(firstCharIndex, 0, i);
        }
    }

}
