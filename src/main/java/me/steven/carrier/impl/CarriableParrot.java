package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ParrotEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriableParrot extends EntityCarriable<ParrotEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "parrot");
    private static ParrotEntity dummyParrot;
    private static ParrotEntityRenderer parrotRenderer;

    public CarriableParrot() {
        super(TYPE, EntityType.PARROT);
    }

    @NotNull
    @Override
    public EntityType<ParrotEntity> getParent() {
        return EntityType.PARROT;
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


    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.9f, 0.9f, 0.9f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.4, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}