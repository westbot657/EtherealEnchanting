package com.westbot.ethereal_enchanting.enchantments;

import com.westbot.ethereal_enchanting.enchantments.types.CelestialBindingEnchant;
import com.westbot.ethereal_enchanting.enchantments.types.SoulboundEnchant;

public class EnchantmentManager {


    public static Enchant getEnchant(String name) {
        switch (name) {
            case "celestial_binding" -> {return CelestialBindingEnchant.getInstance();}
            case "soulbound" -> {return SoulboundEnchant.getInstance();}
            default -> {return null;}
        }
    }


}
