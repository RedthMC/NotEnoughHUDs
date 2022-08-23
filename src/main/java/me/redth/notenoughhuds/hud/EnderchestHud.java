package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.inventory.EnderChestInventory;
import net.minecraft.util.Identifier;

public class EnderchestHud extends ItemHud {
    public static final Identifier CONTAINER_GUI = new Identifier("notenoughhuds", "textures/container.png");
    public final NehBoolean showBackground = new NehBoolean("show_background", true);
    private EnderChestInventory enderchest = null;

    public EnderchestHud() {
        super("enderchest", Alignment.LEFT, Alignment.CENTER, 0, 0);
        options.add(showBackground);
    }

    @Override
    public void render(MatrixStack matrix) {
        if (showBackground.get()) drawTexture(matrix, CONTAINER_GUI, 0, 0, 0, 67, 176, 67);
        if (enderchest == null) return;
        int x = 8;
        int y = 7;
        pushDrawItem();
        for (int i = 0; i < 27; ++i) {
            if (x > 152) {
                x = 8;
                y += 18;
            }
            drawItem(enderchest.getStack(i), x, y, null);
            x += 18;
        }
        popDrawItem();
    }

    @Override
    protected int getWidth() {
        return 176;
    }

    @Override
    protected int getHeight() {
        return 67;
    }

    public void onECUpdate(EnderChestInventory inv) {
        enderchest = inv;
    }
}
