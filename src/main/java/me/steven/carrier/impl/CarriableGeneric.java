package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarriableGeneric implements Carriable<Block> {

    protected final Identifier type;
    protected final Block parent;

    public CarriableGeneric(Identifier type, Block parent) {
        this.type = type;
        this.parent = parent;
    }

    @Override
    public @NotNull Block getParent() {
        return parent;
    }

    @Override
    public @NotNull ActionResult tryPickup(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity blockEntity = world.getBlockEntity(blockPos);
        BlockState blockState = world.getBlockState(blockPos);
        CarryingData carrying = new CarryingData(type, blockState, blockEntity);
        world.removeBlockEntity(blockPos);
        world.removeBlock(blockPos, false);
        carrier.setCarryingData(carrying);
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        CarryingData carrying = carrier.getCarryingData();
        if (carrying == null) return ActionResult.PASS;
        BlockPos pos = ctx.getBlockPos();
        BlockState state = carrying.getBlockState() == null ? parent.getDefaultState() : carrying.getBlockState();
        world.setBlockState(pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            NbtCompound tag = carrying.getBlockEntityTag();
            ((AccessorBlockEntity) blockEntity).carrier_writeIdentifyingData(tag);
            blockEntity.readNbt(tag);
        }
        carrier.setCarryingData(null);
        world.updateNeighbors(pos, state.getBlock());
        return ActionResult.SUCCESS;
    }

    @Override
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        BlockState blockState = parent.getDefaultState();
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion(180));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-yaw));
        matrices.translate(-0.5, 0.8, -1.3);
        BlockRenderManager blockRenderManager = MinecraftClient.getInstance().getBlockRenderManager();
        try {
            blockRenderManager.renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        } catch (Exception e) {
            //yes this is ignored
        }
        matrices.pop();
    }
}
