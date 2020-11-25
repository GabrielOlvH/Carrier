package me.steven.carrier.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CarriablePlacementContext {
    private final CarrierComponent carrier;
    private final Carriable<?> carriable;
    private final BlockPos blockPos;
    private final Direction side;
    private final Direction playerLook;

    public CarriablePlacementContext(CarrierComponent carrier, Carriable<?> carriable, BlockPos blockPos, Direction side, Direction playerLook) {
        this.carrier = carrier;
        this.carriable = carriable;
        this.blockPos = blockPos;
        this.side = side;
        this.playerLook = playerLook;
    }

    public CarrierComponent getHolder() {
        return carrier;
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
}
