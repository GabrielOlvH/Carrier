package me.steven.carrier;

import me.steven.carrier.api.CarryingData;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.math.BlockPos;

public class DeathCarryingData {
    private final CarryingData data;
    private final BlockPos deathPos;
    private int attempts;
    private BlockPos placedPos;

    public DeathCarryingData(CarryingData data, BlockPos deathPos) {
        this.data = data;
        this.deathPos = deathPos;
        this.attempts = 0;
        this.placedPos = BlockPos.ORIGIN;
    }

    public DeathCarryingData(CarryingData data, BlockPos deathPos, int attempts) {
        this(data, deathPos);
        this.attempts = attempts;
    }

    public CarryingData getData() {
        return data;
    }

    public BlockPos getDeathPos() {
        return deathPos;
    }

    public int getAttempts() {
        return attempts;
    }

    public void setAttempts(int attempts) {
        this.attempts = attempts;
    }

    public BlockPos getPlacedPos() {
        return placedPos;
    }

    public void setPlacedPos(BlockPos placedPos) {
        this.placedPos = placedPos;
    }

    public NbtCompound writeNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("data", data.writeNbt());
        nbt.put("pos", NbtHelper.fromBlockPos(deathPos));
        nbt.putInt("attempts", attempts);
        return nbt;
    }

    public static DeathCarryingData fromNbt(NbtCompound nbt) {
        CarryingData data = CarryingData.fromNbt(nbt.getCompound("data"));
        BlockPos pos = NbtHelper.toBlockPos(nbt.getCompound("pos"));
        int attempts = nbt.getInt("attempts");
        return new DeathCarryingData(data, pos, attempts);
    }
}
