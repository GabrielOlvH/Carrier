package me.steven.carrier.mixin;

import io.netty.buffer.Unpooled;
import me.steven.carrier.Carrier;
import me.steven.carrier.api.CarrierPlayerExtension;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class MixinClientPlayerEntity implements CarrierPlayerExtension {
    private boolean lastPressed = false;
    private boolean pressed = false;

    @Inject(method = "tick", at = @At("RETURN"))
    private void carrier_sendPacket(CallbackInfo ci) {
        if (lastPressed != pressed) {
            lastPressed = pressed;
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeBoolean(pressed);
            ClientPlayNetworking.send(Carrier.SET_CAN_CARRY_PACKET, buf);
        }
    }

    @Override
    public boolean canCarry() {
        return pressed;
    }

    @Override
    public void setCanCarry(boolean value) {
        this.pressed = value;
    }
}
