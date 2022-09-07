package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.BooleanWidget;
import me.redth.notenoughhuds.gui.widget.OptionWidget;

public class NehBoolean extends NehOption<Boolean> {

    public NehBoolean(String id, boolean defaultValue) {
        super(id, defaultValue);
    }

    public boolean toggleValue() {
        set(!get());
        return get();
    }

    @Override
    public Boolean read(JsonElement element) {
        return element.getAsBoolean();
    }

    @Override
    public JsonElement write(Boolean element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new BooleanWidget(x, y, this);
    }
}
