package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.IntegerWidget;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import net.minecraft.util.MathHelper;

import java.util.function.Function;

public class NehInteger extends NehOption<Integer> {
    public final int min;
    public final int max;
    private final Function<Integer, String> formatter;

    public NehInteger(String id, int defaultValue, int min, int max) {
        this(id, defaultValue, min, max, String::valueOf);
    }

    public NehInteger(String id, int defaultValue, int min, int max, Function<Integer, String> formatter) {
        super(id, defaultValue);
        this.min = min;
        this.max = max;
        this.formatter = formatter;
    }

    public String formatted() {
        return formatter.apply(value);
    }

    public void set(String value) {
        try {
            set(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void set(Integer value) {
        this.value = MathHelper.clamp_int(value, min, max);
    }


    @Override
    public Integer read(JsonElement element) {
        return element.getAsInt();
    }

    @Override
    public JsonElement write(Integer element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new IntegerWidget(x, y, this);
    }
}
