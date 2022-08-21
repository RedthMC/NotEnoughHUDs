package io.github.reclqtch.notenoughhuds.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import io.github.reclqtch.notenoughhuds.NotEnoughHUDs;
import net.minecraftforge.fml.common.Loader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class NehConfig {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final NotEnoughHUDs neh = NotEnoughHUDs.getInstance();
    public final Path file;

    public NehConfig() {
        file = Loader.instance().getConfigDir().toPath().resolve("notenoughhuds.json");
    }

    public void load() {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            neh.hudManager.loadOptions(GSON.fromJson(reader, JsonObject.class));
        } catch (Throwable e) {
            save();
        }
    }

    public void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            GSON.toJson(neh.hudManager.saveOptions(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
