package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.Identifier;

public class CarriableRabbit extends EntityCarriable<RabbitEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "rabbit");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableRabbit());
    private static RabbitEntity dummyRabbit;
    private static RabbitEntityRenderer rabbitRenderer;

    private CarriableRabbit() {
        super(TYPE, EntityType.RABBIT);
    }

    @Override
    public RabbitEntity getEntity() {
        if (dummyRabbit == null)
            dummyRabbit = new RabbitEntity(EntityType.RABBIT, MinecraftClient.getInstance().world);
        return dummyRabbit;
    }

    @Override
    public EntityRenderer<RabbitEntity> getEntityRenderer() {
        if (rabbitRenderer == null)
            rabbitRenderer = new RabbitEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return rabbitRenderer;
    }
}