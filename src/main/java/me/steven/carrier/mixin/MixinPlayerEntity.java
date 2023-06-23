package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import me.steven.carrier.api.DeathHandler;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends LivingEntity  {
    protected MixinPlayerEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void a(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(this);
        CarryingData carrying = carrier.getCarryingData();
        if (carrying != null) {
            setSwimming(false);
            ci.cancel();
        }
    }

    @Inject(method = "checkFallFlying", at = @At("HEAD"), cancellable = true)
    private void b(CallbackInfoReturnable<Boolean> cir) {
        CarrierComponent carrier = Carrier.HOLDER.get(this);
        CarryingData carrying = carrier.getCarryingData();
        if (carrying != null) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "dropInventory", at = @At("HEAD"))
    private void carrier_onDropInventory(CallbackInfo ci) {
        if (getWorld() instanceof ServerWorld serverWorld) {
            DeathHandler.getDeathHandler(serverWorld).onDeath((PlayerEntity) (Object) this);
        }
    }
}
