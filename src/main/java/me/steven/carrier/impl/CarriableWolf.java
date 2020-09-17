package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.Identifier;

public class CarriableWolf extends EntityCarriable<WolfEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "wolf");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableWolf());
    private static WolfEntity dummyWolf;
    private static WolfEntityRenderer wolfRenderer;

    private CarriableWolf() {
        super(TYPE, EntityType.WOLF);
    }

    @Override
    public WolfEntity getEntity() {
        if (dummyWolf == null)
            dummyWolf = new WolfEntity(EntityType.WOLF, MinecraftClient.getInstance().world);
        return dummyWolf;
    }

    @Override
    public EntityRenderer<WolfEntity> getEntityRenderer() {
        if (wolfRenderer == null)
            wolfRenderer = new WolfEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return wolfRenderer;
    }
}