package com.westbot.ethereal_enchanting.loot_tables;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.items.EnchantedRuneItem;
import com.westbot.ethereal_enchanting.items.WrittenRuneItem;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.math.random.Random;

public class RemoveEnchantedBookLootFunction implements LootFunction {


    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return LootFunctionTypes.MODIFY_CONTENTS;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            float r = lootContext.getRandom().nextFloat();
            if (r < 0.004) {
                ItemStack replacement = new ItemStack(ModItems.XP_TOME);
                replacement.set(ModComponents.TOME_XP_POINTS, lootContext.getRandom().nextInt(1629));
                return replacement;
            } else if (r < 0.104) {
                ItemStack replacement = new ItemStack(ModItems.ENCHANTED_RUNE);
                EnchantedRuneItem.setLetter(replacement, lootContext.getRandom().nextInt(26));
                return replacement;
            } else {
                ItemStack replacement = new ItemStack(ModItems.WRITTEN_RUNE);
                WrittenRuneItem.setLetter(replacement, lootContext.getRandom().nextInt(26));
                return replacement;
            }


        } else {
            return itemStack;
        }
    }
}
