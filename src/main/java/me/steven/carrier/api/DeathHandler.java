package me.steven.carrier.api;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import me.steven.carrier.Carrier;
import me.steven.carrier.DeathCarryingData;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PersistentState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathHandler extends PersistentState {

    private static final Logger LOGGER = LogManager.getLogger("Carrier Death Handler");

    private final Map<UUID, DeathCarryingData> deathsToPlace = new HashMap<>();

    private final Map<UUID, DeathCarryingData> failedToPlace = new HashMap<>();

    public void onDeath(PlayerEntity player) {
        CarrierComponent component = Carrier.HOLDER.get(player);
        CarryingData carryingData = component.getCarryingData();
        if (carryingData != null) {
            deathsToPlace.put(player.getUuid(), new DeathCarryingData(carryingData, player.getBlockPos()));
            component.setCarryingData(null);
            LOGGER.info("{} has died at {}, attempting to place carrying object...", player.getDisplayName().asString(), player.getBlockPos());
        }
    }

    public void tick(ServerWorld world) {
        deathsToPlace.entrySet().removeIf(entry -> {
            UUID uuid = entry.getKey();
            DeathCarryingData data = entry.getValue();
            if (data.getAttempts() > 3) {
                BlockPos top = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, data.getDeathPos());
                if (tryPlace(world, data, top, data.getAttempts() + 1, new LongOpenHashSet())) {
                    LOGGER.info("Placed {}'s carrying object @ {} (world surface) because it was not possible to place near its death site.", uuid, data.getPlacedPos());
                } else {
                    failedToPlace.put(uuid, data);
                    LOGGER.info("Unable to find valid placeable position for {}, use '/carrierrestore {} to restore their previous carrying state", uuid, uuid);
                }
                return true;
            } else if (tryPlace(world, data, data.getDeathPos(), data.getAttempts() + 1, new LongOpenHashSet())) {
                LOGGER.info("Placed {}'s carrying object @ {} after {} attempts", uuid, data.getPlacedPos(), data.getAttempts() + 1);
                return true;
            }

            data.setAttempts(data.getAttempts() + 1);
            return false;
        });
    }

    private boolean tryPlace(ServerWorld world, DeathCarryingData data, BlockPos pos, int attempt, LongOpenHashSet blocksSearched) {
        if (!blocksSearched.add(pos.asLong()) || blocksSearched.size() > attempt * 5) return false;

        BlockState blockState = world.getBlockState(pos);
        if (blockState.getMaterial().isReplaceable()) {
            Carriable<?> carriable = CarriableRegistry.INSTANCE.get(data.getData().getType());
            ActionResult result = carriable.tryPlace(data.getData(), world, new CarriablePlacementContext(data.getData().getCarriable(), pos, Direction.UP, Direction.NORTH, false));
            if (result.isAccepted()) {
                data.setPlacedPos(pos);
                return true;
            }
        }

        for (Direction dir : Direction.values()) {
            BlockPos offset = pos.offset(dir);
            if (tryPlace(world, data, offset, attempt, blocksSearched)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound toPlace = new NbtCompound();
        deathsToPlace.forEach((uuid, data) -> {
            toPlace.put(uuid.toString(), data.writeNbt());
        });
        nbt.put("toPlace", toPlace);

        NbtCompound failed = new NbtCompound();
        failedToPlace.forEach((uuid, data) -> {
            failed.put(uuid.toString(), data.writeNbt());
        });
        nbt.put("failed", failed);
        return nbt;
    }

    public static DeathHandler getDeathHandler(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(DeathHandler::fromNbt, DeathHandler::new, "carrier_deaths");
    }

    private static DeathHandler fromNbt(NbtCompound nbt) {
        DeathHandler handler = new DeathHandler();
        NbtCompound toPlace = nbt.getCompound("toPlace");
        toPlace.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            DeathCarryingData data = DeathCarryingData.fromNbt(toPlace.getCompound(key));
            handler.deathsToPlace.put(uuid, data);
        });

        NbtCompound failed = nbt.getCompound("failed");
        failed.getKeys().forEach(key -> {
            UUID uuid = UUID.fromString(key);
            DeathCarryingData data = DeathCarryingData.fromNbt(failed.getCompound(key));
            handler.failedToPlace.put(uuid, data);
        });
        return handler;
    }
}
