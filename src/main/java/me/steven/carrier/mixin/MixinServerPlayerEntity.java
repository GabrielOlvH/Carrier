package me.steven.carrier.mixin;

import me.steven.carrier.api.CarrierPlayerExtension;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity implements CarrierPlayerExtension {
    private boolean carrier_isCarryReady;

    @Override
    public boolean canCarry() {
        return carrier_isCarryReady;
    }

    @Override
    public void setCanCarry(boolean value) {
        this.carrier_isCarryReady = value;
    }
}
