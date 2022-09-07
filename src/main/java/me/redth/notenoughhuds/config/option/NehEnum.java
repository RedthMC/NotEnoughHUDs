package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.gui.widget.EnumWidget;
import me.redth.notenoughhuds.gui.widget.OptionWidget;

public class NehEnum extends NehOption<NehEnum.EnumType> {

    public NehEnum(String id, EnumType defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public EnumType e2t(JsonElement element) {
        return defaultValue.enumOf(element.getAsString());
    }

    @Override
    public JsonElement t2e(EnumType element) {
        return new JsonPrimitive(element.name());
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

        int ordinal();

        String name();

        default EnumType[] enums() {
            return getClass().getEnumConstants();
        }

        default EnumType enumOf(String name) {
            for (EnumType e : enums()) {
                if (e.name().equals(name))
                    return e;
            }
            return null;
        }

        default String getTranslationKey() {
            return "setting.notenoughhuds." + getId() + "." + this;
        }

        default EnumType getNext() {
            return enums()[(ordinal() + 1) % enums().length];
        }
    }
}
