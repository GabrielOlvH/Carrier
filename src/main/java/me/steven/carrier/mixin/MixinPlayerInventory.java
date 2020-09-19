package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Holder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
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
        Holder holder = Carrier.HOLDER.get(player);
        if (holder.getHolding() != null) ci.cancel();
    }
}
