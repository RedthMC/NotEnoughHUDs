package me.redth.notenoughhuds.hud;

import me.redth.notenoughhuds.config.option.NehBoolean;
import me.redth.notenoughhuds.config.option.NehColor;
import me.redth.notenoughhuds.config.option.NehInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

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
    public final Key forward;
    public final Key left;
    public final Key back;
    public final Key right;
    public final Key jump;
    public final Key attack;
    public final Key use;


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
        forward = new Key(mc.gameSettings.keyBindForward);
        left = new Key(mc.gameSettings.keyBindLeft);
        back = new Key(mc.gameSettings.keyBindBack);
        right = new Key(mc.gameSettings.keyBindRight);
        jump = new Key(mc.gameSettings.keyBindJump);
        attack = new Key(mc.gameSettings.keyBindAttack);
        use = new Key(mc.gameSettings.keyBindUseItem);
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
    public void render() {
        int key = keySize.get() * 2;
        int space = key * 3 + 4;
        int mouse = key * 3 / 2 + 1;


        forward.render(key + 2, 0, key, key, -1);
        int y = key + 2;

        left.render(0, y, key, key, -1);
        back.render(key + 2, y, key, key, -1);
        right.render(key * 2 + 4, y, key, key, -1);
        y += key + 2;

        if (showMouseButtons.get()) {
            attack.render(0, y, mouse, key, CpsHud.getLeftCps());
            use.render(mouse + 2, y, mouse, key, CpsHud.getRightCps());
            y += key + 2;
        }

        if (showSpace.get()) {
            jump.render(0, y, space, key / 2, -1);
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
        return MathHelper.clamp_double((Minecraft.getSystemTime() - last) / (double) fadeTime.get(), 0.0D, 1.0D);
    }

    public final class Key {
        private final KeyBinding key;
        private long lastUnpressed;
        private long lastPressed;

        public Key(KeyBinding key) {
            this.key = key;
        }

        public void render(int x, int y, int width, int height, int cps) {
            if (key.isKeyDown()) {
                lastPressed = Minecraft.getSystemTime();
            } else {
                lastUnpressed = Minecraft.getSystemTime();
            }

            double percent = key.isKeyDown() ? 1.0D - fadePercentage(lastUnpressed) : fadePercentage(lastPressed);

            drawRect(x, y, x + width, y + height, getFadedColor(backgroundColor.asInt(), pressedBackgroundColor.asInt(), percent));
            int tc = getFadedColor(textColor.asInt(), pressedTextColor.asInt(), percent);
            String s = showCps.get() && cps > 0 ? String.valueOf(cps) : getName();
            drawString(s, x + width / 2.0F, y + height / 2.0F - 4.0F, tc, textShadow.get(), Alignment.CENTER);
        }

        public String getName() {
            switch (key.getKeyCode()) {
                case -100:
                    return "LMB";
                case -99:
                    return "RMB";
                case -98:
                    return "MMB";
                case Keyboard.KEY_SPACE:
                    return "\u00a7m         ";
                default:
                    return Keyboard.getKeyName(key.getKeyCode());
            }
        }
//
//        public void onInput() {
//            if (pressed) {
//                lastPressed = Minecraft.getSystemTime();
//            } else {
//                lastUnpressed = Minecraft.getSystemTime();
//            }
//            pressed = key.isKeyDown();
//        }

    }

}
