package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.config.option.NehBoolean;
import io.github.redth.notenoughhuds.config.option.NehColor;
import io.github.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

public class DirectionHud extends BaseHud {
    public static final String[] NAMES = new String[] {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehInteger backgroundWidth = new NehInteger("background_width", 90, 90, 180);
    public final NehInteger backgroundHeight = new NehInteger("background_height", 16, 10, 110);
    private static final float[] lines = new float[32];
    private static final float[] directions = new float[8];

    public DirectionHud() {
        super("direction", Alignment.LEFT, Alignment.CENTER, 0, 0);
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(textColor);
        options.add(backgroundWidth);
        options.add(backgroundHeight);
    }

//    @Override
//    public void tick() {
//        float yaw = mc.cameraEntity == null ? -94.323f : mc.cameraEntity.getYaw();
//        float f = width / 2.0F;
//
//        for (int i = 0; i < 32; ++i) {
//            lines[i] = f + wrapNscale(yaw, 32, i);
//        }
//        for (int i = 0; i < 8; ++i) {
//            directions[i] = f + wrapNscale(yaw, 8, i);
//        }
//
//        super.tick();
//    }

    @Override
    public void render(MatrixStack matrix) {
        float yaw = mc.cameraEntity == null ? -180.0F : mc.cameraEntity.getYaw();
//        float mid = width / 2.0F;

        for (int i = 0; i < 32; ++i) {
            lines[i] = scaleOf(yaw, 32, i);
        }
        for (int i = 0; i < 8; ++i) {
            directions[i] = scaleOf(yaw, 8, i);
        }

        drawBg(matrix, backgroundColor);

        float x = width / 2.0F;

        boolean isLong = false;
        float longs = height / 2.0F;
        float shorts = height / 4.0F;
        for (float f : lines) {
            if (-0.5 < f && f < 0.5F) drawVerticalLine(matrix, x + f * width, 0, isLong ? longs : shorts, 0xFFAAAAAA);
            isLong = !isLong;
        }

        int i = 0;
        int y = height - 8;
        for (float f : directions) {
            if (-0.5 < f && f < 0.5F) drawString(matrix, NAMES[i], x + f * width, y, textColor.asColor(), textShadow.get(), Alignment.CENTER);
            i++;
        }

        drawVerticalLine(matrix, x, 0, height, 0xFFFF5555);
    }

    @Override
    protected int getWidth() {
        return backgroundWidth.get();
    }

    @Override
    protected int getHeight() {
        return backgroundHeight.get();
    }

    public float scaleOf(float degrees, int into, int of) {
        return MathHelper.wrapDegrees(360.0F / into * of - degrees) / 180.0F;
    }

}
