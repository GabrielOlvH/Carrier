package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriableRabbit extends EntityCarriable<RabbitEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "rabbit");
    @Environment(EnvType.CLIENT)
    private static RabbitEntity dummyRabbit;
    @Environment(EnvType.CLIENT)
    private static RabbitEntityRenderer rabbitRenderer;

    public CarriableRabbit() {
        super(TYPE, EntityType.RABBIT);
    }

    @NotNull
    @Override
    public EntityType<RabbitEntity> getParent() {
        return EntityType.RABBIT;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public RabbitEntity getEntity() {
        if (dummyRabbit == null)
            dummyRabbit = new RabbitEntity(EntityType.RABBIT, MinecraftClient.getInstance().world);
        return dummyRabbit;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityRenderer<RabbitEntity> getEntityRenderer() {
        if (rabbitRenderer == null)
            rabbitRenderer = new RabbitEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return rabbitRenderer;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        updateEntity(holder.getHolding());
        matrices.push();
        //matrices.scale(0.9f, 0.9f, 0.9f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.3, 0.8, 0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}