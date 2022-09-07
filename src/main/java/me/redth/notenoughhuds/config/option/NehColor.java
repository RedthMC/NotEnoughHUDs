package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.ColorWidget;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

import java.awt.Color;

public class NehColor extends NehOption<String> {
    public boolean chroma;

    public NehColor(String id, String defaultValue) {
        super(id, defaultValue);
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


    public void set(int value) {
        String s = Long.toString(value & 0xFFFFFFFFL, 16);
        set(StringUtils.leftPad(s, 8, '0'));
    }

    @Override
    public String read(JsonElement element) {
        String s = element.getAsString();
        if (s.startsWith("*")) {
            chroma = true;
            s = s.substring(1);
        }
        return s;
    }

    @Override
    public JsonElement write(String element) {
        if (chroma) element = "*" + element;
        return new JsonPrimitive(element);
    }

    @Override
    public void reset() {
        super.reset();
        chroma = false;
    }

    @Override
    public ColorWidget getOptionWidget(int x, int y) {
        return new ColorWidget(x, y, this);
    }

    public int asInt() {
        try {
            if (chroma) return getChroma(0, 0);
            return (int) Long.parseLong(value, 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFFFF;
        }
    }

    public int getChroma(int x, int y) {
        return ((int) Long.parseLong(value, 16) & 0xFF000000) | (Color.HSBtoRGB((Minecraft.getSystemTime() + x + y) / 2000.0F, 1.0F, 1.0F) & 0xFFFFFF);
    }

    public static int getRainbow() {
        return Color.HSBtoRGB((Minecraft.getSystemTime()) / 2000.0F, 1.0F, 1.0F);
    }

}
