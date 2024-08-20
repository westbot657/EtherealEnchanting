package com.westbot.ethereal_enchanting.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;

public record SyncTrailPayload(UUID uuid, NbtCompound corners) implements CustomPayload {
    public static final CustomPayload.Id<SyncTrailPayload> ID = new CustomPayload.Id<>(ModNetworking.SYNC_TRAIL);
    public static final PacketCodec<RegistryByteBuf, SyncTrailPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, SyncTrailPayload::uuid,
        PacketCodecs.NBT_COMPOUND, SyncTrailPayload::corners,
        SyncTrailPayload::new
    );


    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
