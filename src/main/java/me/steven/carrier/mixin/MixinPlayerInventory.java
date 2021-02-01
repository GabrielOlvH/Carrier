package me.steven.carrier.mixin;

import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {

    private static final ItemStack NON_EMPTY_STACK = new ItemStack(Items.STONE);

    @Shadow @Final public PlayerEntity player;

    @Shadow public int selectedSlot;

    @Inject(method = "scrollInHotbar", at = @At("HEAD"), cancellable = true)
    private void carrier_cancelSelectedSlotChange(double scrollAmount, CallbackInfo ci) {
        CarrierComponent carrier = Carrier.HOLDER.get(player);
        if (carrier.getCarryingData() != null) ci.cancel();
    }

    @Redirect(method = "getEmptySlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/collection/DefaultedList;get(I)Ljava/lang/Object;"))
    private Object carrier_ignoreSlot(DefaultedList<ItemStack> defaultedList, int index) {
        if (index == selectedSlot && Carrier.HOLDER.get(player).getCarryingData() != null)
            return NON_EMPTY_STACK;
        return defaultedList.get(index);
    }
}
