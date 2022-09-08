package me.redth.notenoughhuds.gui.widget;

import me.redth.notenoughhuds.hud.BaseHud;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HudMenu extends ButtonWidget {
    public static final int ENTRY_HEIGHT = 10;
    public final BaseHud hud;
    public final List<MenuEntry> entries = new ArrayList<>();

    public HudMenu(BaseHud hud, int x, int y) {
        super(x, y, 64, 0, Text.empty(), b -> {
        });
        this.hud = hud;
    }

    public HudMenu add(String name, Consumer<BaseHud> action) {
        entries.add(new MenuEntry(Text.translatable("screen.notenoughhuds." + name), action));
        height += ENTRY_HEIGHT;
        return this;
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        int i = ((int) mouseY - y) / ENTRY_HEIGHT;
        if (i >= entries.size())
            return;
        entries.get((int) (mouseY - y) / ENTRY_HEIGHT).run();
    }

    @Override
    public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float delta) {
        fill(matrix, x, y, x + width, y + height, 0xFF111111);
        DrawUtils.drawOutline(matrix, x, y, x + width, y + height, 0xFFFFFFFF);
        int i = 1;
        for (MenuEntry entry : entries) {
            MinecraftClient.getInstance().textRenderer.drawWithShadow(matrix, entry.name, x + 2, y + i, 0xFFCCCCCC);
            i += ENTRY_HEIGHT;
        }
        if (hovered) {
            i = (mouseY - y) / ENTRY_HEIGHT;
            if (i < entries.size()) {
                int y2 = y + i * ENTRY_HEIGHT;
                fill(matrix, x, y2, x + width, y2 + ENTRY_HEIGHT, 0x50FFFFFF);
            }
        }
    }

    public class MenuEntry {
        public final Text name;
        public final Consumer<BaseHud> action;

        public MenuEntry(Text name, Consumer<BaseHud> action) {
            this.name = name;
            this.action = action;
        }

        public void run() {
            action.accept(hud);
        }
    }
}
