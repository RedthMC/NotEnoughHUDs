package me.redth.notenoughhuds.utils;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Rect2i;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Matrix4f;

public class DrawUtils extends DrawableHelper {
    public static final Identifier TRANSPARENT = new Identifier("notenoughhuds", "textures/transparent.png");
    public static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void drawTransparentBackground(MatrixStack matrix, int x, int y, int w, int h) {
        resetGl();
        mc.getTextureManager().bindTexture(TRANSPARENT);
        drawTexture(matrix, x, y, 0, 0, w, h, 2, 2);
        RenderSystem.disableBlend();
    }

    public static void drawTransparentBackground(MatrixStack matrix, Rect2i rect) {
        drawTransparentBackground(matrix, rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public static void fill(MatrixStack matrix, Rect2i rect, int color) {
        fill(matrix, rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color);
    }

    public static void fill(MatrixStack matrix, float x1, float y1, float x2, float y2, int color) {
        if ((color >> 24 & 255) == 0) return;

        float i;
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

        Matrix4f matrix4f = matrix.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(matrix4f, x1, y2, 0.0F).color(color).next();
        bufferBuilder.vertex(matrix4f, x2, y2, 0.0F).color(color).next();
        bufferBuilder.vertex(matrix4f, x2, y1, 0.0F).color(color).next();
        bufferBuilder.vertex(matrix4f, x1, y1, 0.0F).color(color).next();
        BufferRenderer.drawWithShader(bufferBuilder.end());
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
    }

    public static void drawHorizontalLine(MatrixStack matrix, float x1, float x2, float y, int color) {
        if (x2 < x1) {
            float f = x1;
            x1 = x2;
            x2 = f;
        }

        fill(matrix, x1, y - 0.5F, x2, y + 0.5F, color);
    }

    public static void drawVerticalLine(MatrixStack matrix, float x, float y1, float y2, int color) {
        if (y2 < y1) {
            float f = y1;
            y1 = y2;
            y2 = f;
        }

        fill(matrix, x - 0.5F, y1, x + 0.5F, y2, color);
    }


    public static void drawOutline(MatrixStack matrix, Rect2i rect, int color) {
        drawOutline(matrix, rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color);
    }

    public static void drawOutline(MatrixStack matrix, float x1, float y1, float x2, float y2, int color) {
        fill(matrix, x1, y1, x2 - 1.0F, y1 + 1.0F, color);
        fill(matrix, x2 - 1.0F, y1, x2, y2 - 1.0F, color);
        fill(matrix, x1 + 1.0F, y2 - 1.0F, x2, y2, color);
        fill(matrix, x1, y1 + 1.0F, x1 + 1.0F, y2, color);
    }


    public static int drawString(MatrixStack matrix, String text, float x, float y, int textColor, boolean textShadow) {
        return drawString(matrix, text, x, y, textColor, textShadow, BaseHud.Alignment.LEFT);
    }

    public static int drawText(MatrixStack matrix, Text text, float x, float y, int textColor, boolean textShadow) {
        return drawText(matrix, text, x, y, textColor, textShadow, BaseHud.Alignment.LEFT);
    }

    public static int drawString(MatrixStack matrix, String text, float x, float y, int textColor, boolean textShadow, BaseHud.Alignment align) {
        if (text.isEmpty()) return (int) x;
        if (align == BaseHud.Alignment.CENTER) {
            x -= (mc.textRenderer.getWidth(text) - 1) / 2.0f;
        } else if (align == BaseHud.Alignment.RIGHT) {
            x -= mc.textRenderer.getWidth(text) - 1;
        }
        return textShadow ? mc.textRenderer.drawWithShadow(matrix, text, x, y, textColor) : mc.textRenderer.draw(matrix, text, x, y, textColor);
    }

    public static int drawText(MatrixStack matrix, Text text, float x, float y, int textColor, boolean textShadow, BaseHud.Alignment align) {
        if (align == BaseHud.Alignment.CENTER) {
            x -= (mc.textRenderer.getWidth(text) - 1) / 2.0f;
        } else if (align == BaseHud.Alignment.RIGHT) {
            x -= mc.textRenderer.getWidth(text) - 1;
        }
        return textShadow ? mc.textRenderer.drawWithShadow(matrix, text, x, y, textColor) : mc.textRenderer.draw(matrix, text, x, y, textColor);
    }

    public void drawTexture(MatrixStack matrix, Identifier texture, int x, int y, int u, int v, int width, int height) {
        RenderSystem.setShaderTexture(0, texture);
        resetGl();
        drawTexture(matrix, x, y, u, v, width, height);
        RenderSystem.disableBlend();
    }

    public static void resetGl() {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.defaultBlendFunc();
    }
}
