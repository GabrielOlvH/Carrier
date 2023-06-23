package me.steven.carrier;

import me.steven.carrier.api.*;
import me.steven.carrier.mixin.AccessorEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class HolderInteractCallback {

    public static final HolderInteractCallback INSTANCE = new HolderInteractCallback();

    private HolderInteractCallback() {
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockPos pos, Direction hitDirection, boolean canPickup) {
        if (hand == Hand.OFF_HAND) return ActionResult.PASS;
        if (!world.canPlayerModifyAt(player, pos)) return ActionResult.PASS;
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getCarryingData();
        if (canPickup && carrying == null && CarriableRegistry.INSTANCE.contains(block) && blockState.getHardness(world, pos) > -1) {
            Identifier id = Registries.BLOCK.getId(block);
            if (world.isClient && !Carrier.canCarry(id)) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(block);
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(id)) {
                BlockEntity blockEntity = world.getBlockEntity(pos);
                ActionResult actionResult = carriable.tryPickup(player, world, pos, null);
                if (actionResult.isAccepted()) {
                    carrier.setCarryingData(new CarryingData(CarriableRegistry.INSTANCE.getId(CarriableRegistry.INSTANCE.get(block)), blockState, blockEntity));
                    return actionResult;
                }
            }
        }

        if (carrying != null) {
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
            if (!world.isClient && carriable != null && world.getBlockState(pos.offset(hitDirection)).isReplaceable()) {
                ActionResult actionResult = carriable.tryPlace(carrying, world, new CarriablePlacementContext(carriable, pos.offset(hitDirection), hitDirection, player.getHorizontalFacing(), player.isSneaking()));
                if (actionResult.isAccepted()) {
                    carrier.setCarryingData(null);
                    return actionResult;
                }
            }
        }

        return ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity, boolean canPickup) {
        if (hand == Hand.OFF_HAND || !world.canPlayerModifyAt(player, entity.getBlockPos())) return ActionResult.PASS;
        BlockPos pos = entity.getBlockPos();
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getCarryingData();
        if (canPickup && carrying == null && CarriableRegistry.INSTANCE.contains(entity.getType())) {
            Identifier id = Registries.ENTITY_TYPE.getId(entity.getType());
            if (world.isClient && !Carrier.canCarry(id)) return ActionResult.CONSUME;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(entity.getType());
            if (world.canPlayerModifyAt(player, pos) && carriable != null && Carrier.canCarry(id)) {
                ActionResult actionResult = carriable.tryPickup(player, world, pos, entity);
                if (actionResult.isAccepted()) {
                    NbtCompound tag = new NbtCompound();
                    entity.writeNbt(tag);
                    ((AccessorEntity) entity).carrier_writeCustomDataToNbt(tag);
                    carrier.setCarryingData(new CarryingData(CarriableRegistry.INSTANCE.getId(CarriableRegistry.INSTANCE.get(entity.getType())), tag));
                    return actionResult;
                }
            }
        }
        if (carrying == null) return ActionResult.PASS;
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
        if (!world.isClient && carriable != null) {
            ActionResult actionResult = carriable.tryPlace(carrying, world, new CarriablePlacementContext(carriable, pos, player.getHorizontalFacing(), player.getHorizontalFacing(), player.isSneaking()));
            if (actionResult.isAccepted()) {
                carrier.setCarryingData(null);
                return actionResult;
            }
        }
        return ActionResult.PASS;
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return interact(player, world, hand, hitResult.getBlockPos(), hitResult.getSide(), !Carrier.CONFIG.doGlovesExist()
                && Carrier.isHoldingKey(player) && player.getStackInHand(hand).isEmpty());
    }

    public ActionResult interact(PlayerEntity player, World world, Hand hand, Entity entity) {
        return interact(player, world, hand, entity, !Carrier.CONFIG.doGlovesExist()
                && Carrier.isHoldingKey(player)
                && player.getStackInHand(hand).isEmpty());
    }

    public ActionResult interact(ItemUsageContext context) {
        return interact(context.getPlayer(), context.getWorld(), context.getHand(), context.getBlockPos(), context.getSide(), true);
    }
}
