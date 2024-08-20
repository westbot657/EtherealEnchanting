package com.westbot.ethereal_enchanting.client.networking;

import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.networking.SyncTrailPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ModNetworkingClient {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SyncTrailPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client() == null) return;
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
    }

}
