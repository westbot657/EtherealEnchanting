package com.westbot.ethereal_enchanting.client.networking;

import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.networking.BatchSyncTrailPayload;
import com.westbot.ethereal_enchanting.networking.SyncTrailPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

import java.util.UUID;

public class ModNetworkingClient {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SyncTrailPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;

                for (Entity entity : world.getEntities()) {
                    if (entity.getUuid().equals(payload.uuid())) {
                        CelestialTrailEntity entity1 = (CelestialTrailEntity) entity;
                        entity1.sync(payload.corners());
                        return;
                    }
                }

            });
        });


        ClientPlayNetworking.registerGlobalReceiver(BatchSyncTrailPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;
                for (Entity entity : world.getEntities()) {
                    if (payload.uuids().contains(entity.getUuid())) {
                        CelestialTrailEntity entity1 = (CelestialTrailEntity) entity;
                        entity1.sync(payload.compounds().get(payload.uuids().indexOf(entity.getUuid())));
                        return;
                    }
                }
            });
        });


    }

}
