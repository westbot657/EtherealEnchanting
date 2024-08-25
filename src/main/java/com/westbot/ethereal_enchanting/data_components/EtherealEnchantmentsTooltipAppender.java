package com.westbot.ethereal_enchanting.data_components;

import com.westbot.ethereal_enchanting.enchantments.EnchantmentManager;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.westbot.ethereal_enchanting.enchantments.EnchantmentManager.*;

public class EtherealEnchantmentsTooltipAppender {

    private static EtherealEnchantmentsTooltipAppender INSTANCE = new EtherealEnchantmentsTooltipAppender();








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

        AtomicBoolean has_enchant = new AtomicBoolean(false);
        EnchantmentManager.ENCHANT_ORDER.forEach((enchant, color) -> {
            if (remap.containsKey(enchant)) {
                if (!has_enchant.get()) {
                    tooltip.accept(Text.translatable("tooltip.ethereal_enchanting.enchant.enchants"));
                    has_enchant.set(true);
                }
                tooltip.accept(
                    Text.translatable(
                        "tooltip.ethereal_enchanting.enchant."+enchant,
                        (Objects.equals(remap.get(enchant), MAX_LEVELS.get(enchant)) ? MAX_LEVEL : LOWER_LEVEL) +
                        ROMAN_NUMERALS.getOrDefault(
                            remap.get(enchant),
                            remap.get(enchant).toString()
                        ) + "§r"
                    ).formatted(color)
                );
            }
        });

    }

}
