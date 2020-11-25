package me.steven.carrier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.impl.*;
import me.steven.carrier.items.GloveItem;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Carrier implements ModInitializer, EntityComponentInitializer {

    public static final ComponentKey<CarrierComponent> HOLDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("carrier", "holder"), CarrierComponent.class);

    public static final String MOD_ID = "carrier";

    public static Config CONFIG = new Config();

    public static final Item ITEM_GLOVE = new GloveItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));

    @Override
    public void onInitialize() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "carrier.json");
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) throw new IOException("Failed to create file");
                FileUtils.write(file, gson.toJson(CONFIG), StandardCharsets.UTF_8);
            } catch (IOException e) {
                LogManager.getLogger("Carrier").error("Failed to create carrier config");
                throw new RuntimeException(e);
            }
        } else {
            try {
                String lines = String.join("\n", FileUtils.readLines(file, StandardCharsets.UTF_8));
                CONFIG = gson.fromJson(lines, Config.class);
            } catch (IOException e) {
                LogManager.getLogger("Carrier").error("Failed to read config");
                throw new RuntimeException(e);
            }
        }

        ServerTickEvents.END_WORLD_TICK.register(new ServerWorldTickCallback());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "cow"), new CarriableCow());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "chicken"), new CarriableChicken());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "parrot"), new CarriableParrot());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "pig"), new CarriablePig());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "rabbit"), new CarriableRabbit());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "sheep"), new CarriableSheep());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "turtle"), new CarriableTurtle());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "wolf"), new CarriableWolf());
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "spawner"), new CarriableSpawner(new Identifier(MOD_ID, "spawner")));
        CarriableRegistry.INSTANCE.register(new Identifier(MOD_ID, "enchanting_table"), new CarriableEnchantingTable(new Identifier(MOD_ID, "enchanting_table")));

        Registry.BLOCK.forEach((block) -> {
            Identifier type = new Identifier("carrier", Registry.BLOCK.getId(block).getPath());
            registerGenericCarriable(block, type);
        });
        RegistryEntryAddedCallback.event(Registry.BLOCK).register((rawId, id, block) -> {
            Identifier type = new Identifier("carrier", id.getPath());
            registerGenericCarriable(block, type);
        });
        if (CONFIG.doGlovesExist()) {
            Registry.register(Registry.ITEM, new Identifier(MOD_ID, "glove"), ITEM_GLOVE);
        }
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HOLDER, CarrierComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static boolean canCarry(Identifier id) {
        if (CONFIG.getType() == Config.ListType.WHITELIST) return CONFIG.getList().stream().anyMatch((s) -> Pattern.compile(s).matcher(id.toString()).find());
        else return CONFIG.getList().stream().noneMatch((s) -> Pattern.compile(s).matcher(id.toString()).find());
    }


    private static void registerGenericCarriable(Block block, Identifier type) {
        if (block instanceof BlockEntityProvider) {
            if (!CarriableRegistry.INSTANCE.contains(type)) {
                if (block instanceof AbstractChestBlock<?>)
                    CarriableRegistry.INSTANCE.register(type, new CarriableChest(type, block));
                else
                    CarriableRegistry.INSTANCE.register(type, new CarriableGeneric(type, block));
            }
        }
    }
}
