package com.westbot.ethereal_enchanting.enchantments;

import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentManager {

    public static final String MAX_LEVEL = "§6"; // GOLD
    public static final String LOWER_LEVEL = "§r"; // WHITE

    public static final Map<String, Formatting> ENCHANT_ORDER = new HashMap<>() {{

        put("celestial_binding", Formatting.LIGHT_PURPLE);
        // items have no gravity and can't be destroyed

        put("soulbound", Formatting.LIGHT_PURPLE);
        // items teleport to whoever threw it as soon as it hits the ground (except if landed on soul sand/soil)

        put("mending", Formatting.DARK_GREEN);
        // xp from the world and xp tomes can be used to repair armor

        put("unbreaking", Formatting.DARK_GREEN);
        // durability is less likely to be used

        put("chilled", Formatting.DARK_AQUA);
        // slows enemies that you attack, also acts as frost walker

        put("incendiary", Formatting.RED);
        // acts as fire aspect

        put("slashing", Formatting.GRAY);
        // acts as sharpness

        put("weighted", Formatting.GRAY);
        // balances between attack speed and attack sweeping

        put("conductive", Formatting.GOLD);
        // part 1 of the electric combo
        // enables higher damage from an attack with inductive

        put("inductive", Formatting.GOLD);
        // part 2 of the electric combo
        // deals higher damage after an attack from conductive

        put("resistive", Formatting.GOLD);
        // part 3 of the electric combo
        // blocks damage from the electric combo

        put("luck", Formatting.BLUE);
        // fortune / looting

        put("padded", Formatting.GRAY);
        // silk touch + makes armor equipping silent (maybe does a little for fall damage?)

        put("plated", Formatting.GRAY);
        // acts as basic protection, but with less protection against some damage types

        put("insulated", Formatting.GRAY);
        // acts as protection against fire and ice

        put("elastic", Formatting.GREEN);
        // protection against mace attacks

        put("thorns", Formatting.DARK_RED);
        // splits damage that you would've taken between you and your attacker

        put("inertial", Formatting.YELLOW);
        // weak attacks do a little more damage than normal, but higher damages are reduced
        // in a way where you shouldn't ever get one-shot from full health

        put("hydrodynamic", Formatting.DARK_AQUA);
        // aqua affinity, respiration, and depth strider

        put("swift_sneak", Formatting.DARK_BLUE);
        // classic swift sneak

        put("soul_speed", Formatting.AQUA);
        // classic soul speed, though maybe a small boost if you are carrying a lost soul

        put("cruel_and_unusual", Formatting.LIGHT_PURPLE);
        // I haven't figured out what this'll do

        // TODO: add vein miner
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

    @Nullable
    public static EtherealEnchantComponent getEnchant(ItemStack stack, String enchant) {
        List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
        if (enchants == null) return null;

        for (EtherealEnchantComponent e : enchants) {
            if (e.enchant().equals(enchant)) {
                return e;
            }
        }
        return null;
    }

}
