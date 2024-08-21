package com.westbot.ethereal_enchanting.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.UUID;

public record BatchSyncTrailRequestPayload(List<UUID> uuids) implements CustomPayload {
    public static final CustomPayload.Id<BatchSyncTrailRequestPayload> ID = new CustomPayload.Id<>(ModNetworking.BATCH_SYNC_TRAIL_REQUEST);
    public static final PacketCodec<RegistryByteBuf, BatchSyncTrailRequestPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC.collect(PacketCodecs.toList()), BatchSyncTrailRequestPayload::uuids,
        BatchSyncTrailRequestPayload::new
    );
    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
