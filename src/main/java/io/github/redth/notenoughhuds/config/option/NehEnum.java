package io.github.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.github.redth.notenoughhuds.gui.widget.EnumWidget;
import io.github.redth.notenoughhuds.gui.widget.OptionWidget;

public class NehEnum extends NehOption<NehEnum.EnumType> {

    public NehEnum(String id, EnumType defaultValue) {
        this(id, defaultValue, false);
    }

    public NehEnum(String id, EnumType defaultValue, boolean hidden) {
        super(id, defaultValue, hidden);
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

        @SuppressWarnings("unchecked")
        default <E extends Enum<E>> E enumOf(String name) {
            return Enum.valueOf((Class<E>) getClass(), name);
        }

        default String getTranslationKey() {
            return "setting.notenoughhuds." + getId() + "." + this;
        }

        default EnumType getNext() {
            return enums()[(ordinal() + 1) % enums().length];
        }
    }
}
