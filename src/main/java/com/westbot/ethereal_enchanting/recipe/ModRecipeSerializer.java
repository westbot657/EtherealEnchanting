package com.westbot.ethereal_enchanting.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModRecipeSerializer {

    public static final net.minecraft.recipe.RecipeSerializer<CipherRecipe> CIPHER_RECIPE = register("cipher_recipe", new SpecialRecipeSerializer<>(CipherRecipe::new));

    static <S extends net.minecraft.recipe.RecipeSerializer<T>, T extends Recipe<?>> S register(String id, S serializer) {
        return Registry.register(Registries.RECIPE_SERIALIZER, id, serializer);
    }

    public static void initialize() {}

}
