package me.steven.carrier.api.event;

import me.steven.carrier.api.Carriable;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;

public interface RegisterCarriableCallback {
    Event<BlockEvent> BLOCK_EVENT = EventFactory.createArrayBacked(BlockEvent.class, callbacks -> (block) -> {
        Carriable<?> result = null;
        for (BlockEvent callback : callbacks) {
            result = callback.register(block);
            if (result != null) break;
        }

        return result;
    });

    Event<EntityEvent> ENTITY_EVENT = EventFactory.createArrayBacked(EntityEvent.class, callbacks -> (entityType) -> {
        Carriable<?> result = null;
        for (EntityEvent callback : callbacks) {
            result = callback.register(entityType);
            if (result != null) break;
        }
        return result;
    });

    interface BlockEvent {
        Carriable<?> register(Block block);
    }

    interface EntityEvent {
        Carriable<?> register(EntityType<?> entityType);
    }
}
