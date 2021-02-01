package me.steven.carrier.mixin;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockEntity.class)
public interface AccessorBlockEntity {
    @Accessor
    void setWorld(World world);
    @Invoker("writeIdentifyingData")
    CompoundTag carrier_writeIdentifyingData(CompoundTag tag);
}
