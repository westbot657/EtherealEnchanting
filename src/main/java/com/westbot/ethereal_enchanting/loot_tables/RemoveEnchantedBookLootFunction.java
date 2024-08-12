package com.westbot.ethereal_enchanting.loot_tables;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

public class RemoveEnchantedBookLootFunction implements LootFunction {


    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return LootFunctionTypes.MODIFY_CONTENTS;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            return ItemStack.EMPTY;
        } else {
            return itemStack;
        }
    }
}
