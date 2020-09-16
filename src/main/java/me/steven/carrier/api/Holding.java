package me.steven.carrier.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class Holding {
    private final Identifier type;
    private final CompoundTag tag;

    public Holding(Identifier type, CompoundTag tag) {
        this.type = type;
        this.tag = tag;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public Identifier getType() {
        return type;
    }
}
