package io.github.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.redth.notenoughhuds.gui.widget.ColorWidget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.StringUtils;

public class NehColor extends NehOption<String> {
    public NehColor(String id, String defaultValue) {
        this(id, defaultValue, false);
    }

    public NehColor(String id, String defaultValue, boolean hidden) {
        super(id, defaultValue, hidden);
    }

    public static String filterValidChars(String text) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (isValidChar(c)) {
                sb.append(Character.toUpperCase(c));
            }
        }
        return sb.toString();
    }

    public static boolean isValidChar(char c) {
        return (c >= 48 && c <= 57) || (c >= 65 && c <= 70) || (c >= 97 && c <= 102); // 0-9 A-F a-f
    }

    @Override
    public void set(String value) {
        value = filterValidChars(value);
        if (value.length() > 8) value = value.substring(0, 8);
        else if (value.length() < 8) value = StringUtils.rightPad(value, 8, '0');
        this.value = value;
    }

    @Override
    public String e2t(JsonElement element) {
        return element.getAsString();
    }

    @Override
    public JsonElement t2e(String element) {
        return new JsonPrimitive(element);
    }

    @Override
    public ColorWidget getOptionWidget(int x, int y) {
        return new ColorWidget(x, y, this);
    }

    public int asColor() {
        return asColor(value);
    }

    public static int asColor(String hex) {
        try {
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFFFF;
        }
    }

    public static int getChroma(float offset) {
        return MathHelper.hsvToRgb((Util.getMeasuringTimeMs() + offset) % 1000.0F / 1000.0F, 1.0F, 1.0F);
    }
}
