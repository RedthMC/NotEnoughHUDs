package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.OptionWidget;
import me.redth.notenoughhuds.gui.widget.StringWidget;

public class NehString extends NehOption<String> {

    public NehString(String id, String defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public String read(JsonElement element) {
        return element.getAsString();
    }

    @Override
    public JsonElement write(String element) {
        return new JsonPrimitive(element);
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new StringWidget(x, y, this);
    }
}
