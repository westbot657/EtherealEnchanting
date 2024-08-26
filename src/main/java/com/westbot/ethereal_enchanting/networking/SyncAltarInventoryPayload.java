package com.westbot.ethereal_enchanting.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record SyncAltarInventoryPayload(NbtCompound blockPos, NbtCompound inventory) implements CustomPayload {
    public static final Id<SyncAltarInventoryPayload> ID = new Id<>(ModNetworking.SYNC_ALTAR_INVENTORY);
    public static final PacketCodec<RegistryByteBuf, SyncAltarInventoryPayload> CODEC = PacketCodec.tuple(
        PacketCodecs.NBT_COMPOUND, SyncAltarInventoryPayload::blockPos,
        PacketCodecs.NBT_COMPOUND, SyncAltarInventoryPayload::inventory,
        SyncAltarInventoryPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
