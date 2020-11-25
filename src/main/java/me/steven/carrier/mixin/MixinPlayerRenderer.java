package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class MixinPlayerRenderer {
    @Inject(method = "render", at = @At("TAIL"))
    private void carrier_renderCarrying(AbstractClientPlayerEntity player, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        CarryingData carrying = carrier.getHolding();
        if (carrying == null) return;
        Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
        if (carriable != null) {
            carriable.render(player, carrier, matrices, vertexConsumerProvider, tickDelta, light);
        }
    }
}
