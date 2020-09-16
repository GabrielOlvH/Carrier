package me.steven.carrier;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.Holder;
import me.steven.carrier.api.Holding;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class CarrierClient implements ClientModInitializer {

    private static PigEntity DUMMY_PIG;
    private static PigEntityRenderer PIG_RENDERER;

    public static PigEntity getDummyPig() {
        if (DUMMY_PIG == null)
            DUMMY_PIG = new PigEntity(EntityType.PIG, MinecraftClient.getInstance().world);
        return DUMMY_PIG;
    }

    public static PigEntityRenderer getPigRenderer() {
        if (PIG_RENDERER == null)
            PIG_RENDERER = new PigEntityRenderer(MinecraftClient.getInstance().getEntityRenderDispatcher());
        return PIG_RENDERER;
    }

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Carrier.SYNC_CARRYING_PACKET, (ctx, buf) -> {
            boolean isNull = buf.readBoolean();
            Holding holding = null;
            if (!isNull) {
                CompoundTag tag = buf.readCompoundTag();
                Identifier type = buf.readIdentifier();
                if (tag == null) return;
                if (!CarriableRegistry.INSTANCE.contains(type)) {
                    Carrier.LOGGER.error("Received unknown carriable type " + type);
                    return;
                }
                holding = new Holding(type, tag);
            }
            Holding finalHolding = holding;
            ctx.getTaskQueue().execute(() -> {
                Holder holder = (Holder) MinecraftClient.getInstance().player;
                if (holder != null)
                    holder.setHolding(finalHolding);
            });
        });
    }
}
