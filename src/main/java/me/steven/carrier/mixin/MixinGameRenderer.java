package me.steven.carrier.mixin;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V"), cancellable = true)
    private void carrier_renderCarrying(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player instanceof Holder) {
            Holder holder = (Holder) player;
            Holding holding = holder.getHolding();
            if (holding == null) return ;
            Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (carriable != null) {
                ci.cancel();
            }
        }
    }
}
