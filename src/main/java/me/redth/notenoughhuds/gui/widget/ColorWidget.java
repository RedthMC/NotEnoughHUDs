package me.redth.notenoughhuds.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

public class ColorWidget extends TextWidget {
    private final NehColor option;
    private final Rect2i colorBox;
    private final Rect2i colorPicker;
    private final SatXBr satXBr;
    private final HueSlider hueSlider;
    private final AlphaSlider alphaSlider;
    private final Rect2i textBox;
    private final Rect2i chromaBox;
    private ColorPicker dragging = null;

    public ColorWidget(int x, int y, NehColor option) {
        super(x, y, option, 8, NehColor::isValidChar, NehColor::filterValidChars);
        this.option = option;
        colorPicker = new Rect2i(editBox.getX() + editBox.getWidth() + 4, editBox.getY() - 43, 62, 110);
        int i = colorPicker.getX() + 2;
        int j = colorPicker.getY() + 2;
        colorBox = new Rect2i(editBox.getX() + editBox.getWidth() - 12, editBox.getY() + editBox.getHeight() / 2 - 6, 12, 12);
        satXBr = new SatXBr(i, j, 58, 58);
        hueSlider = new HueSlider(i, j + 62, 58, 6);
        alphaSlider = new AlphaSlider(i, j + 72, 58, 6);
        textBox = new Rect2i(i, j + 82, 58, 12);
        chromaBox = new Rect2i(i, j + 96, 58, 10);
    }

    @Override
    public void drawEditButton(MatrixStack matrix, int mouseX, int mouseY) {
        DrawUtils.drawTransparentBackground(matrix, colorBox);
        DrawUtils.fill(matrix, colorBox, option.asColor());

        if (isFocused()) {
            DrawUtils.fill(matrix, colorPicker, 0xFF121212);
            DrawUtils.fill(matrix, textBox, 0xFF292929);
            int j = selectionStart;
            int k = selectionEnd;
            String s = text;
            boolean inbound = j >= 0 && j <= s.length();
            int x = textBox.getX() + 2;
            int y = textBox.getY() + 2;

            tr.drawWithShadow(matrix, "#" + StringUtils.rightPad(text, 8, '0'), x, y, 0xAAAAAA);
            x += tr.getWidth("#");

            int x2 = x;

            if (s.length() > 0) {
                String s1 = inbound ? s.substring(0, j) : s;
                x2 = tr.drawWithShadow(matrix, s1, x, y, 0xFFFFFF);
            }

            --x2;

            if (s.length() > 0 && inbound && j < s.length()) {
                tr.drawWithShadow(matrix, s.substring(j), x2, y, 0xFFFFFF);
            }

            if (focusedTicks / 6 % 2 == 0 && inbound) {
                fill(matrix, x2, y - 1, x2 + 1, y + 9, 0xFFFFFFFF);
            }

            if (k != j) {
                int l1 = x + tr.getWidth(s.substring(0, k));
                drawSelectionHighlight(x2, y - 1, l1 - 1, y + 9);
            }

            if (dragging != null) {
                dragging.onDrag(mouseX, mouseY);
                option.set((int) (alphaSlider.alpha * 255) << 24 | (0xFFFFFF & Color.HSBtoRGB(hueSlider.hue, satXBr.sat, satXBr.br)));
                syncValue();
            }
            hueSlider.render(matrix);
            satXBr.render(matrix, hueSlider.hue);
            alphaSlider.render(matrix, Color.HSBtoRGB(hueSlider.hue, satXBr.sat, satXBr.br));
            int cmxw = chromaBox.getX() + chromaBox.getWidth();
            int cmy = chromaBox.getY();
            mc.textRenderer.drawWithShadow(matrix, "Rainbow", chromaBox.getX(), cmy + 1, NehColor.getRainbow());
            DrawUtils.drawOutline(matrix, cmxw - 10, cmy, cmxw, cmy + 10, 0xFFFFFFFF);
            if (option.chroma) {
                fill(matrix, cmxw - 8, cmy + 2, cmxw - 2, cmy + 8, 0xFFFFFFFF);
                updateColor();
            }
        }
    }

    @Override
    public boolean clicked(double mouseX, double mouseY) {
        return super.clicked(mouseX, mouseY) || (isFocused() && colorPicker.contains((int) mouseX, (int) mouseY));
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        dragging = null;
    }

    @Override
    public void write(String text) {
        super.write(text);
        updateColor();
    }

    @Override
    public void onClick(double mouseXD, double mouseYD) {
        int mouseX = (int) mouseXD;
        int mouseY = (int) mouseYD;
        setFocused(true);
        if (colorPicker.contains(mouseX, mouseY)) {
            if (textBox.contains(mouseX, mouseY)) {
                int i = mouseX - textBox.getX();
                setSelectionStart(tr.trimToWidth(text, i).length());
                updateColor();
            } else {
                if (alphaSlider.isHovered(mouseX, mouseY)) dragging = alphaSlider;
                else if (chromaBox.contains(mouseX, mouseY)) option.chroma = !option.chroma;
                else if (option.chroma) {
                } else if (hueSlider.isHovered(mouseX, mouseY)) dragging = hueSlider;
                else if (satXBr.isHovered(mouseX, mouseY)) dragging = satXBr;
            }
        } else {
            updateColor();
        }
    }

    public void updateColor() {
        float a = (option.asColor() >> 24 & 255) / 255.0f;
        int r = (option.asColor() >> 16 & 255);
        int g = (option.asColor() >> 8 & 255);
        int b = (option.asColor() & 255);
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

        public void render(MatrixStack matrix, float hue) {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bb = tessellator.getBuffer();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

            BBVC(bb, x2, y, Color.HSBtoRGB(hue, 1.0F, 1.0F));
            BBVC(bb, x, y, 0xFFFFFFFF);
            BBVC(bb, x, y2, 0xFF000000);
            BBVC(bb, x2, y2, 0xFF000000);

            BufferRenderer.drawWithShader(bb.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            int i = x + (int) (sat * w);
            int j = y2 - (int) (br * h);

            DrawUtils.drawOutline(matrix, i - 2, j - 2, i + 2, j + 2, 0xFFFFFFFF);
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

        public void render(MatrixStack matrix) {
            // DrawUtils.drawRect(rect, 0xFFFFFFFF);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bb = tessellator.getBuffer();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bb.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);

            BBVC(bb, x, y, 0xFFFF0000);
            BBVC(bb, x, y2, 0xFFFF0000);

            for (int i = 1; i <= w; i++) {
                int color = Color.HSBtoRGB((float) i / w, 1.0F, 1.0F);
                int j = x + i;
                BBVC(bb, j, y, color);
                BBVC(bb, j, y2, color);
            }

            BufferRenderer.drawWithShader(bb.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            int i = x + (int) (hue * w);
            DrawUtils.drawOutline(matrix, i - 2, y - 2, i + 2, y2 + 2, 0xFFFFFFFF);
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

        public void render(MatrixStack matrix, int color) {
            DrawUtils.drawTransparentBackground(matrix, x, y, w, h);

            // DrawUtils.drawRect(rect, 0xFFFFFFFF);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bb = tessellator.getBuffer();
            RenderSystem.disableTexture();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            bb.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            BBVC(bb, x2, y, color & 0xFFFFFF);
            BBVC(bb, x, y, color);
            BBVC(bb, x, y2, color);
            BBVC(bb, x2, y2, color & 0xFFFFFF);
            BufferRenderer.drawWithShader(bb.end());
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();

            int i = x2 - (int) (alpha * w);
            DrawUtils.drawOutline(matrix, i - 2, y - 2, i + 2, y2 + 2, 0xFFFFFFFF);
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

        protected static void BBVC(BufferBuilder bb, double x, double y, int color) {
            // float a = (color >> 24 & 255) / 255.0F;
            // float r = (color >> 16 & 255) / 255.0F;
            // float g = (color >> 8 & 255) / 255.0F;
            // float b = (color & 255) / 255.0F;
            bb.vertex(x, y, 0.0d).color(color).next();
        }

        protected static float clamp0_1(float f) {
            return MathHelper.clamp(f, 0.0f, 1.0f);
        }

    }

}
