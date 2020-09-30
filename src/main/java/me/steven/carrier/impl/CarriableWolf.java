package me.steven.carrier.impl;

import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.WolfEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class CarriableWolf extends EntityCarriable<WolfEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "wolf");
    @Environment(EnvType.CLIENT)
    private static WolfEntity dummyWolf;
    @Environment(EnvType.CLIENT)
    private static WolfEntityRenderer wolfRenderer;

    public CarriableWolf() {
        super(TYPE, EntityType.WOLF);
    }

    @NotNull
    @Override
    public EntityType<WolfEntity> getParent() {
        return EntityType.WOLF;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public WolfEntity getEntity() {
        if (dummyWolf == null)
            dummyWolf = new WolfEntity(EntityType.WOLF, MinecraftClient.getInstance().world);
        return dummyWolf;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityRenderer<WolfEntity> getEntityRenderer() {
        if (wolfRenderer == null)
            wolfRenderer = new WolfEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return wolfRenderer;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        //updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}