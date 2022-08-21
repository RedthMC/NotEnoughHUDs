package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import io.github.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.biome.Biome;

import java.util.stream.IntStream;

public class CoordsHud extends BaseHud {
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehBoolean showYaw = new NehBoolean("show_yaw", true);
    public final NehBoolean showPitch = new NehBoolean("show_pitch", true);
    public final NehBoolean showBiome = new NehBoolean("show_biome", true);
    public final NehInteger precision = new NehInteger("precision", 1, 0, 10);
    public final NehColor labelColor = new NehColor("label_color", "FFFFAA00");
    public final NehColor valueColor = new NehColor("value_color", "FFFFFFFF");
    private String posX;
    private String posY;
    private String posZ;
    private String yaw;
    private String pitch;
    private String biome;

    public CoordsHud() {
        super("coords", Alignment.LEFT, Alignment.CENTER, 0, 0);
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
        getLines();
        super.tick();
    }

    @Override
    public void render(MatrixStack matrix) {
        drawBg(matrix, backgroundColor);
        int y = 2;
        y = drawLine(matrix, "X: ", posX, y);
        y = drawLine(matrix, "Y: ", posY, y);
        y = drawLine(matrix, "Z: ", posZ, y);
        y = drawLine(matrix, "Yaw: ", yaw, y);
        y = drawLine(matrix, "Pitch: ", pitch, y);
        y = drawLine(matrix, "Biome: ", biome, y);
    }

    @Override
    protected int getWidth() {
        return IntStream.of(getWidth("X: ", posX), getWidth("Y: ", posY), getWidth("Z: ", posZ), getWidth("Yaw: ", yaw), getWidth("Pitch: ", pitch), getWidth("Biome: ", biome)).max().getAsInt();
    }

    @Override
    protected int getHeight() {
        int i = 27;
        if (showYaw.get()) i += 9;
        if (showPitch.get()) i += 9;
        if (showBiome.get()) i += 9;
        return i + 3;
    }

    public void getLines() {
        Entity e = mc.cameraEntity;
        if (e == null) {
            if (isEditing()) {
                boolean asBlock = precision.get() == 0;
                posX = asBlock ? "150" : formatPrecision(150.542d);
                posY = asBlock ? "64" : formatPrecision(64.375d);
                posZ = asBlock ? "-75" : formatPrecision(-74.53d);
                if (showYaw.get()) yaw = formatPrecision(-94.323f);
                if (showPitch.get()) pitch = formatPrecision(34.323f);
                if (showBiome.get()) biome = "Plains";
            } else posX = posY = posZ = yaw = pitch = biome = null;
        } else {
            boolean asBlock = precision.get() == 0;
            BlockPos bp = e.getBlockPos();
            posX = asBlock ? String.valueOf(bp.getX()) : formatPrecision(e.getX());
            posY = asBlock ? String.valueOf(bp.getY()) : formatPrecision(e.getY());
            posZ = asBlock ? String.valueOf(bp.getZ()) : formatPrecision(e.getZ());
            if (showYaw.get()) yaw = formatPrecision(MathHelper.wrapDegrees(e.getYaw()));
            if (showPitch.get()) pitch = formatPrecision(MathHelper.wrapDegrees(e.getPitch()));
            if (showBiome.get()) biome = biomeToString(mc.world.getBiome(bp));
        }
    }

    public String formatPrecision(double d) {
        return String.format("%." + precision.get() + "f", d);
    }

    public int drawLine(MatrixStack matrix, String label, String value, int y) {
        if (value == null) return y;
        int x1 = drawString(matrix, label, 2, y, labelColor.asColor(), textShadow.get()) - 1;
        drawString(matrix, value, x1, y, valueColor.asColor(), textShadow.get());
        return y + 9;
    }

    public int getWidth(String label, String value) {
        return value == null ? 0 : mc.textRenderer.getWidth(label + value);
    }

    public static String biomeToString(RegistryEntry<Biome> biome) {
        if (biome.getKey().isEmpty()) return "Unknown";
        Identifier id = biome.getKey().get().getValue();
        String s = "biome." + id.getNamespace() + "." + id.getPath();
        return I18n.hasTranslation(s) ? I18n.translate(s) : id.getNamespace() + ":" + id.getPath();
    }
}
