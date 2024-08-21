package com.westbot.ethereal_enchanting.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.List;
import java.util.UUID;

public record BatchSyncTrailPayload(List<UUID> uuids, List<NbtCompound> compounds) implements CustomPayload {
    public static final CustomPayload.Id<BatchSyncTrailPayload> ID = new CustomPayload.Id<>(ModNetworking.BATCH_SYNC_TRAIL);
    public static final PacketCodec<RegistryByteBuf, BatchSyncTrailPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC.collect(PacketCodecs.toList()), BatchSyncTrailPayload::uuids,
        PacketCodecs.NBT_COMPOUND.collect(PacketCodecs.toList()), BatchSyncTrailPayload::compounds,
        BatchSyncTrailPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
