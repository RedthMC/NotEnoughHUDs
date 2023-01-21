package me.redth.notenoughhuds.utils;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.hud.BaseHud;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class HudManager {
    private final Set<BaseHud> huds;
    private final Set<BaseHud> enabled;

    public HudManager() {
        this.huds = new TreeSet<>(Comparator.comparing(hud -> hud.id));
        this.enabled = new TreeSet<>(Comparator.comparing(hud -> hud.id));
    }

    public void register(BaseHud hud) {
        huds.add(hud);
    }

    public Set<BaseHud> getEnabledHuds() {
        return enabled;
    }

    public Set<BaseHud> getHuds() {
        return huds;
    }

    public void loadOptions(JsonObject json) {
        boolean enabledCheck = json.has("enabled");
        JsonArray array = json.getAsJsonArray("enabled");

        for (BaseHud hud : huds) {
            if (enabledCheck && Iterables.contains(array, new JsonPrimitive(hud.id))) {
                hud.setEnabled(true);
            }

            if (!json.has(hud.id)) continue;
            JsonObject hudJson = json.getAsJsonObject(hud.id);
            for (NehOption<?> option : hud.options) {
                if (!hudJson.has(option.getId())) continue;
                option.set(hudJson.get(option.getId()));
            }
        }
    }

    public JsonObject saveOptions() {
        JsonObject json = new JsonObject();
        json.add("enabled", enabled.stream().collect(JsonArray::new, (j, h) -> j.add(new JsonPrimitive(h.id)), JsonArray::addAll));

        for (BaseHud hud : huds) {
            JsonObject hudJson = new JsonObject();
            for (NehOption<?> option : hud.options) {
                option.addToJson(hudJson);
            }
            json.add(hud.id, hudJson);
        }
        return json;
    }

}
