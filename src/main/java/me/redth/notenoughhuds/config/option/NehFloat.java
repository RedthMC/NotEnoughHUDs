package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.FloatWidget;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import net.minecraft.util.MathHelper;

public class NehFloat extends NehOption<Float> {
    public final float min;
    public final float max;

    public NehFloat(String id, float defaultValue, float min, float max) {
        super(id, defaultValue);
        this.min = min;
        this.max = max;
    }


    public static float floorTo2(float f) {
        return (int) (f * 100.0F) / 100.0F;
    }

    @Override
    public void set(Float value) {
        this.value = MathHelper.clamp_float(floorTo2(value), min, max);
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
