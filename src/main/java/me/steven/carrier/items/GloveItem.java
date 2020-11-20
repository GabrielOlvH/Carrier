package me.steven.carrier.items;

import me.steven.carrier.HolderInteractCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;

public class GloveItem extends Item {
    public GloveItem(Settings settings){
        super(settings);
    }
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        return HolderInteractCallback.INSTANCE.interact(player, player.world, hand, entity, true);
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return  HolderInteractCallback.INSTANCE.interact(context);
    }
}
