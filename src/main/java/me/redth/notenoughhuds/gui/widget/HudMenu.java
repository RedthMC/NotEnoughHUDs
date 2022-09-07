package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HudMenu extends GuiButtonExt {
    public static final int ENTRY_HEIGHT = 10;
    public final BaseHud hud;
    public final List<MenuEntry> entries = new ArrayList<>();

    public HudMenu(BaseHud hud, int xPos, int yPos) {
        super(111, xPos, yPos, 64, 0, "");
        this.hud = hud;
    }

    public HudMenu add(String name, Consumer<BaseHud> action) {
        entries.add(new MenuEntry(name, action));
        height += ENTRY_HEIGHT;
        return this;
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            int i = (mouseY - yPosition) / ENTRY_HEIGHT;
            if (i >= entries.size()) return false;
            entries.get((mouseY - yPosition) / ENTRY_HEIGHT).run();
            return true;
        }
        return false;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF111111);
        DrawUtils.drawOutline(xPosition, yPosition, xPosition + width, yPosition + height, 0xFFFFFFFF);
        int i = 1;
        for (MenuEntry entry : entries) {
            mc.fontRendererObj.drawStringWithShadow(entry.name, xPosition + 2, yPosition + i, 0xFFCCCCCC);
            i += ENTRY_HEIGHT;
        }
        if (hovered) {
            i = (mouseY - yPosition) / ENTRY_HEIGHT;
            if (i < entries.size()) {
                int y = yPosition + i * ENTRY_HEIGHT;
                drawRect(xPosition, y, xPosition + width, y + ENTRY_HEIGHT, 0x50FFFFFF);
            }
        }
    }

    public class MenuEntry {
        public final String name;
        public final Consumer<BaseHud> action;

        public MenuEntry(String name, Consumer<BaseHud> action) {
            this.name = name;
            this.action = action;
        }

        public void run() {
            action.accept(hud);
        }
    }
}
