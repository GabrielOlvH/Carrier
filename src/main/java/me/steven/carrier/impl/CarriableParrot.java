package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.Identifier;

public class CarriableParrot extends EntityCarriable<ParrotEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "parrot");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableParrot());
    private static ParrotEntity dummyParrot;
    private static ParrotEntityRenderer parrotRenderer;

    private CarriableParrot() {
        super(TYPE, EntityType.PARROT);
    }

    @Override
    public ParrotEntity getEntity() {
        if (dummyParrot == null)
            dummyParrot = new ParrotEntity(EntityType.PARROT, MinecraftClient.getInstance().world);
        return dummyParrot;
    }

    @Override
    public EntityRenderer<ParrotEntity> getEntityRenderer() {
        if (parrotRenderer == null)
            parrotRenderer = new ParrotEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return parrotRenderer;
    }
}