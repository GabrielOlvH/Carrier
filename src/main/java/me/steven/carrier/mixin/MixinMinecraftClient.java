package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierComponent;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {
    @Inject(
            method = "handleInputEvents",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/player/PlayerInventory;selectedSlot:I"),
            cancellable = true
    )
    private void carrier_cancelHotbarSelect(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(MinecraftClient.getInstance().player);
        if (carrier.getCarryingData() != null) ci.cancel();
    }

    @Inject(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;doAttack()Z"),
            cancellable = true
    )
    private void carrier_cancelPunch(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(MinecraftClient.getInstance().player);
        if (carrier.getCarryingData() != null) ci.cancel();
    }

    @ModifyArg(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;handleBlockBreaking(Z)V")
    )
    private boolean carrier_cancelBlockBreak(boolean value) {
        CarrierComponent carrier = Carrier.HOLDER.get(MinecraftClient.getInstance().player);
        if (carrier.getCarryingData() != null) return false;
        return value;
    }

    @Inject(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;dropSelectedItem(Z)Z"),
            cancellable = true
    )
    private void carrier_cancelDrop(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(MinecraftClient.getInstance().player);
        if (carrier.getCarryingData() != null) ci.cancel();
    }

    @Inject(
            method = "handleInputEvents",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setScreen(Lnet/minecraft/client/gui/screen/Screen;)V"),
            slice = @Slice(from = @At(value = "FIELD", target = "Lnet/minecraft/client/option/GameOptions;inventoryKey:Lnet/minecraft/client/option/KeyBinding;")),
            cancellable = true
    )
    private void carrier_cancelOpenInventory(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(MinecraftClient.getInstance().player);
        if (carrier.getCarryingData() != null) ci.cancel();
    }
}
