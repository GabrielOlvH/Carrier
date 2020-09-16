package me.steven.carrier;

import me.steven.carrier.api.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
        UseBlockCallback.EVENT.register(new HolderInteractCallback());
        CarriableRegistry.INSTANCE.register(new Identifier("carrier:chest"), (Carriable) Blocks.CHEST);
    }
}
