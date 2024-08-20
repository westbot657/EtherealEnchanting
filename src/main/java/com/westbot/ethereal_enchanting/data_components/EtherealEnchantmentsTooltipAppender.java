package com.westbot.ethereal_enchanting.data_components;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EtherealEnchantmentsTooltipAppender {

    private static EtherealEnchantmentsTooltipAppender INSTANCE = new EtherealEnchantmentsTooltipAppender();

    private static final Map<String, Formatting> ENCHANT_ORDER = new HashMap<>() {{
        put("celestial_binding", Formatting.LIGHT_PURPLE);
        put("soulbound", Formatting.LIGHT_PURPLE);
        put("mending", Formatting.DARK_GREEN);
        put("unbreaking", Formatting.DARK_GREEN);
    }};

    private static final Map<Integer, String> ROMAN_NUMERALS = new HashMap<>() {{
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
    }};

    private EtherealEnchantmentsTooltipAppender() {
    }

    public static EtherealEnchantmentsTooltipAppender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new EtherealEnchantmentsTooltipAppender();
        }

        return INSTANCE;
    }

    public void appendTooltip(ItemStack stack, Consumer<Text> tooltip) {
        List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);

        if (enchants == null) {
            return;
        }

        Map<String, Integer> remap = new HashMap<>();

        for (EtherealEnchantComponent enchant : enchants) {
            remap.put(enchant.enchant(), enchant.level());
        }

        ENCHANT_ORDER.forEach((enchant, color) -> {
            if (remap.containsKey(enchant)) {
                tooltip.accept(Text.translatable("tooltip.ethereal_enchanting.enchant."+enchant, ROMAN_NUMERALS.getOrDefault(remap.get(enchant), remap.get(enchant).toString())).formatted(color));
            }
        });

    }

}
