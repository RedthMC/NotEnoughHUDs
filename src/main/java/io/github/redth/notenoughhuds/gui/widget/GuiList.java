package io.github.redth.notenoughhuds.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiList<T extends GuiList.GuiListEntry> extends ClickableWidget {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    protected final List<T> entries = new ArrayList<>();
    protected boolean dragging;
    protected int scrollToMouseOffset;
    public int scrollY;
    protected int scrollLength;
    public int length;

    public GuiList(int x, int y, int width, int height) {
        super(x, y, width, height, Text.empty());
        scrollLength = height;
    }

    public void addEntry(T entry) {
        entries.add(entry);
        length = Math.max(length, entry.initialY + entry.getHeight() + 4);
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
        return scrollLength < height && mouseX >= x + width - 2 && mouseY >= scrollY && mouseX < x + width && mouseY < scrollY + scrollLength;
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
        scrollY = MathHelper.clamp(scrollY - wheel * 5, y, y + height - scrollLength);
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
//        drawCenteredTextWithShadow(matrix, mc.textRenderer, getMessage().asOrderedText(), x + width / 2, y + 4, 0xFFFFFF);
        if (scrollLength < height) fill(matrix, x + width - 2, scrollY, x + width, scrollY + scrollLength, dragging || hoveringScrollBar(mouseX, mouseY) ? 0xFF555555 : 0xFFAAAAAA);

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
