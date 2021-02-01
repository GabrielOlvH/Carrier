package me.steven.carrier.mixin;

import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.nbt.ListTag;
import net.minecraft.util.DyeColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(BannerBlockEntity.class)
public interface AccessorBannerBlockEntity {
    @Accessor
    void setPatternListTag(ListTag listTag);
    @Accessor
    void setPatternListTagRead(boolean read);
    @Accessor
    void setBaseColor(DyeColor color);
}
