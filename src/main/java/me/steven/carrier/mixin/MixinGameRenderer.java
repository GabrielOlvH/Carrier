package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Inject(method = "renderWorld", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/GameRenderer;renderHand(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/Camera;F)V"), cancellable = true)
    private void carrier_renderCarrying(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getCarryingData();
        if (carrying == null) return;
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
        if (carriable != null) {
            ci.cancel();
        }
    }
}
