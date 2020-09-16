package me.steven.carrier.mixin;

import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class MixinPlayerEntity implements Holder {

    private Holding holding;

    @Nullable
    @Override
    public Holding getHolding() {
        return holding;
    }
    @Override
    public void setHolding(@Nullable Holding holding) {
        this.holding = holding;
    }

    @Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
    private void carrier_writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
        if (holding != null) {
            CompoundTag holdingTag = holding.getTag();
            holdingTag.putString("type", holding.getType().toString());
            tag.put("holding", holdingTag);
        }
    }

    @Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
    private void carrier_readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
        if (tag.contains("holding")) {

            CompoundTag carryingTag = tag.getCompound("holding");
            Identifier id = new Identifier(carryingTag.getString("type"));
            if (!CarriableRegistry.INSTANCE.contains(id)) return;
            Holding holding = new Holding(id, carryingTag);
            setHolding(holding);
        }
    }

}
