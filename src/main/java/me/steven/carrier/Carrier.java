package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class Carrier implements ModInitializer {

    public static final String MOD_ID = "carrier";

    @Override
    public void onInitialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            BlockPos pos = hitResult.getBlockPos();
            Block block = world.getBlockState(pos).getBlock();
            if (player instanceof Holder) {
                Holder holder = (Holder) player;
                Holding holding = holder.getHolding();
                if (holding == null && player.isSneaking() && block instanceof Carriable) {
                    Carriable carriable = (Carriable) block;
                    ActionResult actionResult = carriable.tryPickup(holder, world, pos);
                    if (actionResult.isAccepted()) return actionResult;
                }
                if (holding == null) return ActionResult.PASS;
                Carriable carriable = CarriableRegistry.INSTANCE.get(holding.getType());
                if (carriable != null) {
                    ActionResult actionResult = carriable.tryPlace(holder, world, new CarriablePlacementContext(holder, carriable, pos.offset(hitResult.getSide()), hitResult.getSide()));
                    if (actionResult.isAccepted()) return actionResult;
                }
            }
            return ActionResult.PASS;
        });
        CarriableRegistry.INSTANCE.register(new Identifier("carrier:chest"), (Carriable) Blocks.CHEST);
    }
}
