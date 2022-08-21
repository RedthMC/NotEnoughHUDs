package io.github.redth.notenoughhuds.hud;

import io.github.redth.notenoughhuds.NotEnoughHUDs;
import io.github.redth.notenoughhuds.config.option.NehColor;
import io.github.redth.notenoughhuds.config.option.NehEnum;
import io.github.redth.notenoughhuds.config.option.NehFloat;
import io.github.redth.notenoughhuds.config.option.NehInteger;
import io.github.redth.notenoughhuds.config.option.NehOption;
import io.github.redth.notenoughhuds.gui.EditorScreen;
import io.github.redth.notenoughhuds.gui.SettingsScreen;
import io.github.redth.notenoughhuds.utils.DrawUtils;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseHud extends DrawUtils {
    protected static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    public static int screenWidth;
    public static int screenHeight;
    public final String id;
    public final List<NehOption<?>> options = new ArrayList<>();
    public final NehEnum horAlign;
    public final NehEnum verAlign;
    public final NehInteger xOffset;
    public final NehInteger yOffset;
    public final NehFloat scale = new NehFloat("scale", 1.0F, 0.5F, 2.0F);
    public long lastRendered;
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

    public BaseHud(String id, Alignment defaultHorAlign, Alignment defaultVerAlign, int defaultXOffset, int defaultYOffset) {
        this.id = id;
        options.add(horAlign = new NehEnum("horizontal_alignment", defaultHorAlign, true));
        options.add(verAlign = new NehEnum("vertical_alignment", defaultVerAlign, true));
        options.add(xOffset = new NehInteger("x_offset", defaultXOffset, true, -32767, 32767));
        options.add(yOffset = new NehInteger("y_offset", defaultYOffset, true, -32767, 32767));
        options.add(scale);
    }

    public void tick() {
        width = getWidth();
        height = getHeight();
        scaledWidth = (int) (width * scale.get());
        scaledHeight = (int) (height * scale.get());

        x = xOffset.get();
        if (horAlign.get() == Alignment.CENTER) x += (screenWidth - scaledWidth) / 2;
        else if (horAlign.get() == Alignment.RIGHT) x += screenWidth - scaledWidth;

        y = yOffset.get();
        if (verAlign.get() == Alignment.CENTER) y += (screenHeight - scaledHeight) / 2;
        else if (verAlign.get() == Alignment.BOTTOM) y += screenHeight - scaledHeight;
    }

    public abstract void render(MatrixStack matrix);

    protected abstract int getWidth();

    protected abstract int getHeight();

    public int getScaledWidth() {
        return scaledWidth;
    }

    public int getScaledHeight() {
        return scaledHeight;
    }

    public void render(MatrixStack matrix, int x, int y, boolean centered) {
        if (scaledWidth == 0.0F || scaledHeight == 0.0F) return;
        if (centered) {
            x -= scaledWidth / 2.0F;
            y -= scaledHeight / 2.0F;
        }
        matrix.push();
        matrix.translate(x, y, 0.0F);
        matrix.scale(scale.get(), scale.get(), 1.0F);
        render(matrix);
        matrix.pop();
    }

    public void renderScaled(MatrixStack matrix) {
        render(matrix, x, y, false);
    }

    public void renderPlaceholder(MatrixStack matrix, int x, int y) {
        render(matrix, x, y, true);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        int i = screenWidth - scaledWidth;
        this.x = x = MathHelper.clamp(x, 0, i);
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
        this.y = y = MathHelper.clamp(y, 0, i);
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

    public void drawPad(MatrixStack matrix, int color) {
        fill(matrix, x, y, x + scaledWidth, y + scaledHeight, color);
    }

    public void drawBg(MatrixStack matrix, NehColor backgroundColor) {
        int i = backgroundColor.asColor();
        if ((i >> 24 & 255) == 0) return;
        fill(matrix, 0, 0, width, height, i);
    }

    public enum Alignment implements NehEnum.EnumType {
        TOP, LEFT, CENTER, RIGHT, BOTTOM;

        @Override
        public String getId() {
            return "alignment";
        }
    }
}
