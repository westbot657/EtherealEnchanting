package com.westbot.ethereal_enchanting.networking;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record ClearAltarIngredientsPayload(NbtCompound blockPos) implements CustomPayload {
    public static final Id<ClearAltarIngredientsPayload> ID = new Id<>(ModNetworking.CLEAR_INGREDIENTS);
    public static final PacketCodec<RegistryByteBuf, ClearAltarIngredientsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.NBT_COMPOUND, ClearAltarIngredientsPayload::blockPos,
            ClearAltarIngredientsPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
