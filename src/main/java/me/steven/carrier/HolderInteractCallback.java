package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HolderInteractCallback implements UseBlockCallback, UseEntityCallback {

    public static final HolderInteractCallback INSTANCE = new HolderInteractCallback();

    private HolderInteractCallback() {}

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || hand == Hand.OFF_HAND) return ActionResult.PASS;
        BlockPos pos = hitResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        Holder holder = Carrier.HOLDER.get(player);
            Holding holding = holder.getHolding();
            if (holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(block)) {
                Carriable<?> carriable = CarriableRegistry.INSTANCE.get(block);
                ActionResult actionResult = carriable.tryPickup(holder, world, pos, null);
                if (actionResult.isAccepted())
                    return actionResult;
            }
            if (holding == null) return ActionResult.PASS;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (carriable != null) {
                ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos.offset(hitResult.getSide()), hitResult.getSide(), player.getHorizontalFacing()));
                if (actionResult.isAccepted()) return actionResult;
            }

        return ActionResult.PASS;
    }

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, EntityHitResult hitResult) {
        if (world.isClient || hand == Hand.OFF_HAND) return ActionResult.PASS;
        BlockPos pos = entity.getBlockPos();
        Holder holder = Carrier.HOLDER.get(player);
            Holding holding = holder.getHolding();
            if (holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(entity.getType())) {
                Carriable<?> carriable = CarriableRegistry.INSTANCE.get(entity.getType());
                ActionResult actionResult = carriable.tryPickup(holder, world, pos, entity);
                if (actionResult.isAccepted())
                    return actionResult;
            }
            if (holding == null) return ActionResult.PASS;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (carriable != null) {
                ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos, player.getHorizontalFacing(), player.getHorizontalFacing()));
                if (actionResult.isAccepted()) return actionResult;
            }
        return ActionResult.PASS;
    }
}
