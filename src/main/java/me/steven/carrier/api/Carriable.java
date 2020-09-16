package me.steven.carrier.api;

import io.netty.buffer.Unpooled;
import me.steven.carrier.Carrier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Carriable {
    @NotNull
    ActionResult tryPickup(@NotNull Holder holder, @NotNull World world, @NotNull BlockPos blockPos, @Nullable Entity entity);
    @NotNull
    ActionResult tryPlace(@NotNull Holder holder, @NotNull World world, @NotNull CarriablePlacementContext ctx);

    default void sync(Holder holder) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        Holding holding = holder.getHolding();
        buf.writeBoolean(holding == null);
        if (holding != null) {
            buf.writeCompoundTag(holding.getTag());
            buf.writeIdentifier(holding.getType());
        }
        ServerSidePacketRegistry.INSTANCE.sendToPlayer((PlayerEntity) holder, Carrier.SYNC_CARRYING_PACKET, buf);
    }

    @Environment(EnvType.CLIENT)
    void render(@NotNull Holder holder, @NotNull MatrixStack matrices, @NotNull VertexConsumerProvider vcp, float tickDelta, int light);
}