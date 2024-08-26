package com.westbot.ethereal_enchanting.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public record EnchanterXPSyncPayload(UUID playerUuid, int playerXp, int syncId, int tomeXp) implements CustomPayload {
    public static final Id<EnchanterXPSyncPayload> ID = new Id<>(ModNetworking.ENCHANTER_XP_SYNC);
    public static final PacketCodec<RegistryByteBuf, EnchanterXPSyncPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, EnchanterXPSyncPayload::playerUuid,
        PacketCodecs.INTEGER, EnchanterXPSyncPayload::playerXp,
        PacketCodecs.INTEGER, EnchanterXPSyncPayload::syncId,
        PacketCodecs.INTEGER, EnchanterXPSyncPayload::tomeXp,
        EnchanterXPSyncPayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
