package me.steven.carrier.api;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

public interface Carriable {
    @NotNull
    ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos blockPos);
    @NotNull
    ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx);

    @Environment(EnvType.CLIENT)
    void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light);
}