package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent;

import java.util.List;

public class EnderChestHud extends BaseHud {
    public static final IChatComponent EC_TEXT = new ChatComponentTranslation("container.enderchest");
    public static final ResourceLocation CONTAINER_GUI = new ResourceLocation("notenoughhuds", "textures/container.png");
    public final NehBoolean showBg = new NehBoolean("show_background", true);
    private IInventory enderChest = null;

    public EnderChestHud() {
        super("ender_chest");
        options.add(showBg);
    }

    @Override
    public void render() {
        if (showBg.get()) drawTexture(CONTAINER_GUI, 0, 0, 0, 67, 176, 67);

        if (enderChest == null) return;

        int x = 8;
        int y = 7;

        RenderItem ri = mc.getRenderItem();
        zLevel = 100.0F;
        ri.zLevel = 100.0F;
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
        zLevel = 0.0F;
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

    public void onInitGui(GuiScreenEvent.InitGuiEvent.Post e) {
        if (e.gui instanceof GuiChest) {
            IInventory chest = ((ContainerChest) mc.thePlayer.openContainer).getLowerChestInventory();
            if (EC_TEXT.getUnformattedText().equals(chest.getName())) {
                enderChest = chest;
            }
        }

    }

}
