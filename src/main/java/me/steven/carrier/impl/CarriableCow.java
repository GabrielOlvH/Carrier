package me.steven.carrier.impl;

import me.steven.carrier.api.EntityCarriable;
import me.steven.carrier.api.Holder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.CowEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class CarriableCow extends EntityCarriable<CowEntity> {

    public static final Identifier TYPE = new Identifier("carrier", "cow");
    @Environment(EnvType.CLIENT)
    private static CowEntity dummyCow;
    @Environment(EnvType.CLIENT)
    private static CowEntityRenderer cowRenderer;

    public CarriableCow() {
        super(TYPE, EntityType.COW);
    }

    @NotNull
    @Override
    public EntityType<CowEntity> getParent() {
        return EntityType.COW;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public CowEntity getEntity() {
        if (dummyCow == null)
            dummyCow = new CowEntity(EntityType.COW, MinecraftClient.getInstance().world);
        return dummyCow;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public EntityRenderer<CowEntity> getEntityRenderer() {
        if (cowRenderer == null)
            cowRenderer = new CowEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return cowRenderer;
    }


    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        updateEntity(holder.getHolding());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}
