package com.westbot.ethereal_enchanting.enchantments.types;

import com.westbot.ethereal_enchanting.enchantments.Enchant;

public class SoulboundEnchant extends Enchant {
    private static SoulboundEnchant instance;

    private SoulboundEnchant() {}

    public static SoulboundEnchant getInstance() {
        if (instance == null) {
            instance = new SoulboundEnchant();
        }
        return instance;
    }
}
