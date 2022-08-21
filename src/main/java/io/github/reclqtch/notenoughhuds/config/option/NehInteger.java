package io.github.reclqtch.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.reclqtch.notenoughhuds.gui.widget.IntegerWidget;
import io.github.reclqtch.notenoughhuds.gui.widget.OptionWidget;
import net.minecraft.util.MathHelper;

public class NehInteger extends NehOption<Integer> {
    public final int min;
    public final int max;
    public final String unit;

    public NehInteger(String id, int defaultValue, int min, int max) {
        this(id, defaultValue, min, max, null);
    }

    public NehInteger(String id, int defaultValue, int min, int max, String unit) {
        super(id, defaultValue);
        this.min = min;
        this.max = max;
        this.unit = unit;
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
    public Integer e2t(JsonElement element) {
        return element.getAsInt();
    }

    @Override
    public JsonElement t2e(Integer element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new IntegerWidget(x, y, this);
    }
}
