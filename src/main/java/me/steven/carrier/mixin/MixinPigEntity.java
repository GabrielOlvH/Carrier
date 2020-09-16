package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.*;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigEntity.class)
public abstract class MixinPigEntity extends AnimalEntity {

    private static final Identifier CARRIABLE_TYPE = new Identifier("carrier", "pig");
    private static final Carriable PIG_CARRIABLE = CarriableRegistry.INSTANCE.register(CARRIABLE_TYPE, new EntityCarriable(CARRIABLE_TYPE, EntityType.PIG));

    protected MixinPigEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void carrier_tryPickup(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        PIG_CARRIABLE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
    }
}
