package me.steven.carrier;

import me.steven.carrier.api.CarrierComponent;
import me.steven.carrier.api.CarryingData;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerWorldTickCallback implements ServerTickEvents.EndWorldTick {
    @Override
    public void onEndTick(ServerWorld serverWorld) {
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            CarrierComponent carrier = Carrier.HOLDER.get(player);
            CarryingData carrying = carrier.getHolding();
            if (carrying != null) {
                Config config = Carrier.CONFIG;
                if (config.getSlownessLevel() > 0)
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, config.getSlownessLevel() - 1));
                if (config.getHungerExhaustion() > 0)
                    player.getHungerManager().addExhaustion(config.getHungerExhaustion());
            }
        }
    }
}
