package me.steven.carrier.impl;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import me.steven.carrier.mixin.AccessorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EntityCarriable<T extends Entity> implements Carriable<EntityType<T>> {

    private final Identifier type;
    private final EntityType<T> entityType;

    public EntityCarriable(Identifier type, EntityType<T> entityType) {
        this.type = type;
        this.entityType = entityType;
    }

    public Identifier getType() {
        return type;
    }

    @Override
    public @NotNull EntityType<T> getParent() {
        return entityType;
    }

    @Override
    public @NotNull ActionResult tryPickup(@NotNull PlayerEntity player, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity) {
        if (world.isClient || entity == null) return ActionResult.PASS;
        NbtCompound tag = new NbtCompound();
        entity.writeNbt(tag);
        ((AccessorEntity) entity).carrier_writeCustomDataToNbt(tag);
        CarryingData carrying = new CarryingData(type, tag);
        entity.remove(Entity.RemovalReason.DISCARDED);
        return ActionResult.SUCCESS;
    }

    @Override
    public @NotNull ActionResult tryPlace(@NotNull CarryingData data, @NotNull World world, @NotNull CarriablePlacementContext ctx) {
        if (world.isClient) return ActionResult.PASS;
        Entity entity = entityType.create(world);
        if (entity == null) return ActionResult.PASS;
        ((AccessorEntity) entity).carrier_readCustomDataFromNbt(data.getTag());
        entity.readNbt(data.getTag());
        BlockPos blockPos = ctx.getBlockPos();
        entity.setPos(blockPos.getX() + 0.5F, blockPos.getY(), blockPos.getZ() + 0.5F);
        entity.refreshPositionAfterTeleport(entity.getPos());
        world.spawnEntity(entity);
        return ActionResult.SUCCESS;
    }

    @Override
    public void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        matrices.push();
        CarryingData data = carrier.getCarryingData();
        T entity = entityType.create(player.getWorld());
        if (data != null && entity != null) {
            setupRenderEntity(entity, data, player, carrier, matrices, vcp, tickDelta, light);
            EntityRenderDispatcher dispatcher = MinecraftClient.getInstance().getEntityRenderDispatcher();
            dispatcher.setRenderShadows(false);
            dispatcher.render(entity, 0.0, 0.0, 0.0, entity.getYaw(), tickDelta, matrices, vcp, light);
        }
        matrices.pop();
    }

    protected void setupRenderEntity(T entity, CarryingData carrying, @NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light) {
        if (!carrying.getTag().getUuid("UUID").equals(entity.getUuid())) {
            entity.readNbt(carrying.getTag());
        }
        if (entity instanceof LivingEntity living) {
            living.bodyYaw = 0;
            living.prevBodyYaw =0;
        }
        entity.setYaw(0);
        entity.prevYaw = 0;
        entity.setPitch(0);
        entity.prevPitch = 0;
        entity.setHeadYaw(0);

        double width = entity.getBoundingBox().getXLength();
        double height = entity.getBoundingBox().getYLength();
        double depth = entity.getBoundingBox().getZLength();

        if (width > 0.6) {
            width *= 1f/(width/0.6f);
            height = entity.getBoundingBox().getYLength()*(width/entity.getBoundingBox().getXLength());
            depth = entity.getBoundingBox().getZLength()*(width/entity.getBoundingBox().getXLength());
        }
        if (height > 1.5) {
            width *= 1f/(height/1.5f);
            height = entity.getBoundingBox().getYLength()*(width/entity.getBoundingBox().getXLength());
            depth = entity.getBoundingBox().getZLength()*(width/entity.getBoundingBox().getXLength());
        }
        if (depth > 0.6) {
            depth = (depth - 0.2) / 2.0;
        }

        //matrices.translate(0.0, 1.2+(((1.3*height)/2.0)/2.0), 0.0);
        matrices.scale(0.9f, 0.9f, 0.9f);
        matrices.scale((float)(width/entity.getBoundingBox().getXLength()), (float)(width/entity.getBoundingBox().getXLength()), (float)(width/entity.getBoundingBox().getXLength()));

        float yaw = MathHelper.lerpAngleDegrees(tickDelta, player.prevBodyYaw, player.bodyYaw);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-yaw + 90));
        matrices.translate(-1.4+((0.6*width)/0.6)/0.6, 1.6-height, 0.0);
        matrices.translate(depth - 0.2, 0.0, 0.0);
    }
}
