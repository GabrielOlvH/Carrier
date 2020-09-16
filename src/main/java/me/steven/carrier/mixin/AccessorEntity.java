package me.steven.carrier.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Invoker(value = "writeCustomDataToTag")
    void carrier_writeCustomDataToTag(CompoundTag tag);

    @Invoker(value = "readCustomDataFromTag")
    void carrier_readCustomDataFromTag(CompoundTag tag);
}
