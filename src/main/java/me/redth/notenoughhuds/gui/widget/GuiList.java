package me.redth.notenoughhuds.gui.widget;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiList<T extends GuiList.GuiListEntry> extends ClickableWidget {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final int SCROLL_BAR_WIDTH = 6;
    protected final Screen screen;
    protected final List<T> entries = new ArrayList<>();
    protected boolean dragging;
    protected int scrollToMouseOffset;
    public int scrollY;
    protected int scrollLength;
    public int length;

    public GuiList(Screen screen, int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        this.screen = screen;
        scrollY = y;
        scrollLength = height;
    }

    public void addEntry(T entry) {
        entries.add(entry);
        length = Math.max(length, entry.initialY + entry.getHeight());
        scrollLength = height * height / length;
    }

    public void clearEntries() {
        entries.clear();
        length = 0;
        scrollLength = 0;
    }

    public List<T> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    protected boolean hoveringScrollBar(double mouseX, double mouseY) {
        return scrollLength < height && mouseX >= x + width - SCROLL_BAR_WIDTH && mouseY >= scrollY && mouseX < x + width && mouseY < scrollY + scrollLength;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && isValidClickButton(button) && clicked(mouseX, mouseY)) {
            onClick(mouseX, mouseY);
            return true;
        }
        return false;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        for (T entry : entries) {
            if (entry.isMouseOver(mouseX, mouseY)) {
                entry.onClick(mouseX, mouseY);
                playDownSound(mc.getSoundManager());
                break;
            }
        }
        if (hoveringScrollBar(mouseX, mouseY)) {
            dragging = true;
            scrollToMouseOffset = (int) mouseY - scrollY;
            scrollTo((int) mouseY);
        }
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        if (dragging) {
            scrollTo((int) mouseY);
        }
    }

    @Override
    public void onRelease(double mouseX, double mouseY) {
        for (T entry : entries) {
            entry.onRelease(mouseX, mouseY);
        }
        dragging = false;
    }

    public void scrollTo(int mouseY) {
        if (scrollLength >= height) return;
        scrollY = MathHelper.clamp(mouseY - scrollToMouseOffset, y, y + height - scrollLength);
        for (T entry : entries) {
            entry.y = entry.initialY - (scrollY - y) * length / height;
        }
    }

    public void scrollBy(int wheel) {
        if (scrollLength >= height) return;
        scrollY = MathHelper.clamp(scrollY - wheel * 10, y, y + height - scrollLength);
        for (T entry : entries) {
            entry.y = entry.initialY - (scrollY - y) * length / height;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        scrollBy((int) amount);
        return true;
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        for (T entry : entries) {
            entry.render(matrix, mouseX, mouseY, delta);
        }
        renderHorizontalShadow();
        if (scrollLength < height) renderScrollBar(matrix);

    }

    protected void renderHorizontalShadow() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DrawableHelper.OPTIONS_BACKGROUND_TEXTURE);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(519);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR); // top = 20 left = 0 bottom = height - 20
        bufferBuilder.vertex(0D, y, -100D).texture(0F, 20F / 32F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, y, -100D).texture(screen.width / 32F, 20F / 32F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, 0D, -100D).texture(screen.width / 32F, 0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0D, 0D, -100D).texture(0F, 0F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0D, screen.height, -100D).texture(0F, screen.height / 32F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, screen.height, -100D).texture(screen.width / 32F, screen.height / 32F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(screen.width, y + height, -100D).texture(screen.width / 32F, (y + height) / 32F).color(64, 64, 64, 255).next();
        bufferBuilder.vertex(0D, y + height, -100D).texture(0F, (y + height) / 32F).color(64, 64, 64, 255).next();
        tessellator.draw();
        RenderSystem.depthFunc(515);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SrcFactor.ZERO, GlStateManager.DstFactor.ONE);
        RenderSystem.disableTexture();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
        bufferBuilder.vertex(0D, y + 4D, 0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(screen.width, y + 4D, 0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(screen.width, y, 0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0D, y, 0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(0D, y + height, 0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(screen.width, y + height, 0D).color(0, 0, 0, 255).next();
        bufferBuilder.vertex(screen.width, y + height - 4D, 0D).color(0, 0, 0, 0).next();
        bufferBuilder.vertex(0D, y + height - 4D, 0D).color(0, 0, 0, 0).next();
        tessellator.draw();
    }

    protected void renderScrollBar(MatrixStack matrix) {
        int sX = x + width - SCROLL_BAR_WIDTH;
        int sY = scrollY;
        int sX2 = x + width;
        int sY2 = scrollY + scrollLength;
        fill(matrix, sX, sY, sX2, sY2, 0xFF808080);
        fill(matrix, sX, sY, sX2 - 1, sY2 - 1, 0xFFC0C0C0);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (T entry : entries) if (entry.isFocused()) return entry.charTyped(chr, modifiers);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (T entry : entries) if (entry.isFocused()) return entry.keyPressed(keyCode, scanCode, modifiers);
        return false;
    }

    @Override
    public void appendNarrations(NarrationMessageBuilder builder) {
    }

    public static class GuiListEntry extends ClickableWidget {
        protected static final MinecraftClient mc = MinecraftClient.getInstance();
        public final int initialY;

        public GuiListEntry(int x, int y, int width, int height, Text text) {
            super(x, y, width, height, text);
            initialY = y;
        }

        @Override
        public void appendNarrations(NarrationMessageBuilder builder) {
        }

    }

}
