package com.westbot.ethereal_enchanting.recipe;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.items.EnchantedCipherItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class CipherRecipe extends SpecialCraftingRecipe {
    public CipherRecipe(CraftingRecipeCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingRecipeInput input, World world) {
        return matches(input);
    }

    public boolean matches(CraftingRecipeInput input) {
        boolean hasBook = false;
        boolean hasWrittenCipher = false;
        boolean hasEnchantedCipher = false;
        int numWritten = 0;
        int numEnchanted = 0;

        for (int i=0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isOf(ModItems.WRITTEN_RUNE)) {
                if (numEnchanted > 0) return false;
                numWritten++;
            } else if (stack.isOf(ModItems.ENCHANTED_RUNE)) {
                if (numWritten > 0) return false;
                numEnchanted++;
            } else if (stack.isOf(Items.BOOK)) {
                if (hasBook || hasEnchantedCipher || hasWrittenCipher) return false;
                hasBook = true;
            } else if (stack.isOf(ModItems.WRITTEN_CIPHER)) {
                if (numEnchanted > 0 || hasBook || hasEnchantedCipher || hasWrittenCipher) return false;
                hasWrittenCipher = true;
            } else if (stack.isOf(ModItems.ENCHANTED_CIPHER)) {
                if (numEnchanted > 0 || hasBook || hasEnchantedCipher || hasWrittenCipher) return false;
                hasEnchantedCipher = true;
            } else if (!stack.isEmpty()) {
                return false;
            }
        }

        return (
            (hasBook && (numWritten+numEnchanted > 0)) ||
                (hasEnchantedCipher && (numEnchanted > 0) && (numWritten == 0)) ||
                (hasWrittenCipher && (numEnchanted == 0) && (numWritten > 0))
        );
    }

    @Override
    public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
        if (!matches(input)) {return ItemStack.EMPTY; }

        List<String> letters = new ArrayList<>();
        boolean enchanted = false;

        ItemStack cipher = ItemStack.EMPTY;

        for (int i = 0; i < input.getSize(); i++) {
            ItemStack stack = input.getStackInSlot(i);
            if (stack.isOf(ModItems.ENCHANTED_RUNE)) {
                letters.add(stack.get(ModComponents.RUNE_LETTER));
                enchanted = true;
            } else if (stack.isOf(ModItems.WRITTEN_RUNE)) {
                letters.add(stack.get(ModComponents.RUNE_LETTER));
            } else if (stack.isOf(ModItems.ENCHANTED_CIPHER) || stack.isOf(ModItems.WRITTEN_CIPHER)) {
                cipher = stack.copy();
            }
        }

        if (cipher.isEmpty()) {
            if (enchanted) {
                cipher = new ItemStack(ModItems.ENCHANTED_CIPHER);
            } else {
                cipher = new ItemStack(ModItems.WRITTEN_CIPHER);
            }
        }
        for (String l : letters) {
            EnchantedCipherItem.addLetter(cipher, l);
        }
        return cipher;

    }

    @Override
    public boolean fits(int width, int height) {
        return width * height > 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializer.CIPHER_RECIPE;
    }
}
