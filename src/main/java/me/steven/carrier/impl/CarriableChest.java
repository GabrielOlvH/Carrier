package me.steven.carrier.impl;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import me.steven.carrier.mixin.AccessorChestBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarriableChest implements Carriable<ChestBlock> {

    @Override
    public @NotNull ChestBlock getParent() {
        return (ChestBlock) Blocks.CHEST;
    }

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos pos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ChestBlockEntity)) return ActionResult.PASS;
        AccessorChestBlockEntity chest = (AccessorChestBlockEntity) blockEntity;
        Holding holding = new Holding(new Identifier(Carrier.MOD_ID, "chest"), Inventories.toTag(new CompoundTag(), chest.getInventory()));
        holder.setHolding(holding);
        sync(holder);
        chest.getInventory().clear();
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Holding holding = holder.getHolding();
        if (holding == null) return ActionResult.PASS;
        PlayerEntity player = (PlayerEntity) holder;
        BlockPos pos = ctx.getBlockPos();
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, player.getHorizontalFacing().getOpposite()));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof ChestBlockEntity)) return ActionResult.PASS;
        AccessorChestBlockEntity chest = (AccessorChestBlockEntity) blockEntity;
        Inventories.fromTag(holding.getTag(), chest.getInventory());
        holder.setHolding(null);
        sync(holder);
        return ActionResult.SUCCESS;
    }

    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        if (holder instanceof PlayerEntity) {
            BlockState blockState = Blocks.CHEST.getDefaultState();
            PlayerEntity player = (PlayerEntity) holder;
            matrices.push();
            matrices.scale(0.6f, 0.6f, 0.6f);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw));
            matrices.translate(-0.5, 0.8, 0.2);
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
            matrices.pop();
        }
    }
}
