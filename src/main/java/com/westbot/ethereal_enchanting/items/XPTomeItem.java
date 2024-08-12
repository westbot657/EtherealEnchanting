package com.westbot.ethereal_enchanting.items;

import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class XPTomeItem extends Item {
    public XPTomeItem() {
        super(
            new Settings().maxCount(1).component(ModComponents.TOME_XP_POINTS, 0)
        );
    }
    // 32 levels = 1628 points

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line2").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line3", getXP(stack)).formatted(Formatting.LIGHT_PURPLE));
    }

    public int getXP(ItemStack stack) {
        return stack.getOrDefault(ModComponents.TOME_XP_POINTS, 0);
    }

    public void setXP(ItemStack stack, int xp) {
        if (stack.contains(ModComponents.TOME_XP_POINTS)) {
            stack.set(ModComponents.TOME_XP_POINTS, xp);
        }
    }

}
