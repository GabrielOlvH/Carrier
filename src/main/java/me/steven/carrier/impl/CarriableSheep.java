package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.SheepEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.util.Identifier;

public class CarriableSheep extends EntityCarriable<SheepEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "sheep");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableSheep());
    private static SheepEntity dummySheep;
    private static SheepEntityRenderer sheepRenderer;

    private CarriableSheep() {
        super(TYPE, EntityType.SHEEP);
    }

    @Override
    public SheepEntity getEntity() {
        if (dummySheep == null)
            dummySheep = new SheepEntity(EntityType.SHEEP, MinecraftClient.getInstance().world);
        return dummySheep;
    }

    @Override
    public EntityRenderer<SheepEntity> getEntityRenderer() {
        if (sheepRenderer == null)
            sheepRenderer = new SheepEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return sheepRenderer;
    }
}
