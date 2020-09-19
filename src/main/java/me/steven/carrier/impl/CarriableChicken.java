package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.ChickenEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriableChicken  extends EntityCarriable<ChickenEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "chicken");
    private static ChickenEntity dummyChicken;
    private static ChickenEntityRenderer chickenRenderer;

    public CarriableChicken() {
        super(TYPE, EntityType.CHICKEN);
    }

    @NotNull
    @Override
    public EntityType<ChickenEntity> getParent() {
        return EntityType.CHICKEN;
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


    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.9f, 0.9f, 0.9f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.4, 0.6, 0.00);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}