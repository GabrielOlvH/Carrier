package me.steven.carrier.impl;

import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CarriableChest extends CarriableGeneric {

    public CarriableChest(Identifier type, Block parent) {
        super(type, parent);
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        CarryingData carrying = carrier.getCarryingData();
        if (carrying == null) return ActionResult.PASS;
        BlockPos pos = ctx.getBlockPos();

        BlockState state = parent.getPlacementState(new ItemPlacementContext(carrier.getOwner(), Hand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), ctx.getSide(), ctx.getBlockPos(), false)));
        world.setBlockState(pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.fromTag(state, carrying.getBlockEntityTag());
            blockEntity.setPos(pos);
        }
        carrier.setCarryingData(null);
        return ActionResult.SUCCESS;
    }

    @Override
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        BlockState blockState = parent.getDefaultState();
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrices.translate(-0.5, 0.8, 0.2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
