package io.github.redth.notenoughhuds.config.option;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.redth.notenoughhuds.gui.widget.OptionWidget;

public abstract class NehOption<T> {
    protected final String id;
    protected final T defaultValue;
    protected T value;
    protected boolean hidden;

    public NehOption(String id, T defaultValue, boolean hidden) {
        this.id = id;
        this.value = defaultValue;
        this.defaultValue = defaultValue;
        this.hidden = hidden;
    }

    public String getId() {
        return id;
    }

    public String getTranslationKey() {
        return "setting.notenoughhuds." + id;
    }

    public boolean isHidden() {
        return hidden;
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
