package me.steven.carrier.mixin;

import me.steven.carrier.HolderInteractCallback;
import net.minecraft.block.AbstractBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class MixinAbstractBlock {
    @Inject(method = "onUse", at = @At("INVOKE"), cancellable = true)
    private void carrier_interactBlock(World world, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult actionResult = HolderInteractCallback.INSTANCE.interact(player, world, hand, hit);
        if (actionResult.isAccepted()) cir.setReturnValue(actionResult);
    }
}
