package me.steven.carrier.impl;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import me.steven.carrier.mixin.AccessorBarrelBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarriableBarrel implements Carriable<BarrelBlock> {

    private static final BlockState RENDER_STATE = Blocks.BARREL.getDefaultState().with(BarrelBlock.FACING, Direction.SOUTH);

    @Override
    public @NotNull BarrelBlock getParent() {
        return (BarrelBlock) Blocks.BARREL;
    }

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos pos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BarrelBlockEntity)) return ActionResult.PASS;
        AccessorBarrelBlockEntity barrel = (AccessorBarrelBlockEntity) blockEntity;
        Holding holding = new Holding(new Identifier(Carrier.MOD_ID, "barrel"), Inventories.toTag(new CompoundTag(), barrel.getInventory()));
        holder.setHolding(holding);
        barrel.getInventory().clear();
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Holding holding = holder.getHolding();
        if (holding == null) return ActionResult.PASS;
        DefaultedList<ItemStack> invList = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.fromTag(holding.getTag(), invList);
        BlockPos pos = ctx.getBlockPos();
        world.setBlockState(pos, Blocks.BARREL.getDefaultState().with(BarrelBlock.FACING, ctx.getPlayerLook()));
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof BarrelBlockEntity)) return ActionResult.PASS;
        AccessorBarrelBlockEntity barrel = (AccessorBarrelBlockEntity) blockEntity;
        Inventories.fromTag(holding.getTag(), barrel.getInventory());
        holder.setHolding(null);
        return ActionResult.SUCCESS;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw));
        matrices.translate(-0.5, 0.8, 0.2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(RENDER_STATE, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
