package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.function.Consumer;

public class HudMenu extends GuiButtonExt {
    public static final int ENTRY_HEIGHT = 10;
    public final MenuEntry[] entries;
    public BaseHud hud;

    public HudMenu(MenuEntry... entries) {
        super(111, 0, 0, 64, entries.length * ENTRY_HEIGHT + 1, "");
        this.entries = entries;
    }

    public void show(BaseHud hud, int x, int y, boolean left, boolean top) {
        if (left) x -= width;
        if (top) y -= height;
        this.hud = hud;
        this.xPosition = x;
        this.yPosition = y;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            int i = (mouseY - yPosition) / ENTRY_HEIGHT;
            if (i >= entries.length) return false;
            entries[i].run(hud);
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF111111);
        DrawUtils.drawOutline(xPosition, yPosition, xPosition + width, yPosition + height, 0xFFFFFFFF);

        int i = 2;

        for (MenuEntry entry : entries) {
            mc.fontRendererObj.drawStringWithShadow(entry.name, xPosition + 2, yPosition + i, 0xFFFFFF);
            i += ENTRY_HEIGHT;
        }

        if (hovered) {
            i = (mouseY - yPosition) / ENTRY_HEIGHT;
            if (i < entries.length) {
                int y = yPosition + i * ENTRY_HEIGHT;
                drawRect(xPosition, y, xPosition + width, y + ENTRY_HEIGHT, 0x3FFFFFFF);
            }
        }
    }

    public static class MenuEntry {
        public final String name;
        public final Consumer<BaseHud> action;

        public MenuEntry(String name, Consumer<BaseHud> action) {
            this.name = name;
            this.action = action;
        }

        public void run(BaseHud hud) {
            action.accept(hud);
        }
    }
}
