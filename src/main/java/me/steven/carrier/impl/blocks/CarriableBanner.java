package me.steven.carrier.impl.blocks;

import me.steven.carrier.ClientUtils;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.mixin.AccessorBannerBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.jetbrains.annotations.NotNull;

public class CarriableBanner extends BaseCarriableBlock<AbstractBannerBlock> {

    public CarriableBanner(Identifier type,  AbstractBannerBlock parent) {
        super(type, parent);
    }

    @Override
    public void setupRender(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw));
        matrices.translate(-0.5, 0.4, -0.05);
    }

    @Override
    public void setupRenderBlockEntity(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light, @NotNull BlockEntity blockEntity) {
        if (blockEntity instanceof BannerBlockEntity banner) {
            BlockState blockState = getParent().getDefaultState();
            banner.getColorForState();
            ((AccessorBannerBlockEntity) banner).setPatternListTag(carrier.getCarryingData().getBlockEntityTag().getList("Patterns", 10));
            ((AccessorBannerBlockEntity) banner).setBaseColor(((AbstractBannerBlock) blockState.getBlock()).getColor());
        }
    }
}
