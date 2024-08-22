package com.westbot.ethereal_enchanting.data_components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record EtherealEnchantComponent(@NotNull String enchant, int level) {

    private static final List<String> enchants = List.of(
        "celestial_binding",
        "soulbound",
        "mending",
        "unbreaking",
        "chilled",
        "incendiary",
        "slashing",
        "weighted",
        "conductive",
        "inductive",
        "resistive",
        "luck",
        "padded",
        "plated",
        "insulated",
        "elastic",
        "thorns",
        "inertial",
        "hydrodynamic",
        "swift_sneak",
        "soul_speed",
        "cruel_and_unusual"
    );

    public static final Codec<EtherealEnchantComponent> CODEC = RecordCodecBuilder.create(builder ->
        builder.group(
            Codec.STRING.fieldOf("enchant").validate(string -> {
                if (enchants.contains(string)) {
                    return DataResult.success(string);
                } else {
                    return DataResult.error(() -> "Invalid enchantment: " + string);
                }
            }).forGetter(EtherealEnchantComponent::enchant),
            Codec.INT.fieldOf("level").validate(num -> {
                if ((0 < num && num <= 10) || (-13 <= num && num <= -1)) {
                    return DataResult.success(num);
                } else {
                    return DataResult.error(() -> "Invalid enchantment level: " + num);
                }
            }).forGetter(EtherealEnchantComponent::level)
        ).apply(builder, EtherealEnchantComponent::new)
    );

}