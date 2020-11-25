package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void carrier_cancelSelectedSlotChange(double scrollAmount, CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        if (carrier.getHolding() != null) ci.cancel();
    }
}
