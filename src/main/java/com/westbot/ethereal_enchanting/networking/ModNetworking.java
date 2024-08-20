package com.westbot.ethereal_enchanting.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.util.Identifier;

public class ModNetworking {

    public static final Identifier SYNC_TRAIL = Identifier.of("ethereal_enchanting", "sync_trail");

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(SyncTrailPayload.ID, SyncTrailPayload.CODEC);
    }
}
