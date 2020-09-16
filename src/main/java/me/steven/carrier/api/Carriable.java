package me.steven.carrier.api;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface Carriable {
    ActionResult tryPickup(Holder holder, World world, BlockPos blockPos);
    ActionResult tryPlace(Holder holder, World world, CarriablePlacementContext ctx);

    void render(Holder holder, MatrixStack matrices, VertexConsumerProvider vcp);
}