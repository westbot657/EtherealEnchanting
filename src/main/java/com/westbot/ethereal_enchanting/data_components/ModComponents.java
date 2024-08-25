package com.westbot.ethereal_enchanting.data_components;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.List;

public class ModComponents {


    public static final ComponentType<Integer> TOME_XP_POINTS = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("ethereal_enchanting", "tome_xp_points"),
        ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static final ComponentType<String> RUNE_LETTER = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("ethereal_enchanting", "rune_letter"),
        ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<Float> RUNE_ID = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("ethereal_enchanting", "rune_id"),
        ComponentType.<Float>builder().codec(Codec.FLOAT).build()
    );

    public static final ComponentType<String> CIPHER_LETTERS = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("ethereal_enchanting", "cipher_letters"),
        ComponentType.<String>builder().codec(Codec.STRING).build()
    );

    public static final ComponentType<List<EtherealEnchantComponent>> ETHEREAL_ENCHANTS = Registry.register(
        Registries.DATA_COMPONENT_TYPE,
        Identifier.of("ethereal_enchanting", "ethereal_enchants"),
        ComponentType.<List<EtherealEnchantComponent>>builder().codec(Codec.list(EtherealEnchantComponent.CODEC)).build()
    );


    protected static void initialize() {

    }



}
