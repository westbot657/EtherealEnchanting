package com.westbot.ethereal_enchanting.loot_tables;

import com.westbot.ethereal_enchanting.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;

import java.util.Collection;

public class RemoveSoulSpeedLootFunction implements LootFunction {

    @Override
    public LootFunctionType<? extends LootFunction> getType() {
        return LootFunctionTypes.MODIFY_CONTENTS;
    }

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        if (itemStack.isOf(Items.ENCHANTED_BOOK)) {
            return new ItemStack(ModItems.LOST_SOUL);
        } else {
            return itemStack;
        }
    }
}
