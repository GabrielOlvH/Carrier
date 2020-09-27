package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class HolderInteractCallback implements UseBlockCallback, UseEntityCallback {

    public static final HolderInteractCallback INSTANCE = new HolderInteractCallback();

    private HolderInteractCallback() {
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;
        BlockPos pos = hitResult.getBlockPos();
        if (!world.canPlayerModifyAt(player, pos)) return ActionResult.PASS;
        Block block = world.getBlockState(pos).getBlock();
        Holder holder = Carrier.HOLDER.get(player);
        Holding holding = holder.getHolding();
        if (holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(block) && player.getStackInHand(hand).isEmpty()) {
            if (world.isClient && !Carrier.canCarry(Registry.BLOCK.getId(block))) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(block);
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(Registry.BLOCK.getId(block)))
                carriable.tryPickup(holder, world, pos, null);
        }

        if (holding != null) {
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (!world.isClient && carriable != null) {
                ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos.offset(hitResult.getSide()), hitResult.getSide(), player.getHorizontalFacing()));
                if (actionResult.isAccepted()) return actionResult;
            }
        }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (hand == Hand.OFF_HAND || !world.canPlayerModifyAt(player, entity.getBlockPos())) return ActionResult.PASS;
        BlockPos pos = entity.getBlockPos();
        Holder holder = Carrier.HOLDER.get(player);
        Holding holding = holder.getHolding();
        if (holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(entity.getType()) && player.getStackInHand(hand).isEmpty() ) {
            if (world.isClient && !Carrier.canCarry(Registry.ENTITY_TYPE.getId(entity.getType()))) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(entity.getType());
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(Registry.ENTITY_TYPE.getId(entity.getType())))
                carriable.tryPickup(holder, world, pos, entity);
        }
        if (holding == null) return ActionResult.PASS;
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
        if (!world.isClient && carriable != null) {
            ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos, player.getHorizontalFacing(), player.getHorizontalFacing()));
            if (actionResult.isAccepted()) return actionResult;
        }
        return ActionResult.PASS;
    }
}
