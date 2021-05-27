package me.steven.carrier.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface AccessorEntity {
    @Invoker(value = "writeCustomDataToNbt")
    void carrier_writeCustomDataToNbt(NbtCompound tag);

    @Invoker(value = "readCustomDataFromNbt")
    void carrier_readCustomDataFromNbt(NbtCompound tag);
}
