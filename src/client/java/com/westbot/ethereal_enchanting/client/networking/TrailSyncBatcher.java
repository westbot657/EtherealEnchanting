package com.westbot.ethereal_enchanting.client.networking;

import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.networking.BatchSyncTrailRequestPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TrailSyncBatcher {
    private static final List<UUID> uuids = new ArrayList<>();
    private static final List<CelestialTrailEntity> entities = new ArrayList<>();

    private static final int MAX_ENTITIES = 100;

    public static void requestSync(CelestialTrailEntity entity) {
        UUID uuid = entity.getUuid();


        if (uuids.contains(uuid)) {

            ClientPlayNetworking.send(new BatchSyncTrailRequestPayload(uuids.stream().toList()));
            uuids.clear();
            for (CelestialTrailEntity e : entities) {
                e.synced = true;
            }
            entity.synced = true;
            entity.syncDelay = 5;
            entities.clear();

            return;
        }
        uuids.add(uuid);
        entities.add(entity);

        if (uuids.size() >= MAX_ENTITIES) {
            ClientPlayNetworking.send(new BatchSyncTrailRequestPayload(uuids.stream().toList()));
            uuids.clear();
            for (CelestialTrailEntity e : entities) {
                e.synced = true;
                e.syncDelay = 5;
            }
            entities.clear();
        }

    }


}
