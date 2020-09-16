package me.steven.carrier;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

public class Utils {
    public static CompoundTag inventoryToTag(Inventory inventory, CompoundTag tag) {
        ListTag listTag = new ListTag();

        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                CompoundTag compoundTag = new CompoundTag();
                compoundTag.putByte("Slot", (byte) i);
                itemStack.toTag(compoundTag);
                listTag.add(compoundTag);
            }
        }

        if (!listTag.isEmpty()) {
            tag.put("Items", listTag);
        }

        return tag;
    }

    public static void inventoryFromTag(Inventory inventory, CompoundTag tag) {
        ListTag listTag = tag.getList("Items", 10);

        for(int i = 0; i < listTag.size(); ++i) {
            CompoundTag compoundTag = listTag.getCompound(i);
            int j = compoundTag.getByte("Slot") & 255;
            if (j < inventory.size()) {
                inventory.setStack(j, ItemStack.fromTag(compoundTag));
            }
        }
    }
}
