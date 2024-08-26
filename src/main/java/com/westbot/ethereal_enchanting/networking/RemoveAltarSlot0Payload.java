package com.westbot.ethereal_enchanting.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record RemoveAltarSlot0Payload(NbtCompound blockPos) implements CustomPayload {
    public static final Id<RemoveAltarSlot0Payload> ID = new Id<>(ModNetworking.SYNC_ALTAR_INVENTORY);
    public static final PacketCodec<RegistryByteBuf, RemoveAltarSlot0Payload> CODEC = PacketCodec.tuple(
        PacketCodecs.NBT_COMPOUND, RemoveAltarSlot0Payload::blockPos,
        RemoveAltarSlot0Payload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
