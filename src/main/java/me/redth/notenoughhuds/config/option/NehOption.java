package me.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.redth.notenoughhuds.gui.widget.OptionWidget;

public abstract class NehOption<T> {
    protected final String id;
    protected final T defaultValue;
    protected boolean hidden;
    protected T value;

    public NehOption(String id, T defaultValue) {
        this.id = id;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public NehOption<T> hidden() {
        hidden = true;
        return this;
    }
    public boolean isHidden() {
        return hidden;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return "setting.notenoughhuds." + id;
    }

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }

    public void reset() {
        value = defaultValue;
    }

    public void set(JsonElement jsonElement) {
        set(e2t(jsonElement));
    }

    public void addToJson(JsonObject json) {
        json.add(id, t2e(value));
    }

    public abstract T e2t(JsonElement element);

    public abstract JsonElement t2e(T element);

    public abstract OptionWidget getOptionWidget(int x, int y);

}
