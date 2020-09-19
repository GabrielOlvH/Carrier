package me.steven.carrier;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistryV3;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.impl.*;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.util.Identifier;

public class Carrier implements ModInitializer, EntityComponentInitializer {

    public static final ComponentKey<Holder> HOLDER = ComponentRegistryV3.INSTANCE.getOrCreate(new Identifier("carrier", "holder"), Holder.class);

    public static final String MOD_ID = "carrier";

    @Override
    public void onInitialize() {
        ServerTickEvents.END_WORLD_TICK.register(new ServerWorldTickCallback());
        UseBlockCallback.EVENT.register(HolderInteractCallback.INSTANCE);
        UseEntityCallback.EVENT.register(HolderInteractCallback.INSTANCE);
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "cow"), new CarriableCow());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "chicken"), new CarriableChicken());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "parrot"), new CarriableParrot());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "pig"), new CarriablePig());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "rabbit"), new CarriableRabbit());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "sheep"), new CarriableSheep());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "turtle"), new CarriableTurtle());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "wolf"), new CarriableWolf());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "chest"), new CarriableChest());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "barrel"), new CarriableBarrel());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier", "spawner"), new CarriableSpawner());
    }

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(HOLDER, Holder::new, RespawnCopyStrategy.ALWAYS_COPY);
    }
}
