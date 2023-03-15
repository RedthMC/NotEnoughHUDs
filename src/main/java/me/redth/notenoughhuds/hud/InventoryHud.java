package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.List;

public class InventoryHud extends Hud {
    public final ResourceLocation CONTAINER_GUI = new ResourceLocation("notenoughhuds", "textures/inventory.png");
    public final NehBoolean showBg = new NehBoolean("show_background", true);
    private List<ItemStack> inventory = null;

    public InventoryHud() {
        super("inventory");
        options.add(showBg);
    }

    @Override
    public void render() {
        if (showBg.get()) drawTexture(CONTAINER_GUI, 0, 0, 0, 0, 176, 67, 176, 67);

        if (inventory == null) return;

        int x = 8;
        int y = 7;

        RenderItem ri = mc.getRenderItem();
        zLevel = 100.0F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        for (ItemStack item : inventory) {
            if (x > 152) {
                x = 8;
                y += 18;
            }
            ri.renderItemAndEffectIntoGUI(item, x, y);
            ri.renderItemOverlayIntoGUI(mc.fontRendererObj, item, x, y, null);
            x += 18;
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        ri.zLevel = 0.0F;

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
        inventory = mc.thePlayer == null ? null : Arrays.asList(mc.thePlayer.inventory.mainInventory).subList(9, 36);
        super.tick();
    }
}
