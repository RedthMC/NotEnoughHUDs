package me.redth.notenoughhuds.hud;

import com.mojang.realmsclient.util.Pair;
import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CoordsHud extends BaseHud {
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showYaw = new NehBoolean("show_yaw", true);
    public final NehBoolean showPitch = new NehBoolean("show_pitch", true);
    public final NehBoolean showBiome = new NehBoolean("show_biome", true);
    public final NehInteger precision = new NehInteger("precision", 1, 0, 10);
    public final NehColor labelColor = new NehColor("label_color", "FFFFAA00");
    public final NehColor valueColor = new NehColor("value_color", "FFFFFFFF");
    public List<Pair<String, String>> lines = Collections.emptyList();

    public CoordsHud() {
        super("coords");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(showYaw);
        options.add(showPitch);
        options.add(showBiome);
        options.add(precision);
        options.add(labelColor);
        options.add(valueColor);
    }

    @Override
    public void tick() {
        lines = getLines();
        super.tick();
    }

    @Override
    public void render() {
        drawBackground(backgroundColor);
        int x = 2;
        int y = 2;
        for (Pair<String, String> entry : lines) {
            int x1 = drawString(entry.first(), x, y, labelColor.asInt(), textShadow.get()) - 1;
            drawString(entry.second(), x1, y, valueColor.asInt(), textShadow.get());
            y += mc.fontRendererObj.FONT_HEIGHT;
        }
    }

    @Override
    protected int getWidth() {
        int i = 0;
        for (Pair<String, String> entry : lines) {
            int j = mc.fontRendererObj.getStringWidth(entry.first() + entry.second());
            if (i < j) i = j;
        }
        return i + 3;
    }

    @Override
    protected int getHeight() {
        return lines.size() * mc.fontRendererObj.FONT_HEIGHT + 2;
    }

    public List<Pair<String, String>> getLines() {
        Entity e = mc.getRenderViewEntity();
        if (e == null) return isEditing() ? getDefault() : Collections.emptyList();

        List<Pair<String, String>> lines = new ArrayList<>();
        boolean asBlock = precision.get() == 0;
        BlockPos bp = e.getPosition();
        lines.add(Pair.of("X: ", asBlock ? String.valueOf(bp.getX()) : formatPrecision(e.posX)));
        lines.add(Pair.of("Y: ", asBlock ? String.valueOf(bp.getY()) : formatPrecision(e.posY)));
        lines.add(Pair.of("Z: ", asBlock ? String.valueOf(bp.getZ()) : formatPrecision(e.posZ)));
        if (showYaw.get()) lines.add(Pair.of("Yaw: ", formatPrecision(MathHelper.wrapAngleTo180_float(e.rotationYaw))));
        if (showPitch.get()) lines.add(Pair.of("Pitch: ", formatPrecision(MathHelper.wrapAngleTo180_float(e.rotationPitch))));
        if (showBiome.get()) lines.add(Pair.of("Biome: ", mc.theWorld.getBiomeGenForCoords(e.getPosition()).biomeName));
        return lines;
    }

    public List<Pair<String, String>> getDefault() {
        List<Pair<String, String>> lines = new ArrayList<>();
        boolean asBlock = precision.get() == 0;
        lines.add(Pair.of("X: ", asBlock ? "150" : formatPrecision(150.542d)));
        lines.add(Pair.of("Y: ", asBlock ? "64" : formatPrecision(64.375d)));
        lines.add(Pair.of("Z: ", asBlock ? "-75" : formatPrecision(-74.53d)));
        if (showYaw.get()) lines.add(Pair.of("Yaw: ", formatPrecision(-94.323f)));
        if (showPitch.get()) lines.add(Pair.of("Pitch: ", formatPrecision(34.323f)));
        if (showBiome.get()) lines.add(Pair.of("Biome: ", "Plains"));
        return lines;
    }

    public String formatPrecision(double d) {
        return String.format("%." + precision.get() + "f", d);
    }
}
