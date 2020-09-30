package me.steven.carrier.mixin;

import me.steven.carrier.HolderInteractCallback;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobEntity.class)
public class MixinMobEntity {
    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void carrier_interactMob(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        MobEntity entity = (MobEntity) (Object) this;
        ActionResult actionResult = HolderInteractCallback.INSTANCE.interact(player, player.world, hand, entity);
        if (actionResult.isAccepted()) cir.setReturnValue(actionResult);
    }
}
