package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.CarriablePlacementContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
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
    @Inject(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V", shift = At.Shift.AFTER))
    private void c(CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(this);
        CarryingData carrying = carrier.getCarryingData();
        if (carrying != null) {
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(carrying.getType());
            BlockPos pos = this.getBlockPos();
            if (!world.isClient && carriable != null && world.getBlockState(pos).getMaterial().isReplaceable()) {
                carriable.tryPlace(carrier, world, new CarriablePlacementContext(carrier, carriable, pos, Direction.DOWN, this.getHorizontalFacing()));
            }
            carrier.setCarryingData(null);
        }
    }
}
