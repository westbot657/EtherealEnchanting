package com.westbot.ethereal_enchanting.enchantments;

import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.enchantments.types.CelestialBindingEnchant;
import com.westbot.ethereal_enchanting.enchantments.types.SoulboundEnchant;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentManager {

    public static final String MAX_LEVEL = "§6"; // GOLD
    public static final String LOWER_LEVEL = "§r"; // WHITE

    public static final Map<String, Formatting> ENCHANT_ORDER = new HashMap<>() {{
        put("celestial_binding", Formatting.LIGHT_PURPLE);
        put("soulbound", Formatting.LIGHT_PURPLE);
        put("mending", Formatting.DARK_GREEN);
        put("unbreaking", Formatting.DARK_GREEN);
        put("chilled", Formatting.DARK_AQUA);
        put("incendiary", Formatting.RED);
        put("slashing", Formatting.GRAY);
        put("weighted", Formatting.GRAY);
        put("conductive", Formatting.GOLD);
        put("inductive", Formatting.GOLD);
        put("resistive", Formatting.GOLD);
        put("luck", Formatting.BLUE);
        put("padded", Formatting.GRAY);
        put("plated", Formatting.GRAY);
        put("insulated", Formatting.GRAY);
        put("elastic", Formatting.GREEN);
        put("thorns", Formatting.DARK_RED);
        put("inertial", Formatting.YELLOW);
        put("hydrodynamic", Formatting.DARK_AQUA);
        put("swift_sneak", Formatting.DARK_BLUE);
        put("soul_speed", Formatting.AQUA);
        put("cruel_and_unusual", Formatting.LIGHT_PURPLE);
    }};

    public static final Map<String, Integer> MAX_LEVELS = new HashMap<>() {{
        put("celestial_binding", 1);
        put("soulbound", 1);
        put("mending", 3);
        put("unbreaking", 3);
        put("chilled", 3);
        put("incendiary", 2);
        put("slashing", 5);
        put("weighted", null);
        put("conductive", 2);
        put("inductive", 2);
        put("resistive", 4);
        put("luck", 3);
        put("padded", 1);
        put("plated", 4);
        put("insulated", 4);
        put("elastic", 4);
        put("thorns", 4);
        put("inertial", 4);
        put("hydrodynamic", 3);
        put("swift_sneak", 3);
        put("soul_speed", 3);
        put("cruel_and_unusual", -6);
    }};

    public static final Map<Integer, String> ROMAN_NUMERALS = new HashMap<>() {{
        put(1, "I");
        put(2, "II");
        put(3, "III");
        put(4, "IV");
        put(5, "V");
        put(6, "VI");
        put(7, "VII");
        put(8, "VIII");
        put(9, "IX");
        put(10, "X");

        put(-13, "[|------]");
        put(-12, "[-|-----]");
        put(-11, "[--|----]");
        put(-10, "[---|---]");
        put(-9,  "[----|--]");
        put(-8,  "[-----|-]");
        put(-7,  "[------|]");

        put(-6, "§k?§r");

        put(-5, "[|----]");
        put(-4, "[-|---]");
        put(-3, "[--|--]");
        put(-2, "[---|-]");
        put(-1, "[----|]");
    }};

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
