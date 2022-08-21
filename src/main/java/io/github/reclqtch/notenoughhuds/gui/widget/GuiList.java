package io.github.reclqtch.notenoughhuds.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.MathHelper;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiList<T extends GuiList.GuiListEntry> extends GuiButton {
    protected final List<T> entries = new ArrayList<>();
    protected boolean dragging;
    protected int scrollToMouseOffset;
    public int scrollY;
    protected int scrollLength;
    public int length;

    public GuiList(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, null);
        scrollLength = height;
    }

    public void addEntry(T entry) {
        entries.add(entry);
        length = Math.max(length, entry.initialY + entry.height + 4);
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

    protected boolean hoveringScrollBar(int mouseX, int mouseY) {
        return scrollLength < height && mouseX >= xPosition + width - 2 && mouseY >= scrollY && mouseX < xPosition + width && mouseY < scrollY + scrollLength;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        for (T entry : entries) {
            if (entry.mousePressed(mc, mouseX, mouseY))
                return true;
        }
        if (hoveringScrollBar(mouseX, mouseY)) {
            dragging = true;
            scrollToMouseOffset = mouseY - scrollY;
            scrollTo(mouseY);
            return true;
        }
        return false;
    }

    @Override
    protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        for (T entry : entries) {
            entry.accessDrag(mc, mouseX, mouseY);
        }
        if (dragging) {
            scrollTo(mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        for (T entry : entries) {
            entry.mouseReleased(mouseX, mouseY);
        }
        dragging = false;
    }

    public void scrollTo(int mouseY) {
        if (scrollLength >= height) return;
        scrollY = MathHelper.clamp_int(mouseY - scrollToMouseOffset, yPosition, yPosition + height - scrollLength);
        for (T entry : entries) {
            entry.yPosition = entry.initialY - (scrollY - yPosition) * length / height;
        }
    }

    public void scrollBy(int wheel) {
        if (scrollLength >= height) return;
        scrollY = MathHelper.clamp_int(scrollY - wheel / 10, yPosition, yPosition + height - scrollLength);
        for (T entry : entries) {
            entry.yPosition = entry.initialY - (scrollY - yPosition) * length / height;
        }
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= xPosition && mouseY >= yPosition && mouseX < xPosition + width && mouseY < yPosition + height;
        mouseDragged(mc, mouseX, mouseY);
        for (T entry : entries) {
            entry.drawButton(mc, mouseX, mouseY);
        }
//        drawCenteredString(mc.fontRendererObj, displayString, xPosition + width / 2, yPosition + 4, 0xFFFFFF);
        if (scrollLength < height) drawRect(xPosition + width - 2, scrollY, xPosition + width, scrollY + scrollLength, dragging || hoveringScrollBar(mouseX, mouseY) ? 0xFF555555 : 0xFFAAAAAA);
    }

    public static class GuiListEntry extends GuiButtonExt {
        public final int initialY;

        public GuiListEntry(int x, int y, int widthIn, int heightIn, String buttonText) {
            super(32, x, y, widthIn, heightIn, buttonText);
            initialY = y;
        }

        public void accessDrag(Minecraft mc, int mouseX, int mouseY) {
            mouseDragged(mc, mouseX, mouseY);
        }
    }
}
