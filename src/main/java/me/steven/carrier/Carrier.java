package me.steven.carrier;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.impl.*;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class Carrier implements ModInitializer, EntityComponentInitializer {

    public static final ComponentKey<Holder> HOLDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("carrier", "holder"), Holder.class);

    public static final String MOD_ID = "carrier";

    public static Config CONFIG = new Config();
    
    public static final Identifier C2S_CARRY_BLOCK_PACKET = new Identifier(MOD_ID, "carry_block_packet");
    public static final Identifier C2S_CARRY_ENTITY_PACKET = new Identifier(MOD_ID, "carry_entity_packet");

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
        UseBlockCallback.EVENT.register(HolderInteractCallback.INSTANCE);
        UseEntityCallback.EVENT.register(HolderInteractCallback.INSTANCE);
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

        ServerSidePacketRegistry.INSTANCE.register(C2S_CARRY_BLOCK_PACKET, (ctx, buf) -> {
            BlockPos pos = buf.readBlockPos();
            Holder holder = HOLDER.get(ctx.getPlayer());
            ctx.getTaskQueue().execute(() -> {
                World world = ctx.getPlayer().world;
                Block block = world.getBlockState(pos).getBlock();
                Carriable<?> carriable = CarriableRegistry.INSTANCE.get(block);
                if (world.canPlayerModifyAt(ctx.getPlayer(), pos) && carriable != null && canCarry(Registry.BLOCK.getId(block)))
                    carriable.tryPickup(holder, world, pos, null);
            });
        });

        ServerSidePacketRegistry.INSTANCE.register(C2S_CARRY_ENTITY_PACKET, (ctx, buf) -> {
            int entityId = buf.readInt();
            ctx.getTaskQueue().execute(() -> {
                World world = ctx.getPlayer().world;
                Entity entity = world.getEntityById(entityId);
                if (entity != null) {
                    Holder holder = HOLDER.get(ctx.getPlayer());
                    BlockPos pos = entity.getBlockPos();
                    Carriable<?> carriable = CarriableRegistry.INSTANCE.get(entity.getType());
                    if (world.canPlayerModifyAt(ctx.getPlayer(), pos) && carriable != null && canCarry(Registry.ENTITY_TYPE.getId(entity.getType())))
                        carriable.tryPickup(holder, world, pos, entity);
                }
            });
        });
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HOLDER, Holder::new, RespawnCopyStrategy.ALWAYS_COPY);
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
