package io.github.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.redth.notenoughhuds.gui.widget.IntegerWidget;
import io.github.redth.notenoughhuds.gui.widget.OptionWidget;
import net.minecraft.util.math.MathHelper;

public class NehInteger extends NehOption<Integer> {
    public final int min;
    public final int max;

    public NehInteger(String id, int defaultValue, int min, int max) {
        this(id, defaultValue, false, min, max);
    }

    public NehInteger(String id, int defaultValue, boolean hidden, int min, int max) {
        super(id, defaultValue, hidden);
        this.min = min;
        this.max = max;
    }


    public void set(String value) {
        try {
            set(Integer.parseInt(value));
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void set(Integer value) {
        this.value = MathHelper.clamp(value, min, max);
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
