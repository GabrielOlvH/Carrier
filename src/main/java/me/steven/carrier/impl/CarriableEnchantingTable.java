package me.steven.carrier.impl;

import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class CarriableEnchantingTable extends CarriableGeneric {

    @Environment(EnvType.CLIENT)
    private static EnchantingTableBlockEntity dummyEnchantingTable = null;
    @Environment(EnvType.CLIENT)
    private static EnchantingTableBlockEntityRenderer dummyRenderer = null;

    public CarriableEnchantingTable(Identifier type) {
        super(type, Blocks.ENCHANTING_TABLE);
    }

    @Environment(EnvType.CLIENT)
    public EnchantingTableBlockEntity getEntity() {
        if (dummyEnchantingTable == null)
            dummyEnchantingTable = new EnchantingTableBlockEntity();
        return dummyEnchantingTable;
    }

    @Environment(EnvType.CLIENT)
    public EnchantingTableBlockEntityRenderer getEntityRenderer() {
        if (dummyRenderer == null)
            dummyRenderer = new EnchantingTableBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE);
        return dummyRenderer;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        BlockState blockState = getParent().getDefaultState();
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