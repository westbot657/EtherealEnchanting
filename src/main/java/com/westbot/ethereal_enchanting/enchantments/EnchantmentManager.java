package com.westbot.ethereal_enchanting.enchantments;

import com.westbot.ethereal_enchanting.enchantments.types.CelestialBindingEnchant;

public class EnchantmentManager {


    public static Enchant getEnchant(String name) {
        switch (name) {
            case "celestial_binding" -> {return CelestialBindingEnchant.getInstance();}
            case "soulbound" -> {return null;}
            default -> {return null;}
        }
    }


}
