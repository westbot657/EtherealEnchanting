package com.westbot.ethereal_enchanting.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Uuids;

import java.util.UUID;

public record RequestEnchantmentRemovalPayload(UUID playerUuid, int syncId, String enchant) implements CustomPayload {
    public static final Id<RequestEnchantmentRemovalPayload> ID = new Id<>(ModNetworking.REQUEST_ENCHANT_REMOVAL);
    public static final PacketCodec<RegistryByteBuf, RequestEnchantmentRemovalPayload> CODEC = PacketCodec.tuple(
        Uuids.PACKET_CODEC, RequestEnchantmentRemovalPayload::playerUuid,
        PacketCodecs.INTEGER, RequestEnchantmentRemovalPayload::syncId,
        PacketCodecs.STRING, RequestEnchantmentRemovalPayload::enchant,
        RequestEnchantmentRemovalPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
