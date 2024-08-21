package com.westbot.ethereal_enchanting.networking;

import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModNetworking {

    public static final Identifier SYNC_TRAIL = Identifier.of("ethereal_enchanting", "sync_trail");


    public static final Identifier BATCH_SYNC_TRAIL = Identifier.of("ethereal_enchanting", "batch_sync_trail");
    public static final Identifier BATCH_SYNC_TRAIL_REQUEST = Identifier.of("ethereal_enchanting", "batch_sync_trail_request");


    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(SyncTrailPayload.ID, SyncTrailPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BatchSyncTrailPayload.ID, BatchSyncTrailPayload.CODEC);


        PayloadTypeRegistry.playC2S().register(BatchSyncTrailRequestPayload.ID, BatchSyncTrailRequestPayload.CODEC);


        ServerPlayNetworking.registerGlobalReceiver(BatchSyncTrailRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                List<UUID> uuids = new ArrayList<>();
                List<NbtCompound> compounds = new ArrayList<>();
                for (UUID uuid : payload.uuids()) {

                    CelestialTrailEntity entity = (CelestialTrailEntity) ((ServerWorld) context.player().getWorld()).getEntity(uuid);
                    if (entity == null) continue;
                    uuids.add(uuid);
                    compounds.add(entity.writeNbt(new NbtCompound()));
                }

                ServerPlayNetworking.send(context.player(), new BatchSyncTrailPayload(uuids, compounds));

            });
        });

    }
}
