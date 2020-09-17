package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
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
import org.jetbrains.annotations.NotNull;

public class CarriableWolf extends EntityCarriable<WolfEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "wolf");
    public static final Carriable INSTANCE = CarriableRegistry.INSTANCE.register(TYPE, new CarriableWolf());
    private static WolfEntity dummyWolf;
    private static WolfEntityRenderer wolfRenderer;

    private CarriableWolf() {
        super(TYPE, EntityType.WOLF);
    }

    @Override
    public WolfEntity getEntity() {
        if (dummyWolf == null)
            dummyWolf = new WolfEntity(EntityType.WOLF, MinecraftClient.getInstance().world);
        return dummyWolf;
    }

    @Override
    public EntityRenderer<WolfEntity> getEntityRenderer() {
        if (wolfRenderer == null)
            wolfRenderer = new WolfEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return wolfRenderer;
    }


    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}