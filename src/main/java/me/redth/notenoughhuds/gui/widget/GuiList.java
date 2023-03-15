package me.redth.notenoughhuds.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiList<T extends GuiList.GuiListEntry> extends GuiButton {
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static final int SCROLL_BAR_WIDTH = 6;
    protected final List<T> entries = new ArrayList<>();
    protected final GuiScreen screen;
    protected boolean draggingScrollBar;
    protected int scrollToMouseOffset;
    public int scrollBarY;
    protected int scrollBarLength;
    public int length;

    public GuiList(GuiScreen screen, int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, null);
        this.screen = screen;
        scrollBarY = y;
        scrollBarLength = height;
    }

    public void addEntry(T entry) {
        entries.add(entry);
        length = Math.max(length, entry.initialY + entry.height);
        scrollBarLength = height * height / length;
    }

    public void clearEntries() {
        entries.clear();
        length = 0;
        scrollBarLength = 0;
    }

    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    protected boolean hoveringScrollBar(int mouseX, int mouseY) {
        return scrollBarLength < height && mouseX >= xPosition + width - SCROLL_BAR_WIDTH && mouseY >= scrollBarY && mouseX < xPosition + width && mouseY < scrollBarY + scrollBarLength;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        for (T entry : entries) {
            if (entry.mousePressed(mc, mouseX, mouseY)) {
                entry.playPressSound(mc.getSoundHandler());
                return true;
            }
        }
        if (hoveringScrollBar(mouseX, mouseY)) {
            draggingScrollBar = true;
            scrollToMouseOffset = mouseY - scrollBarY;
            scrollTo(mouseY);
            return true;
        }
        return false;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
//        for (T entry : entries) {
//            entry.accessDrag(mc, mouseX, mouseY);
//        }
        if (draggingScrollBar) {
            scrollTo(mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        for (T entry : entries) {
            entry.mouseReleased(mouseX, mouseY);
        }
        draggingScrollBar = false;
    }

    public void scrollTo(int mouseY) {
        if (scrollBarLength >= height) return;
        scrollBarY = MathHelper.clamp_int(mouseY - scrollToMouseOffset, yPosition, yPosition + height - scrollBarLength);
        for (T entry : entries) {
            entry.yPosition = entry.initialY - (scrollBarY - yPosition) * length / height;
        }
    }

    public void scrollBy(int wheel) {
        if (scrollBarLength >= height) return;
        scrollBarY = MathHelper.clamp_int(scrollBarY - wheel / 10, yPosition, yPosition + height - scrollBarLength);
        for (T entry : entries) {
            entry.yPosition = entry.initialY - (scrollBarY - yPosition) * length / height;
        }
    }


    @Override
    public void playPressSound(SoundHandler soundHandlerIn) {
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        mouseDragged(mc, mouseX, mouseY);
        for (T entry : entries) {
            entry.drawButton(mc, mouseX, mouseY);
        }
//        drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + 4, 0xFFFFFF);
//        if (scrollLength < height) drawRect(xPosition + width - 2, scrollY, xPosition + width, scrollY + scrollLength, dragging || hoveringScrollBar(mouseX, mouseY) ? 0xFF555555 : 0xFFAAAAAA);
        renderHorizontalShadow();
        if (scrollBarLength < height) renderScrollBar();

        GlStateManager.enableTexture2D();
        GlStateManager.shadeModel(7424);
        GlStateManager.enableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
    }

    protected void renderHorizontalShadow() {
        Tessellator tl = Tessellator.getInstance();
        WorldRenderer wr = tl.getWorldRenderer();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.disableDepth();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR); // top = 20 left = 0 bottom = height - 20
        wr.pos(0D, yPosition, -100D).tex(0F, 20F / 32F).color(64, 64, 64, 255).endVertex();
        wr.pos(screen.width, yPosition, -100D).tex(screen.width / 32F, 20F / 32F).color(64, 64, 64, 255).endVertex();
        wr.pos(screen.width, 0D, -100D).tex(screen.width / 32F, 0F).color(64, 64, 64, 255).endVertex();
        wr.pos(0D, 0D, -100D).tex(0F, 0F).color(64, 64, 64, 255).endVertex();
        wr.pos(0D, screen.height, -100D).tex(0F, screen.height / 32F).color(64, 64, 64, 255).endVertex();
        wr.pos(screen.width, screen.height, -100D).tex(screen.width / 32F, screen.height / 32F).color(64, 64, 64, 255).endVertex();
        wr.pos(screen.width, yPosition + height, -100D).tex(screen.width / 32F, (yPosition + height) / 32F).color(64, 64, 64, 255).endVertex();
        wr.pos(0D, yPosition + height, -100D).tex(0F, (yPosition + height) / 32F).color(64, 64, 64, 255).endVertex();
        tl.draw();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.disableTexture2D();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(0D, yPosition + 4D, 0D).color(0, 0, 0, 0).endVertex();
        wr.pos(screen.width, yPosition + 4D, 0D).color(0, 0, 0, 0).endVertex();
        wr.pos(screen.width, yPosition, 0D).color(0, 0, 0, 255).endVertex();
        wr.pos(0D, yPosition, 0D).color(0, 0, 0, 255).endVertex();
        wr.pos(0D, yPosition + height, 0D).color(0, 0, 0, 255).endVertex();
        wr.pos(screen.width, yPosition + height, 0D).color(0, 0, 0, 255).endVertex();
        wr.pos(screen.width, yPosition + height - 4D, 0D).color(0, 0, 0, 0).endVertex();
        wr.pos(0D, yPosition + height - 4D, 0D).color(0, 0, 0, 0).endVertex();
        tl.draw();

    }

    protected void renderScrollBar() {
        int sX = xPosition + width - SCROLL_BAR_WIDTH;
        int sY = scrollBarY;
        int sX2 = xPosition + width;
        int sY2 = scrollBarY + scrollBarLength;
        drawRect(sX, sY, sX2, sY2, 0xFF808080);
        drawRect(sX, sY, sX2 - 1, sY2 - 1, 0xFFC0C0C0);
    }

    public static class GuiListEntry extends GuiButtonExt {
        public final int initialY;

        public GuiListEntry(int x, int y, int widthIn, int heightIn, String buttonText) {
            super(32, x, y, widthIn, heightIn, buttonText);
            initialY = y;
        }

//        public void accessDrag(Minecraft mc, int mouseX, int mouseY) {
//            mouseDragged(mc, mouseX, mouseY);
//        }
    }
}
