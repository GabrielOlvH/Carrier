package me.steven.carrier.api;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class Holding {
    private final Identifier type;
    private final BlockState blockState;
    private final CompoundTag tag;

    public Holding(Identifier type, CompoundTag tag) {
        this.type = type;
        this.tag = tag;
        Optional<Pair<BlockState, Tag>> blockState = BlockState.CODEC.decode(NbtOps.INSTANCE, tag.getCompound("blockState")).get().left();
        this.blockState = blockState.map(Pair::getFirst).orElse(null);
    }

    public Holding(Identifier type, BlockState state, @Nullable BlockEntity entity) {
        this.type = type;
        this.tag = new CompoundTag();
        this.blockState = state;
        DataResult<Tag> result = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, state);
        result.result().ifPresent((t) -> this.tag.put("blockState", t));
        if (entity != null)
            this.tag.put("blockEntity", entity.toTag(new CompoundTag()));
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Nullable
    public CompoundTag getBlockEntityTag() {
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
