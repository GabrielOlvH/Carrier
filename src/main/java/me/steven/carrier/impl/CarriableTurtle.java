package me.steven.carrier.impl;

import me.steven.carrier.ClientUtils;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.EntityCarriable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.TurtleEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.NotNull;

public class CarriableTurtle extends EntityCarriable<TurtleEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "turtle");
    @Environment(EnvType.CLIENT)
    private static TurtleEntity dummyTurtle;
    @Environment(EnvType.CLIENT)
    private static TurtleEntityRenderer turtleRenderer;

    public CarriableTurtle() {
        super(TYPE, EntityType.TURTLE);
    }

    @NotNull
    @Override
    public EntityType<TurtleEntity> getParent() {
        return EntityType.TURTLE;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public TurtleEntity getEntity() {
        if (dummyTurtle == null)
            dummyTurtle = new TurtleEntity(EntityType.TURTLE, MinecraftClient.getInstance().world);
        return dummyTurtle;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityRenderer<TurtleEntity> getEntityRenderer() {
        if (turtleRenderer == null)
            turtleRenderer = new TurtleEntityRenderer(ClientUtils.defaultEntityCtx());
        return turtleRenderer;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        updateEntity(carrier.getCarryingData());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw + 90));
        matrices.translate(-1.0, 1.2, 0.2);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}