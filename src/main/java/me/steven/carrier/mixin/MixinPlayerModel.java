package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.Holder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityModel.class)
public class MixinPlayerModel extends BipedEntityModel<PlayerEntity> {

    public MixinPlayerModel(float scale) {
        super(scale);
    }

    @Inject(method = "setAngles", at = @At("RETURN"))
    private void carrier_setAngles(LivingEntity livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) livingEntity;
        Holder holder = Carrier.HOLDER.get(player);
        if (holder.getHolding() == null) return;
        float pitch = 0.4f;
        if (player.isSneaking()) pitch = 0.8f;
        rightArm.pitch = -pitch;
        leftArm.pitch = -pitch;
    }
}
