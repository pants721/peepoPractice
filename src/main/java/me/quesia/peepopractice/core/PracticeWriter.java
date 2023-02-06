package me.quesia.peepopractice.core;

import com.google.gson.*;
import me.quesia.peepopractice.PeepoPractice;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PracticeWriter {
    public static final PracticeWriter PREFERENCES_WRITER = new PracticeWriter("preferences.json");
    public static final PracticeWriter INVENTORY_WRITER = new PracticeWriter("inventory.json");
    public static final PracticeWriter PB_WRITER = new PracticeWriter("personal_bests.json");
    public static final PracticeWriter STANDARD_SETTINGS_WRITER = new PracticeWriter("standard_settings.json");
    private final File file;
    private JsonObject local;

    public PracticeWriter(String fileName) {
        this.file = this.create(fileName);
        this.local = this.get();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File create(String fileName) {
        try {
            File folder = FabricLoader.getInstance().getConfigDir().resolve(PeepoPractice.MOD_NAME).toFile();
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File file = folder.toPath().resolve(fileName).toFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write() {
        this.create(this.file.getName());
        try {
            FileWriter writer = new FileWriter(this.file);

            writer.write(new GsonBuilder().setPrettyPrinting().create().toJson(this.local));
            writer.flush();
            writer.close();
        } catch (IOException ignored) {}
        this.local = null;
        this.local = this.get();
    }

    public JsonObject get() {
        if (this.local != null) { return this.local; }

        this.create(this.file.getName());

        try {
            FileReader reader = new FileReader(this.file);
            JsonParser parser = new JsonParser();

            Object obj = parser.parse(reader);

            return obj == null || obj.equals(JsonNull.INSTANCE) ? new JsonObject() : (JsonObject) obj;
        } catch (IOException ignored) {}

        return null;
    }

    public void put(String element, long value) {
        if (this.local.has(element)) {
            this.local.remove(element);
        }
        this.local.addProperty(element, value);
    }

    public void put(String element, JsonObject obj) {
        if (this.local.has(element)) {
            this.local.remove(element);
        }
        this.local.add(element, obj);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void put(String element, JsonArray array) {
        if (this.local.has(element)) {
            this.local.remove(element);
        }
        this.local.add(element, array);
    }
}
