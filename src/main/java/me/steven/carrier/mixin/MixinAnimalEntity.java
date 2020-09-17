package me.steven.carrier.mixin;

import me.steven.carrier.api.Holder;
import me.steven.carrier.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AnimalEntity.class)
public abstract class MixinAnimalEntity extends Entity {

    public MixinAnimalEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "interactMob", at = @At("HEAD"), cancellable = true)
    private void carrier_tryPickup(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        Entity entity = this;
        if (entity instanceof ChickenEntity)
            CarriableChicken.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof PigEntity)
            CarriablePig.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof SheepEntity)
            CarriableSheep.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof WolfEntity)
            CarriableWolf.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof RabbitEntity)
            CarriableRabbit.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof TurtleEntity)
            CarriableTurtle.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
        else if (entity instanceof ParrotEntity)
            CarriableParrot.INSTANCE.tryPickup((Holder) player, player.world, player.getBlockPos(), this);
    }
}

