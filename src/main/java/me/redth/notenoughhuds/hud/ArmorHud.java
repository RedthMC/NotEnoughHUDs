package me.redth.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehEnum;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class ArmorHud extends BaseHud {
    public static final List<ItemStack> ARMORS = ImmutableList.of(new ItemStack(Items.diamond_helmet), new ItemStack(Items.diamond_chestplate), new ItemStack(Items.diamond_leggings), new ItemStack(Items.diamond_boots));
    public static final ItemStack SWORD = new ItemStack(Items.diamond_sword);
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor durColor = new NehColor("durability_color", "FFFFFFFF");
    public final NehBoolean dynamicDurColor = new NehBoolean("dynamic_durability_color", true);
    public final NehEnum durFormat = new NehEnum("durability_format", DurabilityFormat.MAX);
    public final NehBoolean showMainhand = new NehBoolean("show_mainhand", true);
    public final NehBoolean showArrow = new NehBoolean("show_arrow_count_on_bow", true);
    private List<ItemStack> items = Collections.emptyList();

    public ArmorHud() {
        super("armor");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(durColor);
        options.add(dynamicDurColor);
        options.add(durFormat);
        options.add(showMainhand);
        options.add(showArrow);
    }

    public static int getDurabilityColor(ItemStack item) {
        int i = (int) Math.round(item.getItem().getDurabilityForDisplay(item) * 255.0D);
        return i << 16 | (255 - i) << 8;
    }

    @Override
    public void render() {
        if (items.isEmpty()) return;
        drawBg(backgroundColor);

        int y = 1;
        int iconX;
        int textX;
        Alignment align;
        if (horAlign.get().equals(Alignment.RIGHT)) {
            iconX = width - 17;
            textX = iconX - 1;
            align = Alignment.RIGHT;
        } else {
            iconX = 1;
            textX = 18;
            align = Alignment.LEFT;
        }

        RenderItem ri = mc.getRenderItem();
        ri.zLevel = 100.0F;
        zLevel = 100.0F;
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        for (ItemStack item : items) {

            String count = null;
            if (item.getItem() instanceof ItemBow && showArrow.get()) {
                int arrows = 0;
                for (ItemStack arrow : mc.thePlayer.inventory.mainInventory) {
                    if (arrow != null && Items.arrow.equals(arrow.getItem())) {
                        arrows += arrow.stackSize;
                    }
                }
                count = String.valueOf(arrows);
            }

            ri.renderItemAndEffectIntoGUI(item, iconX, y);
            ri.renderItemOverlayIntoGUI(mc.fontRendererObj, item, iconX, y, count);

            if (item.isItemStackDamageable()) {
                int color = dynamicDurColor.get() ? getDurabilityColor(item) : durColor.asInt();
                drawString(((DurabilityFormat) durFormat.get()).format(item), textX, y + 4, color, textShadow.get(), align);
            }

            y += 18;
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.disableRescaleNormal();
        ri.zLevel = 0.0F;
        zLevel = 0.0F;
    }

    @Override
    protected int getWidth() {
        if (items.isEmpty()) return 0;
        int i = 0;
        for (ItemStack item : items) {
            if (!item.isItemStackDamageable()) continue;
            int j = mc.fontRendererObj.getStringWidth(((DurabilityFormat) durFormat.get()).format(item)) + 2;
            if (i < j) i = j;
        }
        return 18 + i;
    }

    @Override
    protected int getHeight() {
        if (items.isEmpty()) return 0;
        return 18 * items.size();
    }

    @Override
    public void tick() {
        items = getItems();
        super.tick();
    }

    public List<ItemStack> getItems() {
        if (isEditing()) return getPlaceholderItems();
        if (mc.thePlayer == null) return Collections.emptyList();
        InventoryPlayer inv = mc.thePlayer.inventory;
        List<ItemStack> items = Lists.newArrayList(inv.armorInventory[3], inv.armorInventory[2], inv.armorInventory[1], inv.armorInventory[0]);
        if (showMainhand.get()) items.add(inv.getCurrentItem());
        items.removeIf(Objects::isNull);
        return items;
    }

    public List<ItemStack> getPlaceholderItems() {
        List<ItemStack> items = new ArrayList<>(ARMORS);
        if (showMainhand.get()) items.add(SWORD);
        return items;
    }

    public enum DurabilityFormat implements NehEnum.EnumType {
        PERCENT {
            @Override
            public String format(ItemStack item) {
                return (int) Math.round(((1.0D - item.getItem().getDurabilityForDisplay(item)) * 100.0D)) + "%";
            }
        }, VALUE {
            @Override
            public String format(ItemStack item) {
                return String.valueOf(item.getMaxDamage() - item.getItemDamage());
            }
        }, MAX {
            @Override
            public String format(ItemStack item) {
                return (item.getMaxDamage() - item.getItemDamage()) + "/" + item.getMaxDamage();
            }
        };

        @Override
        public String getId() {
            return "durability_format";
        }

        public abstract String format(ItemStack item);

        public NehEnum.EnumType enumOf(String text) {
            return valueOf(text);
        }
    }
}
