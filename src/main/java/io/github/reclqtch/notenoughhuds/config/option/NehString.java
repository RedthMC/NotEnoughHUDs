package io.github.reclqtch.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.reclqtch.notenoughhuds.gui.widget.OptionWidget;
import io.github.reclqtch.notenoughhuds.gui.widget.StringWidget;

public class NehString extends NehOption<String> {

    public NehString(String id, String defaultValue) {
        super(id, defaultValue);
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
    public OptionWidget getOptionWidget(int x, int y) {
        return new StringWidget(x, y, this);
    }
}
