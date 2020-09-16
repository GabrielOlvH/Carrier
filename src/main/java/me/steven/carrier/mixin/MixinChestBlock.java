package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.Utils;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
@Mixin(ChestBlock.class)
public class MixinChestBlock implements Carriable {

    @Shadow @Final public static DirectionProperty FACING;

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos pos) {
        if (world.isClient) return ActionResult.SUCCESS;
        ChestBlock chestBlock = (ChestBlock) (Object) this;
        BlockState blockState = world.getBlockState(pos);
        Inventory inv = ChestBlock.getInventory(chestBlock, blockState, world, pos, true);
        if (inv == null) return ActionResult.PASS;
        Holding holding = new Holding(new Identifier(Carrier.MOD_ID, "chest"), Utils.inventoryToTag(inv, new CompoundTag()));
        holder.setHolding(holding);
        inv.clear();
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        Holding holding = holder.getHolding();
        if (holding == null) return ActionResult.PASS;
        DefaultedList<ItemStack> invList = DefaultedList.ofSize(27, ItemStack.EMPTY);
        Inventories.fromTag(holding.getTag(), invList);
        PlayerEntity player = (PlayerEntity) holder;
        BlockPos pos = ctx.getBlockPos();
        world.setBlockState(pos, Blocks.CHEST.getDefaultState().with(FACING, player.getHorizontalFacing().getOpposite()));

        Inventory inv = ChestBlock.getInventory((ChestBlock)(Object)this, world.getBlockState(pos), world, pos, false);
        if (inv == null) return ActionResult.PASS;
        for (int i = 0; i < invList.size(); i++) {
            inv.setStack(i, invList.get(i));
        }
        holder.setHolding(null);
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
