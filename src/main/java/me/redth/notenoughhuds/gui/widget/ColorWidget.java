package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Rectangle;

import java.awt.Color;

public class ColorWidget extends TextWidget {
    private final int hashtagWidth;
    private final NehColor option;
    private final Rectangle colorPicker;
    private final Rectangle colorBox;
    private final SatXBr satXBr;
    private final HueSlider hueSlider;
    private final AlphaSlider alphaSlider;
    private final Rectangle textBox;
    private final Rectangle chromaBox;
    private ColorPicker dragging = null;

    public ColorWidget(int x, int y, NehColor option) {
        super(x, y, option, 8, NehColor::isValidChar, NehColor::filterValidChars);
        hashtagWidth = fr.getCharWidth('#');
        this.option = option;
        colorPicker = new Rectangle(editBox.getX() + editBox.getWidth() + 4, editBox.getY() - 43, 62, 110);
        int i = colorPicker.getX() + 2;
        int j = colorPicker.getY() + 2;
        colorBox = new Rectangle(editBox.getX() + editBox.getWidth() - 12, editBox.getY() + editBox.getHeight() / 2 - 6, 12, 12);
        satXBr = new SatXBr(i, j, 58, 58);
        hueSlider = new HueSlider(i, j + 62, 58, 6);
        alphaSlider = new AlphaSlider(i, j + 72, 58, 6);
        textBox = new Rectangle(i, j + 82, 58, 12);
        chromaBox = new Rectangle(i, j + 96, 58, 10);
    }

    @Override
    public void drawEditButton(Minecraft mc, int mouseX, int mouseY) {
        DrawUtils.drawTransparentBackground(colorBox);
        DrawUtils.drawRect(colorBox, option.asInt());

        if (!textFieldFocused) return;

        DrawUtils.drawRect(colorPicker, 0xFF121212);
        DrawUtils.drawRect(textBox, 0xFF292929);

        int x = textBox.getX() + 2;
        int y = textBox.getY() + 2;

        fr.drawStringWithShadow("#" + StringUtils.rightPad(text, 8, '0'), x, y, 0xAAAAAA);
        x += hashtagWidth;
        fr.drawStringWithShadow(text, x, y, 0xFFFFFF);

        int x1 = x + fr.getStringWidth(text.substring(0, selectionStart));
        int x2 = x + fr.getStringWidth(text.substring(0, selectionEnd));

        if (focusedTicks / 6 % 2 == 0) {
            drawRect(x2 - 1, y - 1, x2, y + 9, -3092272);
        }

        if (selectionStart != selectionEnd) {
            highlight(x1, y - 1, x2, y + 9);
        }

//            int j = selectionStart;
//            int k = selectionEnd;
//            boolean inbound = j >= 0 && j <= text.length();
//            int x = textBox.getX() + 2;
//            int y = textBox.getY() + 2;
//
//            fr.drawStringWithShadow("#" + StringUtils.rightPad(text, 8, '0'), x, y, 0xAAAAAA);
//            x += fr.getCharWidth('#');
//            int x2 = x;
//
//            if (k > text.length()) {
//                k = text.length();
//            }
//
//            if (text.length() > 0) {
//                String s1 = inbound ? text.substring(0, j) : text;
//                x2 = fr.drawStringWithShadow(s1, x, y, 0xFFFFFF);
//            }
//
//            --x2;
//
//            if (text.length() > 0 && inbound && j < text.length()) {
//                fr.drawStringWithShadow(text.substring(j), x2, y, 0xFFFFFF);
//            }
//
//            if (focusedTicks / 6 % 2 == 0 && inbound) {
//                drawRect(x2 - 1, y - 1, x2, y + 9, -3092272);
//            }
//
//            if (k != j) {
//                int l1 = x + fr.getStringWidth(text.substring(0, k));
//                highlight(x2, y - 1, l1 - 1, y + 9);
//            }

        if (dragging != null) {
            dragging.onDrag(mouseX, mouseY);
            if (option.chroma) {
                String a = Integer.toString((int) (alphaSlider.alpha * 255), 16);
                if (a.length() == 1) a = "0" + a;
                option.set(a + option.get().substring(2));
            } else {
                option.set((int) (alphaSlider.alpha * 255) << 24 | (0xFFFFFF & Color.HSBtoRGB(hueSlider.hue, satXBr.sat, satXBr.br)));
            }
            syncValue();
        }

        hueSlider.render();
        satXBr.render(hueSlider.hue);
        alphaSlider.render(Color.HSBtoRGB(hueSlider.hue, satXBr.sat, satXBr.br));

        int cmxw = chromaBox.getX() + chromaBox.getWidth();
        int cmy = chromaBox.getY();
        mc.fontRendererObj.drawStringWithShadow("Rainbow", chromaBox.getX(), cmy + 1, NehColor.getRainbow());
        DrawUtils.drawOutline(cmxw - 10, cmy, cmxw, cmy + 10, 0xFFFFFFFF);
        if (option.chroma) {
            drawRect(cmxw - 8, cmy + 2, cmxw - 2, cmy + 8, 0xFFFFFFFF);
            updateColor();
        }

    }

    @Override
    public void onMissClick(int mouseX, int mouseY) {
        textFieldFocused = false;
        syncValue();
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        return super.isMouseOver(mouseX, mouseY) || (textFieldFocused && colorPicker.contains(mouseX, mouseY));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        dragging = null;
    }

    @Override
    public void setText(String str) {
        super.setText(str);
        updateColor();
    }

    @Override
    public void onClick(int mouseX, int mouseY) {
        setFocused(true);

        if (!colorPicker.contains(mouseX, mouseY)) {
            updateColor();
            return;
        }

        if (textBox.contains(mouseX, mouseY)) {
            int i = mouseX - textBox.getX() + hashtagWidth;
            String s = fr.trimStringToWidth(text.substring(firstCharIndex), textBox.getWidth() - hashtagWidth);
            setSelectionStart(fr.trimStringToWidth(s, i).length() + firstCharIndex);
            updateColor();
        } else {
            if (alphaSlider.isHovered(mouseX, mouseY)) {
                dragging = alphaSlider;
            } else if (chromaBox.contains(mouseX, mouseY)) {
                option.chroma = !option.chroma;
                if (!option.chroma) updateColor();
                return;
            }

            if (option.chroma) return;

            if (hueSlider.isHovered(mouseX, mouseY)) {
                dragging = hueSlider;
            } else if (satXBr.isHovered(mouseX, mouseY)) {
                dragging = satXBr;
            }
        }
    }

    public void updateColor() {
        float a = (option.asInt() >> 24 & 255) / 255.0f;
        int r = (option.asInt() >> 16 & 255);
        int g = (option.asInt() >> 8 & 255);
        int b = (option.asInt() & 255);

        alphaSlider.alpha = a;
        float[] hsb = new float[3];
        Color.RGBtoHSB(r, g, b, hsb);
        hueSlider.hue = hsb[0];
        satXBr.sat = hsb[1];
        satXBr.br = hsb[2];
    }

    public static class SatXBr extends ColorPicker {
        public float sat; // x
        public float br; // y

        public SatXBr(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        public void render(float hue) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer wr = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            wr.begin(5, DefaultVertexFormats.POSITION_COLOR);

            WRPC(wr, x, y, 0xFFFFFFFF);
            WRPC(wr, x, y2, 0xFF000000);
            WRPC(wr, x2, y, Color.HSBtoRGB(hue, 1.0F, 1.0F));
            WRPC(wr, x2, y2, 0xFF000000);

            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();

            int i = x + (int) (sat * w);
            int j = y2 - (int) (br * h);

            DrawUtils.drawOutline(i - 2, j - 2, i + 2, j + 2, 0xFFFFFFFF);
        }

        @Override
        public void onDrag(int mouseX, int mouseY) {
            sat = clamp0_1((mouseX - x) / (float) w);
            br = clamp0_1((y2 - mouseY) / (float) h);
        }
    }

    public static class HueSlider extends ColorPicker {
        public float hue;

        public HueSlider(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        public void render() {
            // DrawUtils.drawRect(rect, 0xFFFFFFFF);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer wr = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            wr.begin(5, DefaultVertexFormats.POSITION_COLOR);

            WRPC(wr, x, y, 0xFFFF0000);
            WRPC(wr, x, y2, 0xFFFF0000);

            for (int i = 1; i <= w; i++) {
                int color = Color.HSBtoRGB((float) i / w, 1.0F, 1.0F);
                int j = x + i;
                WRPC(wr, j, y, color);
                WRPC(wr, j, y2, color);
            }

            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();

            int i = x + (int) (hue * w);
            DrawUtils.drawOutline(i - 2, y - 2, i + 2, y2 + 2, 0xFFFFFFFF);
        }

        @Override
        public void onDrag(int mouseX, int mouseY) {
            hue = clamp0_1((float) (mouseX - x) / w);
        }
    }

    public static class AlphaSlider extends ColorPicker {
        public float alpha;

        public AlphaSlider(int x, int y, int w, int h) {
            super(x, y, w, h);
        }

        public void render(int color) {
            DrawUtils.drawTransparentBackground(x, y, w, h);

            // DrawUtils.drawRect(rect, 0xFFFFFFFF);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer wr = tessellator.getWorldRenderer();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.shadeModel(7425);
            wr.begin(5, DefaultVertexFormats.POSITION_COLOR);
            WRPC(wr, x, y, color);
            WRPC(wr, x, y2, color);
            WRPC(wr, x2, y, color & 0xFFFFFF);
            WRPC(wr, x2, y2, color & 0xFFFFFF);
            tessellator.draw();
            GlStateManager.shadeModel(7424);
            GlStateManager.disableBlend();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();

            int i = x2 - (int) (alpha * w);
            DrawUtils.drawOutline(i - 2, y - 2, i + 2, y2 + 2, 0xFFFFFFFF);
        }

        @Override
        public void onDrag(int mouseX, int mouseY) {
            alpha = clamp0_1((float) (x2 - mouseX) / w);
        }
    }

    public static abstract class ColorPicker {
        public final int x;
        public final int y;
        public final int w;
        public final int h;
        public final int x2;
        public final int y2;

        public ColorPicker(int x, int y, int w, int h) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.x2 = x + w;
            this.y2 = y + h;
        }

        public abstract void onDrag(int mouseX, int mouseY);

        public boolean isHovered(int mouseX, int mouseY) {
            return mouseX >= x - 2 && mouseY >= y - 2 && mouseX < x2 + 2 && mouseY < y2 + 2;
        }

        protected static void WRPC(WorldRenderer wr, double x, double y, int color) {
            float a = (color >> 24 & 255) / 255.0F;
            float r = (color >> 16 & 255) / 255.0F;
            float g = (color >> 8 & 255) / 255.0F;
            float b = (color & 255) / 255.0F;
            wr.pos(x, y, 0.0d).color(r, g, b, a).endVertex();
        }

        protected static float clamp0_1(float f) {
            return MathHelper.clamp_float(f, 0.0f, 1.0f);
        }

    }
}
