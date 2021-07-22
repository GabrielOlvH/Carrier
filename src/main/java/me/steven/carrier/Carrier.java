package me.steven.carrier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import me.steven.carrier.api.*;
import me.steven.carrier.api.event.RegisterCarriableCallback;
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
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.regex.Pattern;

public class Carrier implements ModInitializer, EntityComponentInitializer {

    public static final ComponentKey<CarrierComponent> HOLDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("carrier", "holder"), CarrierComponent.class);

    public static final String MOD_ID = "carrier";

    public static Config CONFIG = new Config();

    public static final Item ITEM_GLOVE = new GloveItem(new FabricItemSettings().group(ItemGroup.TOOLS).maxCount(1));

    public static final Identifier SET_CAN_CARRY_PACKET = new Identifier(MOD_ID, "can_carry_packet");

    @Override
    public void onInitialize() {
        CONFIG = Config.getConfig();

        ServerTickEvents.END_WORLD_TICK.register(new ServerWorldTickCallback());


        Registry.ENTITY_TYPE.forEach((entityType) -> {
            Identifier type = new Identifier("carrier", Registry.ENTITY_TYPE.getId(entityType).getPath());
            registerGenericCarriable(entityType, type);
        });

        RegistryEntryAddedCallback.event(Registry.ENTITY_TYPE).register((rawId, id, entityType) -> {
            Identifier type = new Identifier("carrier", id.getPath());
            registerGenericCarriable(entityType, type);
        });

        Registry.BLOCK.forEach((block) -> {
            Identifier type = new Identifier("carrier", Registry.BLOCK.getId(block).getPath());
            registerGenericCarriable(block, type);
        });

        RegistryEntryAddedCallback.event(Registry.BLOCK).register((rawId, id, block) -> {
            Identifier type = new Identifier("carrier", id.getPath());
            registerGenericCarriable(block, type);
        });

        Registry.register(Registry.ITEM, new Identifier(MOD_ID, "glove"), ITEM_GLOVE);

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

        ServerSidePacketRegistry.INSTANCE.register(SET_CAN_CARRY_PACKET, (ctx, buf) -> {
            boolean canCarry = buf.readBoolean();
            ctx.getTaskQueue().execute(() ->
                    ((CarrierPlayerExtension) ctx.getPlayer()).setCanCarry(canCarry));
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

    private static void registerGenericCarriable(Block block, Identifier type) {
        if (block instanceof BlockEntityProvider) {
            Carriable<?> carriable = RegisterCarriableCallback.BLOCK_EVENT.invoker().register(block);
            if (carriable != null)
                CarriableRegistry.INSTANCE.register(type, carriable);
            else if (block instanceof AbstractChestBlock<?> chest)
                CarriableRegistry.INSTANCE.register(type, new CarriableChest(type, chest));
            else if (block instanceof AbstractBannerBlock banner)
                CarriableRegistry.INSTANCE.register(type, new CarriableBanner(type, banner));
            else
                CarriableRegistry.INSTANCE.register(type, new BaseCarriableBlock<>(type, block));
        }
    }

    private static void registerGenericCarriable(EntityType<?> entityType, Identifier type) {
        Carriable<?> carriable = RegisterCarriableCallback.ENTITY_EVENT.invoker().register(entityType);
        if (carriable != null)
            CarriableRegistry.INSTANCE.register(type, carriable);
        else
            CarriableRegistry.INSTANCE.register(type, new EntityCarriable<>(type, entityType));
    }
}
