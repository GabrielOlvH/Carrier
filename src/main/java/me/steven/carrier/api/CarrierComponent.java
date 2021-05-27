package me.steven.carrier.api;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import me.steven.carrier.Carrier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CarrierComponent implements ComponentV3, AutoSyncedComponent {
    private CarryingData carrying;
    private final PlayerEntity owner;

    public CarrierComponent(PlayerEntity player) {
        this.owner = player;
    }

    @Nullable
    public CarryingData getCarryingData() {
        return carrying;
    }

    public void setCarryingData(@Nullable CarryingData carrying) {
        this.carrying = carrying;
        Carrier.HOLDER.sync(owner);
    }

    public PlayerEntity getOwner() {
        return owner;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        if (tag.contains("carrying")) {
            NbtCompound carryingTag = tag.getCompound("carrying");
            Identifier id = new Identifier(carryingTag.getString("type"));
            if (!CarriableRegistry.INSTANCE.contains(id)) return;
            CarryingData carrying = new CarryingData(id, carryingTag);
            setCarryingData(carrying);
        } else setCarryingData(null);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        if (carrying != null) {
            NbtCompound carryingTag = carrying.getTag();
            carryingTag.putString("type", carrying.getType().toString());
            tag.put("carrying", carryingTag);
        }
    }
}
