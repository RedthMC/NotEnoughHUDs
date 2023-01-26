package me.redth.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehEnum;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class ArmorHud extends ItemHud {
    public static final List<ItemStack> ARMORS = ImmutableList.of(new ItemStack(Items.NETHERITE_HELMET), new ItemStack(Items.NETHERITE_CHESTPLATE), new ItemStack(Items.NETHERITE_LEGGINGS), new ItemStack(Items.NETHERITE_BOOTS));
    public static final ItemStack SWORD = new ItemStack(Items.NETHERITE_SWORD);
    public static final ItemStack TOTEM = new ItemStack(Items.TOTEM_OF_UNDYING);
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor durColor = new NehColor("durability_color", "FFFFFFFF");
    public final NehBoolean dynamicDurColor = new NehBoolean("dynamic_durability_color", true);
    public final NehEnum durFormat = new NehEnum("durability_format", DurabilityFormat.MAX);
    public final NehBoolean showMainhand = new NehBoolean("show_mainhand", true);
    public final NehBoolean showOffhand = new NehBoolean("show_offhand", true);
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
        options.add(showOffhand);
        options.add(showArrow);
    }

    @Override
    public void render(MatrixStack matrix) {
        if (items.isEmpty()) return;
        drawBackground(matrix, backgroundColor);


        int y = 1;
        int itemX;
        int textX;

        Alignment textAlign;
        if (horAlign.get() == Alignment.RIGHT) {
            itemX = width - 17;
            textX = itemX - 1;
            textAlign = Alignment.RIGHT;
        } else {
            itemX = 1;
            textX = itemX + 17;
            textAlign = Alignment.LEFT;
        }

        for (ItemStack item : items) {
            String count = (item.getItem() instanceof RangedWeaponItem) && showArrow.get() ? String.valueOf(mc.player.getArrowType(item).getCount()) : null;

            pushDrawItem();
            drawItem(item, itemX, y, count);
            popDrawItem();

            if (item.isDamageable()) {
                int color = dynamicDurColor.get() ? item.getItemBarColor() : durColor.asColor();
                drawString(matrix, ((DurabilityFormat) this.durFormat.get()).format(item), textX, y + 4, color, textShadow.get(), textAlign);
            }
            y += 18;
        }
    }

    @Override
    protected int getWidth() {
        if (items.isEmpty()) return 0;
        int i = 0;
        for (ItemStack item : items) {
            if (!item.isDamageable()) continue;
            int j = mc.textRenderer.getWidth(((DurabilityFormat) durFormat.get()).format(item)) + 2;
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
        if (mc.player == null) return Collections.emptyList();
        PlayerInventory inv = mc.player.getInventory();
        List<ItemStack> items = new ArrayList<>(inv.armor);
        if (showMainhand.get()) items.add(inv.getMainHandStack());
        if (showOffhand.get()) items.add(inv.offHand.get(0));
        items.removeIf(ItemStack::isEmpty);
        return items;
    }

    public List<ItemStack> getPlaceholderItems() {
        List<ItemStack> items = new ArrayList<>(ARMORS);
        if (showMainhand.get()) items.add(SWORD);
        if (showOffhand.get()) items.add(TOTEM);
        return items;
    }

    public enum DurabilityFormat implements NehEnum.EnumType {
        PERCENT(item -> (int) Math.round((1.0D - (double) item.getDamage() / (double) item.getMaxDamage()) * 100.0D) + "%"),
        VALUE(item -> String.valueOf(item.getMaxDamage() - item.getDamage())),
        MAX(item -> (item.getMaxDamage() - item.getDamage()) + "/" + item.getMaxDamage());

        private final Function<ItemStack, String> formatter;

        DurabilityFormat(Function<ItemStack, String> formatter) {
            this.formatter = formatter;
        }

        @Override
        public String getId() {
            return "durability_format";
        }

        public String format(ItemStack item) {
            return formatter.apply(item);
        }

    }

}
