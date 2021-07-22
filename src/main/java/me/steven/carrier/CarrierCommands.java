package me.steven.carrier;

import me.steven.carrier.api.Carriable;
import me.steven.carrier.api.CarriablePlacementContext;
import me.steven.carrier.api.CarriableRegistry;
import me.steven.carrier.api.CarrierComponent;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.util.math.BlockPos;

public class CarrierCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
                commandDispatcher.register(CommandManager.literal("carrierinfo")
                        .executes((ctx) -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            NbtCompound tag = new NbtCompound();
                            Carrier.HOLDER.get(player).writeToNbt(tag);
                            ctx.getSource().sendFeedback(new LiteralText(tag.toString()), false);
                            return 1;
                        })));

        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
                commandDispatcher.register(CommandManager.literal("carrierdelete")
                        .executes((ctx) -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            CarrierComponent component = Carrier.HOLDER.get(player);
                            NbtCompound tag = new NbtCompound();
                            component.writeToNbt(tag);
                            component.setCarryingData(null);
                            ctx.getSource().sendFeedback(new LiteralText("Deleted ").append(new LiteralText(tag.toString()).setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, tag.toString())))), false);
                            return 1;
                        })));

        CommandRegistrationCallback.EVENT.register((commandDispatcher, b) ->
                commandDispatcher.register(CommandManager.literal("carrierplace")
                        .executes((ctx) -> {
                            ServerPlayerEntity player = ctx.getSource().getPlayer();
                            CarrierComponent component = Carrier.HOLDER.get(player);
                            Carriable<Object> carriable = CarriableRegistry.INSTANCE.get(component.getCarryingData().getType());
                            BlockPos pos = player.getBlockPos().offset(player.getHorizontalFacing());
                            ServerWorld world = ctx.getSource().getWorld();
                            if (!world.getBlockState(pos).getMaterial().isReplaceable()) {
                                ctx.getSource().sendFeedback(new LiteralText("Could not place! Make sure you have empty space in front of you."), false);
                                return 1;
                            }
                            CarriablePlacementContext placementCtx = new CarriablePlacementContext(carriable, pos, player.getHorizontalFacing().getOpposite(), player.getHorizontalFacing(), false);
                            carriable.tryPlace(component.getCarryingData(), world, placementCtx);
                            component.setCarryingData(null);
                            return 1;
                        })));
    }
}
