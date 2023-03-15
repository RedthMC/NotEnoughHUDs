package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class DirectionHud extends Hud {
    public static final String[] DIRECTIONS = new String[] {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehInteger backgroundWidth = new NehInteger("background_width", 90, 90, 180);
    public final NehInteger backgroundHeight = new NehInteger("background_height", 16, 10, 110);
    private static final float[] lines = new float[32];
    private static final float[] directions = new float[8];

    public DirectionHud() {
        super("direction");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(textColor);
        options.add(backgroundWidth);
        options.add(backgroundHeight);
    }

    @Override
    public void tick() {
        float yaw = mc.thePlayer == null ? -94.323f : mc.thePlayer.rotationYaw;
        float f = width / 2.0F;

        for (int i = 0; i < 32; ++i) {
            lines[i] = f + wrapNscale(yaw, 32, i);
        }
        for (int i = 0; i < 8; ++i) {
            directions[i] = f + wrapNscale(yaw, 8, i);
        }

        super.tick();
    }

    @Override
    public void render() {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 20.0F);
        drawBackground(backgroundColor);
        GlStateManager.depthFunc(GL11.GL_EQUAL);
        float x1 = width / 2.0F;

        boolean isLong = false;
        float longs = height / 2.0F;
        float shorts = height / 4.0F;
        for (float f : lines) {
            drawCenteredVerticalLine(x1 + f, 0, isLong ? longs : shorts, 0xFFAAAAAA);
            isLong = !isLong;
        }

        int i = 0;
        int y1 = height - 8;
        for (float f : directions) {
            drawString(DIRECTIONS[i], x1 + f, y1, textColor.asInt(), textShadow.get(), Alignment.CENTER);
            ++i;
        }

        GlStateManager.depthFunc(GL11.GL_LEQUAL);
        drawCenteredVerticalLine(x1, 0, height, 0xFFFF5555);
        GlStateManager.popMatrix();
    }

    @Override
    protected int getWidth() {
        return backgroundWidth.get();
    }

    @Override
    protected int getHeight() {
        return backgroundHeight.get();
    }

    public float wrapNscale(float degrees, int into, int of) {
        return scale(MathHelper.wrapAngleTo180_float(360.0F / into * of - degrees));
    }

    public float scale(float degrees) {
        return degrees / 180.0F * getWidth();
    }

}
