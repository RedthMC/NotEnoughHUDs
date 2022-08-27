package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

public class KeystrokesHud extends BaseHud {
    public final NehBoolean textShadow = new NehBoolean("text_shadow", true);
    public final NehColor backgroundColor = new NehColor("background_color", "80000000");
    public final NehColor textColor = new NehColor("text_color", "FFFFFFFF");
    public final NehColor pressedBackgroundColor = new NehColor("pressed_background_color", "80FFFFFF");
    public final NehColor pressedTextColor = new NehColor("pressed_text_color", "FF000000");
    public final NehInteger keySize = new NehInteger("key_size", 9, 7, 15);
    public final NehBoolean showMouseButtons = new NehBoolean("show_mouse_buttons", true);
    public final NehBoolean showCps = new NehBoolean("show_cps", true);
    public final NehBoolean showSpace = new NehBoolean("show_space", true);
    public final NehInteger fadeTime = new NehInteger("fade_time", 100, 10, 250, i -> i + " ms");
    public Key forward;
    public Key left;
    public Key back;
    public Key right;
    public Key jump;
    public Key attack;
    public Key use;


    public KeystrokesHud() {
        super("keystrokes");
        options.add(textShadow);
        options.add(backgroundColor);
        options.add(textColor);
        options.add(pressedBackgroundColor);
        options.add(pressedTextColor);
        options.add(keySize);
        options.add(showMouseButtons);
        options.add(showCps);
        options.add(showSpace);
        options.add(fadeTime);
        mc.execute(() -> {
            forward = new Key(mc.options.forwardKey);
            left = new Key(mc.options.leftKey);
            back = new Key(mc.options.backKey);
            right = new Key(mc.options.rightKey);
            jump = new Key(mc.options.jumpKey);
            attack = new Key(mc.options.attackKey);
            use = new Key(mc.options.useKey);
        });
    }

    public static int getFadedColor(int from, int to, double percent) {
        int redU = from >> 16 & 0xFF;
        int greenU = from >> 8 & 0xFF;
        int blueU = from & 0xFF;
        int alphaU = from >> 24 & 0xFF;

        int redP = to >> 16 & 0xFF;
        int greenP = to >> 8 & 0xFF;
        int blueP = to & 0xFF;
        int alphaP = to >> 24 & 0xFF;

        int red = getFadedInt(redU, redP, percent) & 0xFF;
        int green = getFadedInt(greenU, greenP, percent) & 0xFF;
        int blue = getFadedInt(blueU, blueP, percent) & 0xFF;
        int alpha = getFadedInt(alphaU, alphaP, percent) & 0xFF;

        return red << 16 | green << 8 | blue | alpha << 24;
    }

    public static int getFadedInt(int from, int to, double percent) {
        return (int) Math.round(from * percent + to * (1.0D - percent));
    }

    @Override
    public void render(MatrixStack matrix) {
        int key = keySize.get() * 2;
        int space = key * 3 + 4;
        int mouse = key * 3 / 2 + 1;
        forward.render(matrix, key + 2, 0, key, key, -1);
        int y = key + 2;

        left.render(matrix, 0, y, key, key, -1);
        back.render(matrix, key + 2, y, key, key, -1);
        right.render(matrix, key * 2 + 4, y, key, key, -1);
        y += key + 2;

        if (showMouseButtons.get()) {
            attack.render(matrix, 0, y, mouse, key, CpsHud.getLeftCps());
            use.render(matrix, mouse + 2, y, mouse, key, CpsHud.getRightCps());
            y += key + 2;
        }

        if (showSpace.get()) {
            jump.render(matrix, 0, y, space, key / 2, -1);
        }
    }

    @Override
    protected int getWidth() {
        return keySize.get() * 6 + 4;
    }

    @Override
    protected int getHeight() {
        int key = keySize.get() * 2;
        int height = key * 2 + 2;
        if (showMouseButtons.get()) height += 2 + key;
        if (showSpace.get()) height += 2 + key / 2;
        return height;
    }

    public double fadePercentage(long last) {
        return MathHelper.clamp((Util.getMeasuringTimeMs() - last) / (double) fadeTime.get(), 0.0D, 1.0D);
    }

    public class Key {
        private final KeyBinding key;
        private long lastUnpressed;
        private long lastPressed;

        public Key(KeyBinding key) {
            this.key = key;
        }

        public void render(MatrixStack matrix, int x, int y, int width, int height, int cps) {
            if (key.isPressed()) {
                lastPressed = Util.getMeasuringTimeMs();
            } else {
                lastUnpressed = Util.getMeasuringTimeMs();
            }
            double percent = key.isPressed() ? 1.0D - fadePercentage(lastUnpressed) : fadePercentage(lastPressed);

            fill(matrix, x, y, x + width, y + height, getFadedColor(backgroundColor.asColor(), pressedBackgroundColor.asColor(), percent));
            int tc = getFadedColor(textColor.asColor(), pressedTextColor.asColor(), percent);
            String s = showCps.get() && cps > 0 ? String.valueOf(cps) : getName();
            drawString(matrix, s, x + width / 2.0F, y + height / 2.0F - 4, tc, textShadow.get(), Alignment.CENTER);
        }

        public String getName() {
            if (key.matchesMouse(GLFW.GLFW_MOUSE_BUTTON_1)) return "LMB";
            if (key.matchesMouse(GLFW.GLFW_MOUSE_BUTTON_2)) return "RMB";
            if (key.matchesMouse(GLFW.GLFW_MOUSE_BUTTON_3)) return "MMB";
            if (key.matchesKey(GLFW.GLFW_KEY_SPACE, GLFW.glfwGetKeyScancode(GLFW.GLFW_KEY_SPACE))) return "\u00a7m         ";
            return key.getBoundKeyLocalizedText().getString().toUpperCase(Locale.ROOT);
        }

    }

}
