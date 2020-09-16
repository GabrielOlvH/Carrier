package me.steven.carrier.mixin;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
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
    private void carrier_renderCarrying(AbstractClientPlayerEntity abstractClientPlayerEntity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int light, CallbackInfo ci) {
        if (abstractClientPlayerEntity instanceof Holder) {
            Holder holder = (Holder) abstractClientPlayerEntity;
            Holding holding = holder.getHolding();
            if (holding == null) return;
            Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
            if (carriable != null) {
                carriable.render(holder, matrices, vertexConsumerProvider, tickDelta, light);

            }
        }
    }
}
