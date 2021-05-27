package me.steven.carrier.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CarryingData {
    private final Identifier type;
    private final BlockState blockState;
    private final NbtCompound tag;

    public CarryingData(Identifier type, NbtCompound tag) {
        this.type = type;
        this.tag = tag;
        Optional<Pair<BlockState, NbtElement>> blockState = BlockState.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("blockState")).get().left();
        this.blockState = blockState.map(Pair::getFirst).orElse(null);
    }

    public CarryingData(Identifier type, BlockState state, @Nullable BlockEntity entity) {
        this.type = type;
        this.tag = new NbtCompound();
        this.blockState = state;
        DataResult<NbtElement> result = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state);
        result.result().ifPresent((t) -> this.tag.put("blockState", t));
        if (entity != null)
            this.tag.put("blockEntity", entity.writeNbt(new NbtCompound()));
    }

    public NbtCompound getTag() {
        return tag;
    }

    @Nullable
    public NbtCompound getBlockEntityTag() {
        if (tag.contains("blockEntity"))
            return tag.getCompound("blockEntity");
        else return null;
    }

    public Identifier getType() {
        return type;
    }

    public BlockState getBlockState() {
        return blockState;
    }
}
