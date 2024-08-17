package com.westbot.ethereal_enchanting.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

public record EtherealEnchantComponent(@NotNull String enchant, int level) {

    public static final Codec<EtherealEnchantComponent> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.STRING.fieldOf("enchant").forGetter(EtherealEnchantComponent::enchant),
            Codec.INT.fieldOf("level").forGetter(EtherealEnchantComponent::level)
        ).apply(builder, EtherealEnchantComponent::new)
    );

}