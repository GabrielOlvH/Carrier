package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PigEntityRenderer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

public class CarrierClient implements ClientModInitializer {

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
                if (holder != null) {
                    holder.setHolding(finalHolding);
                    if (finalHolding != null) {
                        Carriable carriable = CarriableRegistry.INSTANCE.get(finalHolding.getType());
                        if (carriable instanceof EntityCarriable) {
                            EntityCarriable<?> entityCarriable = (EntityCarriable<?>) carriable;
                            entityCarriable.getEntity().fromTag(finalHolding.getTag());
                            entityCarriable.getEntity().yaw = 0;
                            entityCarriable.getEntity().prevYaw = 0;
                            entityCarriable.getEntity().pitch = 0;
                            entityCarriable.getEntity().prevPitch = 0;
                            entityCarriable.getEntity().setHeadYaw(0);
                        }
                    }
                }
            });
        });
    }
}
