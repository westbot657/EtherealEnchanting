package com.westbot.ethereal_enchanting.items;

import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

public class XPTomeItem extends Item {
    public XPTomeItem() {
        super(
            new Settings().maxCount(1).component(ModComponents.TOME_XP_POINTS, 0)
        );
    }
    /*
    * 1 = 7
    * 2 = 16
    * 3 = 27
    * 4 = 40
    * 5 = 55
    * 6 = 72
    * 7 = 91
    * 8 = 112
    * 9 = 135
    * 10 = 160
    * 11 = 187
    * 12 = 216
    * 13 = 247
    * 14 = 280
    * 15 = 315
    * 16 = 352
    * 17 = 394
    * 18 = 441
    * 19 = 493
    * 20 = 550
    * 21 = 612
    * 22 = 679
    * 23 = 751
    * 24 = 828
    * 25 = 910
    * 26 = 997
    * 27 = 1083
    * 28 = 1186
    * 29 = 1288
    * 30 = 1395
    * 31 = 1507
    * 32 = 1628
    *
    */

    // 32 levels = 1628 points

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line1").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line2").formatted(Formatting.GOLD));
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line3", getXP(stack)).formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.xp_tome_tooltip_line4", (int) Util.getXpLevelFromPoints(getXP(stack))).formatted(Formatting.LIGHT_PURPLE));
    }

    public static int getXP(ItemStack stack) {
        return stack.getOrDefault(ModComponents.TOME_XP_POINTS, 0);
    }

    public static void setXP(ItemStack stack, int xp) {
        if (stack.contains(ModComponents.TOME_XP_POINTS)) {
            stack.set(ModComponents.TOME_XP_POINTS, xp);
        }
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        super.onItemEntityDestroyed(entity);
        if (entity.getWorld().isClient) return;
        int xp = getXP(entity.getStack());
        ExperienceOrbEntity.spawn((ServerWorld) entity.getWorld(), entity.getPos(), xp);

    }
}
