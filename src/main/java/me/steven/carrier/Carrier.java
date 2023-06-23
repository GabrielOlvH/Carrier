package me.steven.carrier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.steven.carrier.api.*;
import me.steven.carrier.impl.EntityCarriable;
import me.steven.carrier.impl.blocks.*;
import me.steven.carrier.items.GloveItem;
import net.devtech.arrp.api.RRPCallback;
import net.devtech.arrp.api.RuntimeResourcePack;
import net.devtech.arrp.json.recipe.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.regex.Pattern;

public class Carrier implements ModInitializer, EntityComponentInitializer {

    public static final ComponentKey<CarrierComponent> HOLDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("carrier", "holder"), CarrierComponent.class);

    public static final String MOD_ID = "carrier";

    public static Config CONFIG = new Config();

    public static final Item ITEM_GLOVE = new GloveItem(new FabricItemSettings().maxCount(1));

    public static final Identifier SET_CAN_CARRY_PACKET = new Identifier(MOD_ID, "can_carry_packet");

    @Override
    public void onInitialize() {
        CONFIG = Config.getConfig();

        ServerTickEvents.END_WORLD_TICK.register(new ServerWorldTickCallback());


        Registries.ENTITY_TYPE.forEach((entityType) -> {
            Identifier id = Registries.ENTITY_TYPE.getId(entityType);
            Identifier type = new Identifier("carrier", id.getNamespace() + "_" + id.getPath());
            register(entityType, type);
        });

        RegistryEntryAddedCallback.event(Registries.ENTITY_TYPE).register((rawId, id, entityType) -> {
            Identifier type = new Identifier("carrier", id.getNamespace() + "_" + id.getPath());
            register(entityType, type);
        });

        Registries.BLOCK.forEach((block) -> {
            Identifier id = Registries.BLOCK.getId(block);
            Identifier type = new Identifier("carrier", id.getNamespace() + "_" + id.getPath());
            register(block, type);
        });

        RegistryEntryAddedCallback.event(Registries.BLOCK).register((rawId, id, block) -> {
            Identifier type = new Identifier("carrier", id.getNamespace() + "_" + id.getPath());
            register(block, type);
        });

        Registry.register(Registries.ITEM, new Identifier(MOD_ID, "glove"), ITEM_GLOVE);

        if (CONFIG.doGlovesExist()) {
            RuntimeResourcePack resourcePack = RuntimeResourcePack.create(MOD_ID + ":gloves");
            resourcePack.addRecipe(new Identifier(MOD_ID, "gloves"),
                    JRecipe.shaped(
                            JPattern.pattern("L  ", "LL "),
                            JKeys.keys().key("L", JIngredient.ingredient().item("minecraft:leather")),
                            JResult.item(ITEM_GLOVE)
                    )
            );
            RRPCallback.EVENT.register(packs -> packs.add(resourcePack));
        }

        ServerPlayNetworking.registerGlobalReceiver(SET_CAN_CARRY_PACKET, (server, player, handler, buf, responseSender) -> {
            boolean canCarry = buf.readBoolean();
            server.execute(() ->
                    ((CarrierPlayerExtension) player).setCanCarry(canCarry));
        });

        CarrierCommands.register();
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HOLDER, CarrierComponent::new, RespawnCopyStrategy.ALWAYS_COPY);
    }

    public static boolean canCarry(Identifier id) {
        if (CONFIG.getType() == Config.ListType.WHITELIST)
            return CONFIG.getList().stream().anyMatch((s) -> Pattern.compile(s).matcher(id.toString()).find());
        else return CONFIG.getList().stream().noneMatch((s) -> Pattern.compile(s).matcher(id.toString()).find());
    }

    public static boolean isHoldingKey(PlayerEntity playerEntity) {
        return playerEntity instanceof CarrierPlayerExtension && ((CarrierPlayerExtension) playerEntity).canCarry();
    }

    private static void register(Block block, Identifier type) {
        if (!CarriableRegistry.INSTANCE.contains(block)) {
            if (block instanceof AbstractChestBlock<?> chest)
                CarriableRegistry.INSTANCE.register(type, new CarriableChest(type, chest));
            else if (block instanceof AbstractBannerBlock banner)
                CarriableRegistry.INSTANCE.register(type, new CarriableBanner(type, banner));
            else
                CarriableRegistry.INSTANCE.register(type, new BaseCarriableBlock<>(type, block));
        }
    }

    private static void register(EntityType<?> entityType, Identifier type) {
        if (!CarriableRegistry.INSTANCE.contains(entityType))
            CarriableRegistry.INSTANCE.register(type, new EntityCarriable<>(type, entityType));
    }
}
