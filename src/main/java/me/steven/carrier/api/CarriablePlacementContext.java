package me.steven.carrier.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class CarriablePlacementContext {
    private final Holder holder;
    private final Carriable carriable;
    private final BlockPos blockPos;
    private final Direction side;

    public CarriablePlacementContext(Holder holder, Carriable carriable, BlockPos blockPos, Direction side) {
        this.holder = holder;
        this.carriable = carriable;
        this.blockPos = blockPos;
        this.side = side;
    }

    public Holder getHolder() {
        return holder;
    }

    public Carriable getCarriable() {
        return carriable;
    }

    public BlockPos getBlockPos() {
        return blockPos;
    }

    public Direction getSide() {
        return side;
    }
}
