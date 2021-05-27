package me.steven.carrier.api;

import me.steven.carrier.mixin.AccessorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
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
    public @NotNull ActionResult tryPickup(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity) {
        if (world.isClient || entity == null) return ActionResult.PASS;
        NbtCompound tag = new NbtCompound();
        entity.writeNbt(tag);
        ((AccessorEntity) entity).carrier_writeCustomDataToNbt(tag);
        CarryingData carrying = new CarryingData(type, tag);
        carrier.setCarryingData(carrying);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull CarrierComponent carrier, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        CarryingData carrying = carrier.getCarryingData();
        Entity entity = entityType.create(world);
        if (entity == null || carrying == null) return ActionResult.PASS;
        ((AccessorEntity) entity).carrier_readCustomDataFromNbt(carrying.getTag());
        entity.readNbt(carrying.getTag());
        BlockPos blockPos = ctx.getBlockPos();
        entity.setPos(blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F);
        entity.refreshPositionAfterTeleport(entity.getPos());
        world.spawnEntity(entity);
        carrier.setCarryingData(null);
        return ActionResult.SUCCESS;
    }

    protected void updateEntity(CarryingData carrying) {
        if (!carrying.getTag().getUuid("UUID").equals(getEntity().getUuid())) {
            getEntity().readNbt(carrying.getTag());
        }
        if (getEntity() instanceof LivingEntity) {
            LivingEntity entity = (LivingEntity) getEntity();
            entity.bodyYaw = 0;
            entity.prevBodyYaw =0;
        }
        getEntity().setYaw(0);
        getEntity().prevYaw = 0;
        getEntity().setPitch(0);
        getEntity().prevPitch = 0;
        getEntity().setHeadYaw(0);
    }
}
