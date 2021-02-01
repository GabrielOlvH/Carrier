package me.steven.carrier.impl;

import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.mixin.AccessorBannerBlockEntity;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.EnchantingTableBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.NotNull;

public class CarriableBanner extends CarriableGeneric {

    @Environment(EnvType.CLIENT)
    private static BannerBlockEntity dummyEnchantingTable = null;
    @Environment(EnvType.CLIENT)
    private static BannerBlockEntityRenderer dummyRenderer = null;

    public CarriableBanner(Identifier type, Block parent) {
        super(type, parent);
    }

    @Environment(EnvType.CLIENT)
    public BannerBlockEntity getEntity() {
        if (dummyEnchantingTable == null)
            dummyEnchantingTable = new BannerBlockEntity();
        return dummyEnchantingTable;
    }

    @Environment(EnvType.CLIENT)
    public BannerBlockEntityRenderer getEntityRenderer() {
        if (dummyRenderer == null)
            dummyRenderer = new BannerBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE);
        return dummyRenderer;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        BlockState blockState = getParent().getDefaultState();
        BannerBlockEntity banner = getEntity();
        banner.getColorForState(() -> blockState);
        ((AccessorBannerBlockEntity) banner).setPatternListTag(carrier.getCarryingData().getBlockEntityTag().getList("Patterns", 10));
        ((AccessorBannerBlockEntity) banner).setPatternListTagRead(true);
        ((AccessorBannerBlockEntity) banner).setBaseColor(((AbstractBannerBlock) blockState.getBlock()).getColor());
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrices.translate(-0.5, 0.4, -0.05);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        if (MinecraftClient.isFancyGraphicsOrBetter())
            getEntityRenderer().render(banner, tickDelta, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
