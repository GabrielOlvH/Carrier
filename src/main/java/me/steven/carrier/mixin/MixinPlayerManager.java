package me.steven.carrier.mixin;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "onPlayerConnect", at = @At("RETURN"))
    private void carrier_syncCarrying(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        if (player instanceof Holder) {
            Holder holder = (Holder) player;
            Holding holding = holder.getHolding();
            if (holding == null || !CarriableRegistry.INSTANCE.contains(holding.getType())) return;
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            carriable.sync(holder);
        }
    }
}
