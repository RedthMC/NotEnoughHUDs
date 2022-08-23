package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnderChestHud extends BaseHud {
    public final ResourceLocation CONTAINER_GUI = new ResourceLocation("notenoughhuds", "textures/container.png");
    public final NehBoolean showBg = new NehBoolean("show_background", true);
    private InventoryEnderChest enderChest = null;

    public EnderChestHud() {
        super("ender_chest");
        options.add(showBg);
    }

    @Override
    public void render() {
        drawTexture(CONTAINER_GUI, 0, 0, 0, 67, 176, 67);

        if (enderChest == null) return;

        int x = 8;
        int y = 7;

        RenderItem ri = mc.getRenderItem();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        for (int i = 0; i < 27; i++) {
            ItemStack item = enderChest.getStackInSlot(i);
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
        enderChest = mc.thePlayer == null ? null : mc.thePlayer.getInventoryEnderChest();
        super.tick();
    }
}
