package io.github.reclqtch.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.reclqtch.notenoughhuds.gui.widget.BooleanWidget;
import io.github.reclqtch.notenoughhuds.gui.widget.OptionWidget;

public class NehBoolean extends NehOption<Boolean> {

    public NehBoolean(String id, boolean defaultValue) {
        super(id, defaultValue);
    }

    public boolean toggleValue() {
        set(!get());
        return get();
    }

    @Override
    public Boolean e2t(JsonElement element) {
        return element.getAsBoolean();
    }

    @Override
    public JsonElement t2e(Boolean element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new BooleanWidget(x, y, this);
    }
}
