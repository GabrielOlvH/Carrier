package me.steven.carrier.impl.blocks;

import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.minecraft.block.AbstractChestBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public class CarriableChest extends BaseCarriableBlock<AbstractChestBlock<?>> {

    public CarriableChest(Identifier type, AbstractChestBlock<?> parent) {
        super(type, parent);
    }

    @Override
    public BlockState getBlockStateToPlace(@NotNull CarryingData data, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        return parent.getPlacementState(new ItemPlacementContext(world, null, Hand.MAIN_HAND, ItemStack.EMPTY, new BlockHitResult(new Vec3d(pos.getX(), pos.getY(), pos.getZ()), ctx.getSide(), ctx.getBlockPos(), false)){
            @Override
            public Direction getPlayerFacing() {
                return ctx.getPlayerLook();
            }

            @Override
            public boolean shouldCancelInteraction() {
                return ctx.isSneaking();
            }
        });
    }
}
