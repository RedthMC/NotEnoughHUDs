package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.NotEnoughHUDs;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehEnum;
import me.redth.notenoughhuds.config.option.NehInteger;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.gui.EditorScreen;
import me.redth.notenoughhuds.gui.SettingsScreen;
import me.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedList;
import java.util.List;

public abstract class BaseHud extends DrawUtils {
    protected static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    public static int screenWidth;
    public static int screenHeight;
    public final String id;
    public final ResourceLocation icon;
    public final List<NehOption<?>> options = new LinkedList<>();
    public final NehEnum horAlign = new NehEnum("horizontal_alignment", Alignment.CENTER);
    public final NehEnum verAlign = new NehEnum("vertical_alignment", Alignment.TOP);
    public final NehInteger xOffset = new NehInteger("x_offset", 0, -32767, 32767);
    public final NehInteger yOffset = new NehInteger("y_offset", 60, -32767, 32767);
    public final NehInteger scale = new NehInteger("scale", 100, 50, 200, i -> i + "%");
    private int x;
    private int y;
    protected int width;
    protected int height;
    protected int scaledWidth;
    protected int scaledHeight;

    public static boolean isEditing() {
        return mc.currentScreen instanceof EditorScreen || mc.currentScreen instanceof SettingsScreen;
    }

    public boolean isEnabled() {
        return neh.hudManager.getEnabledHuds().contains(this);
    }

    public void setEnabled(boolean enabled) {
        if (enabled) neh.hudManager.getEnabledHuds().add(this);
        else neh.hudManager.getEnabledHuds().remove(this);
    }

    public void toggleEnabled() {
        setEnabled(!isEnabled());
    }

    public String getTranslationKey() {
        return "hud.notenoughhuds." + id;
    }

    public BaseHud(String id) {
        this.id = id;
        icon = new ResourceLocation("notenoughhuds", "textures/icons/" + id + ".png");
        options.add(horAlign.hidden());
        options.add(verAlign.hidden());
        options.add(xOffset.hidden());
        options.add(yOffset.hidden());
        options.add(scale);
    }

    public float scaled() {
        return scale.get() / 100.0F;
    }

    public void tick() {
        width = getWidth();
        height = getHeight();
        scaledWidth = (int) (width * scaled());
        scaledHeight = (int) (height * scaled());

        x = xOffset.get();
        if (horAlign.get() == Alignment.CENTER) x += (screenWidth - scaledWidth) / 2;
        else if (horAlign.get() == Alignment.RIGHT) x += screenWidth - scaledWidth;

        y = yOffset.get();
        if (verAlign.get() == Alignment.CENTER) y += (screenHeight - scaledHeight) / 2;
        else if (verAlign.get() == Alignment.BOTTOM) y += screenHeight - scaledHeight;
    }

    public abstract void render();

    protected abstract int getWidth();

    protected abstract int getHeight();

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public void render(int x, int y, boolean centered) {
        if (scaledWidth == 0.0F || scaledHeight == 0.0F) return;
        float x1 = x;
        float y1 = y;
        if (centered) {
            x1 -= scaledWidth / 2.0F;
            y1 -= scaledHeight / 2.0F;
        }
        GlStateManager.pushMatrix();
        GlStateManager.translate(x1, y1, 0.0F);
        GlStateManager.scale(scaled(), scaled(), 1.0F);
        render();
        GlStateManager.popMatrix();
    }

    public void renderScaled() {
        render(x, y, false);
    }

    public void renderPlaceholder(int x, int y) {
        render(x, y, true);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        int i = screenWidth - scaledWidth;
        this.x = x = MathHelper.clamp_int(x, 0, i);
        if (x < 150) {
            horAlign.set(Alignment.LEFT);
            xOffset.set(x);
        } else if (x >= i - 150) {
            horAlign.set(Alignment.RIGHT);
            xOffset.set(x - i);
        } else {
            horAlign.set(Alignment.CENTER);
            xOffset.set(x - i / 2);
        }
    }

    public void setY(int y) {
        int i = screenHeight - scaledHeight;
        this.y = y = MathHelper.clamp_int(y, 0, i);
        if (y < 100) {
            verAlign.set(Alignment.TOP);
            yOffset.set(y);
        } else if (y >= i - 100) {
            verAlign.set(Alignment.BOTTOM);
            yOffset.set(y - i);
        } else {
            verAlign.set(Alignment.CENTER);
            yOffset.set(y - i / 2);
        }
    }

    public void resetPosition() {
        xOffset.reset();
        yOffset.reset();
        horAlign.reset();
        verAlign.reset();
        scale.reset();
    }

    public void drawPad(int color) {
        drawRect(x, y, x + scaledWidth, y + scaledHeight, color);
    }

    public void drawPad(int x, int y, int color) {
        if ((color >> 24 & 255) == 0) return;
        drawRect(x, y, x + width, y + height, color);
    }

    public void drawBg(NehColor backgroundColor) {
        drawPad(0, 0, backgroundColor.asInt());
    }

    public enum Alignment implements NehEnum.EnumType {
        TOP, LEFT, CENTER, RIGHT, BOTTOM;

        @Override
        public String getId() {
            return "alignment";
        }
    }
}
