package me.steven.carrier.api;

import me.steven.carrier.Carrier;
import me.steven.carrier.CarrierClient;
import me.steven.carrier.mixin.AccessorEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EntityCarriable<T extends Entity> implements Carriable {

    private final Identifier type;
    private final EntityType<T> entityType;

    public EntityCarriable(Identifier type, EntityType<T> entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    public Identifier getType() {
        return type;
    }

    public abstract T getEntity();

    public abstract EntityRenderer<T> getEntityRenderer();

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        CompoundTag tag = new CompoundTag();
        entity.toTag(tag);
        ((AccessorEntity) entity).carrier_writeCustomDataToTag(tag);
        Holding holding = new Holding(type, tag);
        holder.setHolding(holding);
        Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
        carriable.sync(holder);
        entity.remove();
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Holding holding = holder.getHolding();
        Entity entity = entityType.create(world);
        ((AccessorEntity) entity).carrier_readCustomDataFromTag(holding.getTag());
        entity.fromTag(holding.getTag());
        BlockPos blockPos = ctx.getBlockPos();
        entity.setPos(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        entity.refreshPositionAfterTeleport(entity.getPos());
        world.spawnEntity(entity);
        holder.setHolding(null);
        Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
        carriable.sync(holder);
        return ActionResult.SUCCESS;
    }

    @Override
    public void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        PlayerEntity player = (PlayerEntity) holder;
        matrices.push();
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(-player.bodyYaw + 90));
        matrices.translate(-0.6, 0.8, -0.1);
        getEntityRenderer().render(getEntity(), 0, tickDelta, matrices, vcp, light);
        matrices.pop();
    }
}
