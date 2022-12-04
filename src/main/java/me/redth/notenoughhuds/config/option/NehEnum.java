package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.EnumWidget;
import me.redth.notenoughhuds.gui.widget.OptionWidget;

import java.util.List;

public class NehEnum extends NehOption<NehEnum.EnumType> {

    public NehEnum(String id, EnumType defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public EnumType read(JsonElement element) {
        return defaultValue.of(element.getAsString());
    }

    @Override
    public JsonElement write(EnumType element) {
        return new JsonPrimitive(element.toString());
    }

    public EnumType next() {
        return value = value.getNext();
    }

    @Override
    public OptionWidget getOptionWidget(int x, int y) {
        return new EnumWidget(x, y, this);
    }

    public interface EnumType {
        String getId();

        List<EnumType> constants();

        default EnumType of(String name) {
            for (EnumType e : constants()) {
                if (e.toString().equals(name))
                    return e;
            }
            return null;
        }

        default String getTranslationKey() {
            return "setting.notenoughhuds." + getId() + "." + this;
        }

        default EnumType getNext() {
            List<EnumType> e = constants();
            return e.get((e.indexOf(this) + 1) % e.size());
        }
    }
}
