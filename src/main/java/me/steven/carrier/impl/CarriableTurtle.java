package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.util.Identifier;

public class CarriableTurtle extends EntityCarriable<TurtleEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "turtle");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableTurtle());
    private static TurtleEntity dummyTurtle;
    private static TurtleEntityRenderer turtleRenderer;

    private CarriableTurtle() {
        super(TYPE, EntityType.TURTLE);
    }

    @Override
    public TurtleEntity getEntity() {
        if (dummyTurtle == null)
            dummyTurtle = new TurtleEntity(EntityType.TURTLE, MinecraftClient.getInstance().world);
        return dummyTurtle;
    }

    @Override
    public EntityRenderer<TurtleEntity> getEntityRenderer() {
        if (turtleRenderer == null)
            turtleRenderer = new TurtleEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return turtleRenderer;
    }
}