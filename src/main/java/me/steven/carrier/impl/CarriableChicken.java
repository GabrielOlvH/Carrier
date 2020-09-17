package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.util.Identifier;

public class CarriableChicken  extends EntityCarriable<ChickenEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "chicken");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableChicken());
    private static ChickenEntity dummyChicken;
    private static ChickenEntityRenderer chickenRenderer;

    private CarriableChicken() {
        super(TYPE, EntityType.CHICKEN);
    }

    @Override
    public ChickenEntity getEntity() {
        if (dummyChicken == null)
            dummyChicken = new ChickenEntity(EntityType.CHICKEN, MinecraftClient.getInstance().world);
        return dummyChicken;
    }

    @Override
    public EntityRenderer<ChickenEntity> getEntityRenderer() {
        if (chickenRenderer == null)
            chickenRenderer = new ChickenEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return chickenRenderer;
    }
}