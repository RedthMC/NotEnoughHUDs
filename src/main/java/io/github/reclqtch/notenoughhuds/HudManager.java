package io.github.reclqtch.notenoughhuds;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.reclqtch.notenoughhuds.config.option.NehOption;
import io.github.reclqtch.notenoughhuds.hud.BaseHud;

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

        for (BaseHud mod : huds) {
            if (enabledCheck && Iterables.contains(array, new JsonPrimitive(mod.id))) {
                mod.setEnabled(true);
            }

            if (!json.has(mod.id)) continue;
            JsonObject hudJson = json.getAsJsonObject(mod.id);
            for (NehOption<?> option : mod.options) {
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
