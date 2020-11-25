package me.steven.carrier.impl;

import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CarriableChest extends CarriableGeneric {

    public CarriableChest(Identifier type, Block parent) {
        super(type, parent);
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        CarryingData carrying = carrier.getHolding();
        if (carrying == null) return ActionResult.PASS;
        BlockPos pos = ctx.getBlockPos();
        BlockState state = carrying.getBlockState() == null ? Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, ctx.getPlayerLook().getOpposite()) : carrying.getBlockState();
        if (state.getProperties().contains(ChestBlock.CHEST_TYPE) && !world.testBlockState(pos.offset(ChestBlock.getFacing(state)), (neighbor) -> neighbor.isOf(getParent())))
            state = state.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE);
        world.setBlockState(pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.fromTag(state, carrying.getBlockEntityTag());
            blockEntity.setPos(pos);
        }
        carrier.setHolding(null);
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
