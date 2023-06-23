package me.steven.carrier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal"})
public class Config {
    private boolean enableGloves = false;
    private int slownessLevel = 2;
    private float hungerExhaustion = 0.05f;
    private boolean keepCarryingOnDeath = false;
    private ListType type = ListType.WHITELIST;
    private List<String> list = Arrays.asList(
            "minecraft:bat",
            "minecraft:cat",
            "minecraft:chicken",
            "minecraft:cod",
            "minecraft:cow",
            "minecraft:dolphin",
            "minecraft:donkey",
            "minecraft:fox",
            "minecraft:horse",
            "minecraft:mooshroom",
            "minecraft:mule",
            "minecraft:ocelot",
            "minecraft:panda",
            "minecraft:parrot",
            "minecraft:pig",
            "minecraft:pufferfish",
            "minecraft:rabbit",
            "minecraft:salmon",
            "minecraft:sheep",
            "minecraft:skeleton_horse",
            "minecraft:squid",
            "minecraft:strider",
            "minecraft:tropical_fish",
            "minecraft:turtle",
            "minecraft:villager",
            "minecraft:wandering_trader",
            "minecraft:wolf",
            "minecraft:barrel",
            "minecraft:beehive",
            "minecraft:bell",
            "minecraft:blast_furnace",
            "minecraft:brewing_stand",
            "minecraft:campfire",
            "minecraft:chest",
            "minecraft:comparator",
            "minecraft:daylight_detector",
            "minecraft:dispenser",
            "minecraft:dropper",
            "minecraft:enchanting_table",
            "minecraft:ender_chest",
            "minecraft:furnace",
            "minecraft:hopper",
            "minecraft:jukebox",
            "minecraft:lectern",
            "minecraft:note_block",
            "minecraft:shulker_box",
            "minecraft:smoker"
    );

    public Config() {}

    public List<String> getList() {
        return list;
    }

    public ListType getType() {
        return type;
    }

    public float getHungerExhaustion() {
        return hungerExhaustion;
    }

    public boolean shouldKeepCarryingOnDeath() {
        return keepCarryingOnDeath;
    }

    public boolean doGlovesExist() { return enableGloves; }

    public int getSlownessLevel() {
        return slownessLevel;
    }

    public static Config getConfig() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "carrier.json");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) throw new IOException("Failed to create file");
                FileUtils.write(file, gson.toJson(new Config()), StandardCharsets.UTF_8);
                return new Config();
            } catch (IOException e) {
                LogManager.getLogger("Carrier").error("Failed to create carrier config");
                throw new RuntimeException(e);
            }
        } else {
            try {
                String lines = String.join("\n", FileUtils.readLines(file, StandardCharsets.UTF_8));
                return gson.fromJson(lines, Config.class);
            } catch (IOException e) {
                LogManager.getLogger("Carrier").error("Failed to read config");
                throw new RuntimeException(e);
            }
        }
    }

    public enum ListType {
        WHITELIST, BLACKLIST
    }
}
