package me.steven.carrier.api;

import me.steven.carrier.mixin.AccessorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class EntityCarriable<T extends Entity> implements Carriable<EntityType<T>> {

    private final Identifier type;
    private final EntityType<T> entityType;

    public EntityCarriable(Identifier type, EntityType<T> entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    public Identifier getType() {
        return type;
    }

    @Environment(EnvType.CLIENT)
    public abstract T getEntity();

    @Environment(EnvType.CLIENT)
    public abstract EntityRenderer<T> getEntityRenderer();

    @Override
    public @NotNull ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity) {
        if (world.isClient) return ActionResult.PASS;
        CompoundTag tag = new CompoundTag();
        entity.toTag(tag);
        ((AccessorEntity) entity).carrier_writeCustomDataToTag(tag);
        Holding holding = new Holding(type, tag);
        holder.setHolding(holding);
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
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
        return ActionResult.SUCCESS;
    }

    protected void updateEntity(Holding holding) {
        if (!holding.getTag().getUuid("UUID").equals(getEntity().getUuid())) {
            getEntity().fromTag(holding.getTag());
        }
        if (getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) getEntity();
            entity.bodyYaw = 0;
            entity.prevBodyYaw =0;
        }
        getEntity().yaw = 0;
        getEntity().prevYaw = 0;
        getEntity().pitch = 0;
        getEntity().prevPitch = 0;
        getEntity().setHeadYaw(0);
    }
}
