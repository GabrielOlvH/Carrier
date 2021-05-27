package me.steven.carrier.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity {
    @Accessor
    void setWorld(World world);
    @Invoker("writeIdentifyingData")
    NbtCompound carrier_writeIdentifyingData(NbtCompound tag);
}
