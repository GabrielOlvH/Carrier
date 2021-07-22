package me.steven.carrier.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Carriable<T> {

    @NotNull
    T getParent();
    @NotNull
    ActionResult tryPickup(@NotNull PlayerEntity player, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity);
    @NotNull
    ActionResult tryPlace(@NotNull CarryingData data, @NotNull World world, @NotNull CarriablePlacementContext ctx);

    @Environment(EnvType.CLIENT)
    void render(@NotNull PlayerEntity player, @NotNull CarrierComponent carrier, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light);
}