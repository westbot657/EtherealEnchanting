package com.westbot.ethereal_enchanting.enchantments;

import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.enchantments.types.CelestialBindingEnchant;
import com.westbot.ethereal_enchanting.enchantments.types.SoulboundEnchant;
import net.minecraft.item.ItemStack;

import java.util.List;

public class EnchantmentManager {


    public static Enchant getEnchant(String name) {
        switch (name) {
            case "celestial_binding" -> {return CelestialBindingEnchant.getInstance();}
            case "soulbound" -> {return SoulboundEnchant.getInstance();}
            default -> {return null;}
        }
    }

    public static boolean hasEnchant(ItemStack stack, String enchant) {
        List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
        if (enchants == null) return false;

        for (EtherealEnchantComponent e : enchants) {
            if (e.enchant().equals(enchant)) {
                return true;
            }
        }
        return false;
    }

}
