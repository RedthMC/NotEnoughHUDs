package io.github.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.redth.notenoughhuds.gui.widget.FloatWidget;
import io.github.redth.notenoughhuds.gui.widget.OptionWidget;
import net.minecraft.util.math.MathHelper;

public class NehFloat extends NehOption<Float> {
    public final float min;
    public final float max;

    public NehFloat(String id, float defaultValue, float min, float max) {
        this(id, defaultValue, false, min, max);
    }

    public NehFloat(String id, float defaultValue, boolean hidden, float min, float max) {
        super(id, defaultValue, hidden);
        this.min = min;
        this.max = max;
    }

    public static float floorTo2(float f) {
        return (int) (f * 100.0F) / 100.0F;
    }

    @Override
    public void set(Float value) {
        this.value = MathHelper.clamp(floorTo2(value), min, max);
    }

    @Override
    public Float e2t(JsonElement element) {
        return element.getAsFloat();
    }

    @Override
    public JsonElement t2e(Float element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new FloatWidget(x, y, this);
    }
}
