package me.steven.carrier.impl;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import me.steven.carrier.mixin.AccessorBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SpawnerBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.MobSpawnerBlockEntityRenderer;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.RabbitEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Lazy;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CarriableSpawner implements Carriable<SpawnerBlock> {

    @Environment(EnvType.CLIENT)
    private static MobSpawnerBlockEntity dummySpawner = null;
    @Environment(EnvType.CLIENT)
    private static MobSpawnerBlockEntityRenderer dummyRenderer = null;

    @Environment(EnvType.CLIENT)
    public MobSpawnerBlockEntity getEntity() {
        if (dummySpawner == null)
            dummySpawner = new MobSpawnerBlockEntity();
        return dummySpawner;
    }

    @Environment(EnvType.CLIENT)
    public MobSpawnerBlockEntityRenderer getEntityRenderer() {
        if (dummyRenderer == null)
            dummyRenderer = new MobSpawnerBlockEntityRenderer(BlockEntityRenderDispatcher.INSTANCE);
        return dummyRenderer;
    }


    @Override
    public @NotNull SpawnerBlock getParent() {
        return (SpawnerBlock) Blocks.SPAWNER;
    }

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos pos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MobSpawnerBlockEntity)) return ActionResult.PASS;
        MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) blockEntity;
        Holding holding = new Holding(new Identifier(Carrier.MOD_ID, "spawner"), spawner.getLogic().toTag(new CompoundTag()));
        holder.setHolding(holding);
        world.setBlockState(pos, Blocks.AIR.getDefaultState());
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Holding holding = holder.getHolding();
        if (holding == null) return ActionResult.PASS;
        BlockPos pos = ctx.getBlockPos();
        world.setBlockState(pos, Blocks.SPAWNER.getDefaultState());
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof MobSpawnerBlockEntity)) return ActionResult.PASS;
        MobSpawnerBlockEntity spawner = (MobSpawnerBlockEntity) blockEntity;
        spawner.getLogic().fromTag(holding.getTag());
        holder.setHolding(null);
        return ActionResult.SUCCESS;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(@NotNull PlayerEntity player, @NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        getEntity().getLogic().fromTag(holder.getHolding().getTag());
        BlockState blockState = Blocks.SPAWNER.getDefaultState();
        ((AccessorBlockEntity) getEntity()).setWorld(player.world);
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw));
        matrices.translate(-0.5, 0.8, 0.2);
        MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(blockState, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        if (MinecraftClient.isFancyGraphicsOrBetter())
            getEntityRenderer().render(getEntity(), tickDelta, matrices, vcp, light, OverlayTexture.DEFAULT_UV);
        matrices.pop();
    }
}
