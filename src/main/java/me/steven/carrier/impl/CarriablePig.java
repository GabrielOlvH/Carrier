package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.Identifier;

public class CarriablePig extends EntityCarriable<PigEntity>  {

    public static final Identifier TYPE = new Identifier("carrier", "pig");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriablePig());
    private static PigEntity dummyPig;
    private static PigEntityRenderer renderer;

    private CarriablePig() {
        super(TYPE, EntityType.PIG);
    }

    @Override
    public PigEntity getEntity() {
        if (dummyPig == null)
            dummyPig = new PigEntity(EntityType.PIG, MinecraftClient.getInstance().world);
        return dummyPig;
    }

    @Override
    public EntityRenderer<PigEntity> getEntityRenderer() {
        if (renderer == null)
            renderer = new PigEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return renderer;
    }

}
