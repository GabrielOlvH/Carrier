package me.steven.carrier.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CarriablePlacementContext {
    private final Carriable<?> carriable;
    private final BlockPos blockPos;
    private final Direction side;
    private final Direction playerLook;
    private final boolean isSneaking;

    public CarriablePlacementContext(Carriable<?> carriable, BlockPos blockPos, Direction side, Direction playerLook, boolean isSneaking) {
        this.carriable = carriable;
        this.blockPos = blockPos;
        this.side = side;
        this.playerLook = playerLook;
        this.isSneaking = isSneaking;
    }

    public Carriable<?> getCarriable() {
        return carriable;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getSide() {
        return side;
    }

    public Direction getPlayerLook() {
        return playerLook;
    }

    public boolean isSneaking() {
        return isSneaking;
    }
}
