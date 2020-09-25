package me.steven.carrier.impl;

import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.ChestType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CarriableChest extends CarriableGeneric {

    public CarriableChest(Identifier type, Block parent) {
        super(type, parent);
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Holding holding = holder.getHolding();
        if (holding == null) return ActionResult.PASS;
        BlockPos pos = ctx.getBlockPos();
        BlockState state = holding.getBlockState() == null ? Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, ctx.getPlayerLook().getOpposite()) : holding.getBlockState();
        if (state.getProperties().contains(ChestBlock.CHEST_TYPE) && !world.testBlockState(pos.offset(ChestBlock.getFacing(state)), (neighbor) -> neighbor.isOf(getParent())))
            state = state.with(ChestBlock.CHEST_TYPE, ChestType.SINGLE);
        world.setBlockState(pos, state);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.fromTag(state, holding.getBlockEntityTag());
            blockEntity.setPos(pos);
        }
        holder.setHolding(null);
        return ActionResult.SUCCESS;
    }
}
