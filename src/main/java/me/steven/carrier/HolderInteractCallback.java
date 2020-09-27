package me.steven.carrier;

import io.netty.buffer.Unpooled;
import me.steven.carrier.api.*;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
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
        if (world.isClient && holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(block) && player.getStackInHand(hand).isEmpty() && Carrier.canCarry(Registry.BLOCK.getId(block))) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBlockPos(hitResult.getBlockPos());
            ClientSidePacketRegistry.INSTANCE.sendToServer(Carrier.C2S_CARRY_BLOCK_PACKET, buf);
            return ActionResult.CONSUME;
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
        if (world.isClient && holding == null && player.isSneaking() && CarriableRegistry.INSTANCE.contains(entity.getType()) && player.getStackInHand(hand).isEmpty() && Carrier.canCarry(Registry.ENTITY_TYPE.getId(entity.getType()))) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(entity.getEntityId());
            ClientSidePacketRegistry.INSTANCE.sendToServer(Carrier.C2S_CARRY_ENTITY_PACKET, buf);
            return ActionResult.CONSUME;
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
