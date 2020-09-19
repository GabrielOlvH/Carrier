package me.steven.carrier;

import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class ServerWorldTickCallback implements ServerTickEvents.EndWorldTick {
    @Override
    public void onEndTick(ServerWorld serverWorld) {
        for (ServerPlayerEntity player : serverWorld.getPlayers()) {
            Holder holder = Carrier.HOLDER.get(player);
            Holding holding = holder.getHolding();
            if (holding != null) {
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 2));
                player.getHungerManager().addExhaustion(0.05f);
            }
        }
    }
}
