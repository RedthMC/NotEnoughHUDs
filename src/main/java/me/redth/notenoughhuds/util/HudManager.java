package me.redth.notenoughhuds.util;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.redth.notenoughhuds.config.option.NehOption;
import me.redth.notenoughhuds.hud.Hud;
import net.minecraft.client.renderer.GlStateManager;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class HudManager {
    private final Set<Hud> huds;
    private final Set<Hud> enabled;

    public HudManager() {
        this.huds = new TreeSet<>(Comparator.comparing(hud -> hud.id));
        this.enabled = new TreeSet<>(Comparator.comparing(hud -> hud.id));
    }

    public void register(Hud hud) {
        huds.add(hud);
    }

    public Set<Hud> getEnabledHuds() {
        return enabled;
    }

    public Set<Hud> getHuds() {
        return huds;
    }

    public void renderHuds() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        for (Hud hud : enabled) {
            hud.renderScaled();
        }
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public void loadOptions(JsonObject json) {
        boolean enabledCheck = json.has("enabled");
        JsonArray array = json.getAsJsonArray("enabled");

        for (Hud mod : huds) {
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

        for (Hud hud : huds) {
            JsonObject hudJson = new JsonObject();
            for (NehOption<?> option : hud.options) {
                option.addToJson(hudJson);
            }
            json.add(hud.id, hudJson);
        }
        return json;
    }

    public void resetAll() {
        for (Hud hud : huds) {
            hud.reset();
        }
        enabled.clear();
    }

}
