package me.redth.notenoughhuds.utils;

import me.redth.notenoughhuds.hud.BaseHud;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.Rectangle;

public class DrawUtils extends Gui {
    public static final ResourceLocation TRANSPARENT = new ResourceLocation("notenoughhuds", "textures/transparent.png");
    public static final Minecraft mc = Minecraft.getMinecraft();

    public static void drawTransparentBackground(int x, int y, int w, int h) {
        resetGl();
        mc.getTextureManager().bindTexture(TRANSPARENT);
        drawModalRectWithCustomSizedTexture(x, y, 0, 0, w, h, 8, 8);
        GlStateManager.disableBlend();
    }

    public static void drawTransparentBackground(Rectangle rect) {
        drawTransparentBackground(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    public static void drawRect(Rectangle rect, int color) {
        drawRect(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color);
    }

    public static void drawOutline(Rectangle rect, int color) {
        drawOutline(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), color);
    }

    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if ((color >> 24 & 255) == 0) return;

        if (left < right) {
            float f = left;
            left = right;
            right = f;
        }

        if (top < bottom) {
            float f = top;
            top = bottom;
            bottom = f;
        }

        float alpha = (color >> 24 & 255) / 255.0F;
        float red = (color >> 16 & 255) / 255.0F;
        float green = (color >> 8 & 255) / 255.0F;
        float blue = (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(red, green, blue, alpha);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION);
        worldrenderer.pos(left, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, bottom, 0.0D).endVertex();
        worldrenderer.pos(right, top, 0.0D).endVertex();
        worldrenderer.pos(left, top, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawGradient(Rectangle rect, int colorTL, int colorTR, int colorBL, int colorBR) {
        drawGradient(rect.getX(), rect.getY(), rect.getX() + rect.getWidth(), rect.getY() + rect.getHeight(), colorTL, colorTR, colorBL, colorBR);
    }

    public static void drawGradient(float left, float top, float right, float bottom, int colorTL, int colorTR, int colorBL, int colorBR) {
        if (left < right) {
            float f = left;
            left = right;
            right = f;
        }

        if (top < bottom) {
            float f = top;
            top = bottom;
            bottom = f;
        }

        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        WRPCE(worldrenderer, left, bottom, colorBL);
        WRPCE(worldrenderer, right, bottom, colorBR);
        WRPCE(worldrenderer, right, top, colorTR);
        WRPCE(worldrenderer, left, top, colorTL);
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableTexture2D();
    }

    protected static void WRPCE(WorldRenderer wr, double x, double y, int color) {
        float a = (color >> 24 & 255) / 255.0F;
        float r = (color >> 16 & 255) / 255.0F;
        float g = (color >> 8 & 255) / 255.0F;
        float b = (color & 255) / 255.0F;
        wr.pos(x, y, 0.0d).color(r, g, b, a).endVertex();
    }

    public static void drawHorizontalLine(float x1, float x2, float y, int color) {
        if (x2 < x1) {
            float f = x1;
            x1 = x2;
            x2 = f;
        }

        drawRect(x1, y - 0.5F, x2, y + 0.5F, color);
    }

    public static void drawVerticalLine(float x, float y1, float y2, int color) {
        if (y2 < y1) {
            float f = y1;
            y1 = y2;
            y2 = f;
        }

        drawRect(x - 0.5F, y1, x + 0.5F, y2, color);
    }

    public static void drawOutline(float x1, float y1, float x2, float y2, int color) {
        drawRect(x1, y1, x2 - 1.0F, y1 + 1.0F, color);
        drawRect(x2 - 1.0F, y1, x2, y2 - 1.0F, color);
        drawRect(x1 + 1.0F, y2 - 1.0F, x2, y2, color);
        drawRect(x1, y1 + 1.0F, x1 + 1.0F, y2, color);
    }

    public static void drawOutline(int x1, int y1, int x2, int y2, int color) {
        drawRect(x1, y1, x2 - 1, y1 + 1, color);
        drawRect(x2 - 1, y1, x2, y2 - 1, color);
        drawRect(x1 + 1, y2 - 1, x2, y2, color);
        drawRect(x1, y1 + 1, x1 + 1, y2, color);
    }


    public static int drawString(String text, float x, float y, int textColor, boolean textShadow) {
        return drawString(text, x, y, textColor, textShadow, BaseHud.Alignment.LEFT);
    }

    public static int drawString(String text, float x, float y, int textColor, boolean textShadow, BaseHud.Alignment align) {
        if (text.isEmpty()) return (int) x;
        if (align == BaseHud.Alignment.CENTER) {
            x -= (mc.fontRendererObj.getStringWidth(text) - 1) / 2.0f;
        } else if (align == BaseHud.Alignment.RIGHT) {
            x -= mc.fontRendererObj.getStringWidth(text) - 1;
        }
        return mc.fontRendererObj.drawString(text, x, y, textColor, textShadow);
    }

    public void drawTexture(ResourceLocation texture, int x, int y, int textureX, int textureY, int width, int height) {
        resetGl();
        mc.getTextureManager().bindTexture(texture);
        drawTexturedModalRect(x, y, textureX, textureY, width, height);
        GlStateManager.disableBlend();
    }

    public static void resetGl() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
    }

}
