package me.steven.carrier.impl;

import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class CarriableSpawner extends CarriableGeneric {

    @Environment(EnvType.CLIENT)
    private static MobSpawnerBlockEntity dummySpawner = null;
    @Environment(EnvType.CLIENT)
    private static MobSpawnerBlockEntityRenderer dummyRenderer = null;

    public CarriableSpawner(Identifier type) {
        super(type, Blocks.SPAWNER);
    }

    @Environment(EnvType.CLIENT)
    public MobSpawnerBlockEntity getEntity() {
        if (dummySpawner == null)
            dummySpawner = new MobSpawnerBlockEntity();
        return dummySpawner;
    }

    @Environment(EnvType.CLIENT)
    public MobSpawnerBlockEntityRenderer getEntityRenderer() {
        if (dummyRenderer == null)
            dummyRenderer = new MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE);
        return dummyRenderer;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        BlockState blockState = Blocks.SPAWNER.getDefaultState();
        getEntity().fromTag(blockState, carrier.getHolding().getBlockEntityTag());
        ((AccessorBlockEntity) getEntity()).setWorld(player.world);
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrices.translate(-0.5, 0.8, 0.2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        if (MinecraftClient.isFancyGraphicsOrBetter())
            getEntityRenderer().render(getEntity(), tickDelta, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
