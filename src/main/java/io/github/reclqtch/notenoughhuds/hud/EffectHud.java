package io.github.reclqtch.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import io.github.reclqtch.notenoughhuds.config.option.NehBoolean;
import io.github.reclqtch.notenoughhuds.config.option.NehColor;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EffectHud extends BaseHud {
    public static final List<PotionEffect> PLACEHOLDER = ImmutableList.of(new PotionEffect(Potion.fireResistance.id, 60000), new PotionEffect(Potion.moveSpeed.id, 20));
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showName = new NehBoolean("show_name", true);
    public final NehBoolean staticNameColor = new NehBoolean("static_name_color", false);
    public final NehColor nameOrAmpColor = new NehColor("name_or_amplifier_color", "FFFFFFFF");
    public final NehColor durationColor = new NehColor("duration_color", "FFAAAAAA");
    private static List<PotionEffect> effects = Collections.emptyList();

    @Override
    public void tick() {
        effects = getEffects();
        super.tick();
    }

    public EffectHud() {
        super("effect");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showName);
        options.add(staticNameColor);
        options.add(nameOrAmpColor);
        options.add(durationColor);
    }

    public static List<PotionEffect> getEffects() {
        if (isEditing()) return PLACEHOLDER;
        if (mc.thePlayer == null) return Collections.emptyList();
        List<PotionEffect> list = new ArrayList<>(mc.thePlayer.getActivePotionEffects());
        list.sort(Comparator.comparingInt(PotionEffect::getDuration));
        return list;
    }

    public static Potion getPotionFromEffect(PotionEffect effect) {
        return GameData.getPotionRegistry().getRaw(effect.getPotionID());
    }

    @Override
    public void render() {
        if (effects.isEmpty()) return;
        drawBg(backgroundColor);

        if (showName.get()) {
            int y = 2;
            int iconX;
            int textX;
            Alignment align;
            if (horAlign.get().equals(Alignment.RIGHT)) {
                iconX = getWidth() - 18;
                textX = iconX - 1;
                align = Alignment.RIGHT;
            } else {
                iconX = 2;
                textX = 22;
                align = Alignment.LEFT;
            }
            for (PotionEffect effect : effects) {
                int color = staticNameColor.get() ? getPotionFromEffect(effect).getLiquidColor() : nameOrAmpColor.asInt();
                String name = getContacted(effect);
                String duration = Potion.getDurationString(effect);

                drawIcon(iconX, y, effect);
                drawString(name, textX, y, color, textShadow.get(), align);
                drawString(duration, textX, y + 10, durationColor.asInt(), textShadow.get(), align);
                y += 20;
            }
        } else {
            int x = 7;

            for (PotionEffect effect : effects) {
                int color = staticNameColor.get() ? getPotionFromEffect(effect).getLiquidColor() : nameOrAmpColor.asInt();
                String duration = Potion.getDurationString(effect);

                drawIcon(x, 3, effect);
                drawString(amplifierToString(effect.getAmplifier()), x + 23, 12, color, textShadow.get(), Alignment.RIGHT);
                drawString(duration, x + 9, 22, durationColor.asInt(), textShadow.get(), Alignment.CENTER);
                x += 28;
            }
        }
    }

    @Override
    protected int getWidth() {
        if (effects.isEmpty()) return 0;
        int width;
        if (showName.get()) {
            width = 20;
            int i = 0;
            for (PotionEffect effect : effects) {
                int j = mc.fontRendererObj.getStringWidth(getContacted(effect));
                if (i < j) i = j;
                j = mc.fontRendererObj.getStringWidth(Potion.getDurationString(effect));
                if (i < j) i = j;
            }
            width += i + 4;
        } else {
            width = 28 * effects.size();
        }
        return width;
    }

    @Override
    protected int getHeight() {
        if (effects.isEmpty()) return 0;
        int height;
        if (showName.get()) {
            height = 20 * effects.size();
        } else {
            height = 32;
        }
        return height;
    }

    public void drawIcon(int x, int y, PotionEffect effect) {
        if (getPotionFromEffect(effect).hasStatusIcon()) {
            int i = getPotionFromEffect(effect).getStatusIconIndex();
            drawTexture(new ResourceLocation("textures/gui/container/inventory.png"), x, y, i % 8 * 18, 198 + i / 8 * 18, 18, 18);
        }
    }

    public static String amplifierToString(int amp) {
        return amp == 0 ? "" : String.valueOf(amp + 1);
    }

    public static String getContacted(PotionEffect effect) {
        String name = I18n.format(effect.getEffectName());
        if (effect.getAmplifier() != 0) name += (effect.getAmplifier() + 1);
        return name;
    }
}
