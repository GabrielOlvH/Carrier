package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HolderInteractCallback implements UseBlockCallback {
    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (world.isClient || hand == Hand.OFF_HAND) return ActionResult.PASS;
        BlockPos pos = hitResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        if (player instanceof Holder) {
            Holder holder = (Holder) player;
            Holding holding = holder.getHolding();
            if (holding == null && player.isSneaking() && block instanceof Carriable) {
                Carriable carriable = (Carriable) block;
                ActionResult actionResult = carriable.tryPickup(holder, world, pos, null);
                if (actionResult.isAccepted())
                    return actionResult;
            }
            if (holding == null) return ActionResult.PASS;
            Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (carriable != null) {
                ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos.offset(hitResult.getSide()), hitResult.getSide()));
                if (actionResult.isAccepted()) return actionResult;
            }
        }
        return ActionResult.PASS;
    }
}
