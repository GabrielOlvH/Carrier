package me.steven.carrier.mixin;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BannerBlockEntity.class)
public interface AccessorBannerBlockEntity {
    @Accessor
    void setBaseColor(DyeColor color);
}
