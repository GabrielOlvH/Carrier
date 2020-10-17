package me.steven.carrier.api;

import dev.onyxstudios.cca.api.v3.component.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import me.steven.carrier.Carrier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class Holder implements ComponentV3, AutoSyncedComponent {
    private Holding holding;
    private final PlayerEntity owner;

    public Holder(PlayerEntity player) {
        this.owner = player;
    }

    @Nullable
    public Holding getHolding() {
        return holding;
    }

    public void setHolding(@Nullable Holding holding) {
        this.holding = holding;
        Carrier.HOLDER.sync(owner);
    }

    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public void readFromNbt(CompoundTag tag) {
        if (tag.contains("holding")) {
            CompoundTag carryingTag = tag.getCompound("holding");
            Identifier id = new Identifier(carryingTag.getString("type"));
            if (!CarriableRegistry.INSTANCE.contains(id)) return;
            Holding holding = new Holding(id, carryingTag);
            setHolding(holding);
        } else setHolding(null);
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        if (holding != null) {
            CompoundTag holdingTag = holding.getTag();
            holdingTag.putString("type", holding.getType().toString());
            tag.put("holding", holdingTag);
        }
    }
}
