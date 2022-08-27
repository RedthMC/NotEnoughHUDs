package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class InventoryHud extends ItemHud {
    public static final Identifier CONTAINER_GUI = new Identifier("notenoughhuds", "textures/container.png");
    public final NehBoolean showBackground = new NehBoolean("show_background", true);
    private List<ItemStack> inventory = null;

    public InventoryHud() {
        super("inventory");
        options.add(showBackground);
    }

    @Override
    public void render(MatrixStack matrix) {
        if (showBackground.get()) drawTexture(matrix, CONTAINER_GUI, 0, 0, 0, 0, 176, 67);
        if (inventory == null) return;
        int x = 8;
        int y = 7;
        pushDrawItem();
        for (ItemStack item : inventory) {
            if (x > 152) {
                x = 8;
                y += 18;
            }
            drawItem(item, x, y, null);
            x += 18;
        }
        popDrawItem();
        // fix lining
    }

    @Override
    protected int getWidth() {
        return 176;
    }

    @Override
    protected int getHeight() {
        return 67;
    }

    @Override
    public void tick() {
        inventory = mc.player == null ? null : mc.player.getInventory().main.subList(9, 36);
        super.tick();
    }
}
