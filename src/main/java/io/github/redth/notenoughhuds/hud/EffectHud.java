package io.github.redth.notenoughhuds.hud;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EffectHud extends BaseHud {
    public static final List<StatusEffectInstance> PLACEHOLDER = ImmutableList.of(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 60000), new StatusEffectInstance(StatusEffects.SPEED, 20));
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showName = new NehBoolean("show_name", true);
    public final NehBoolean staticNameColor = new NehBoolean("static_name_color", true);
    public final NehColor nameOrAmpColor = new NehColor("name_or_amplifier_color", "FFFFFFFF");
    public final NehColor durationColor = new NehColor("duration_color", "FFAAAAAA");
    private static List<StatusEffectInstance> effects = Collections.emptyList();

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

    public static List<StatusEffectInstance> getEffects() {
        if (isEditing()) return PLACEHOLDER;
        if (mc.player == null) return Collections.emptyList();
        List<StatusEffectInstance> list = new ArrayList<>(mc.player.getStatusEffects());
        list.sort(Comparator.comparingInt(StatusEffectInstance::getDuration));
        return list;
    }

    @Override
    public void render(MatrixStack matrix) {
        if (effects.isEmpty()) return;

        drawBg(matrix, backgroundColor);

        if (showName.get()) {
            int iconX;
            int textX;
            int y = 2;
            Alignment align;

            if (horAlign.get().equals(Alignment.RIGHT)) {
                iconX = width - 22;
                textX = iconX - 1;
                align = Alignment.RIGHT;
            } else {
                iconX = 2;
                textX = 22;
                align = Alignment.LEFT;
            }

            for (StatusEffectInstance effect : effects) {
                drawIcon(matrix, iconX, y, effect);

                int color = staticNameColor.get() ? nameOrAmpColor.asColor() : effect.getEffectType().getColor();

                drawString(matrix, getNameAmp(effect), textX, y, color, textShadow.get(), align); // name + amp
                drawString(matrix, StringHelper.formatTicks(effect.getDuration()), textX, y + 10, durationColor.asColor(), textShadow.get(), align); // dur

                y += 20;
            }

        } else {
            int x = 7;
            for (StatusEffectInstance effect : effects) {
                drawIcon(matrix, x, 2, effect);
                int color = staticNameColor.get() ? nameOrAmpColor.asColor() : effect.getEffectType().getColor();
                drawString(matrix, amplifierToString(effect.getAmplifier()), x + 23, 12, color, textShadow.get(), Alignment.RIGHT); // amp
                drawString(matrix, StringHelper.formatTicks(effect.getDuration()), x + 11, 22, durationColor.asColor(), textShadow.get(), Alignment.CENTER); // dur
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
            for (StatusEffectInstance effect : effects) {
                int j = mc.textRenderer.getWidth(getNameAmp(effect));
                if (i < j) i = j;
                j = mc.textRenderer.getWidth(StringHelper.formatTicks(effect.getDuration()));
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

    public void drawIcon(MatrixStack matrix, int x, int y, StatusEffectInstance effect) {
        Sprite sprite = mc.getStatusEffectSpriteManager().getSprite(effect.getEffectType());
        RenderSystem.setShaderTexture(0, sprite.getAtlas().getId());
        drawSprite(matrix, x, y, this.getZOffset(), 18, 18, sprite);
    }

    public static String amplifierToString(int amp) {
        return amp == 0 ? "" : String.valueOf(amp + 1);
    }

    public static String getNameAmp(StatusEffectInstance effect) {
        String name = I18n.translate(effect.getTranslationKey());
        if (effect.getDuration() != 0) name += " " + amplifierToString(effect.getAmplifier());
        return name;
    }

}
