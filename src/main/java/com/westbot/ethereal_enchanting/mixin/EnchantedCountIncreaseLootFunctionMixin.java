package com.westbot.ethereal_enchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.enchantments.EnchantmentManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.function.EnchantedCountIncreaseLootFunction;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EnchantedCountIncreaseLootFunction.class)
public class EnchantedCountIncreaseLootFunctionMixin {

    @WrapOperation(method = "process", at= @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEquipmentLevel(Lnet/minecraft/registry/entry/RegistryEntry;Lnet/minecraft/entity/LivingEntity;)I"))
    public int wrapProcess(RegistryEntry<Enchantment> enchantment, LivingEntity entity, Operation<Integer> original) {

        int i = 0;
        ItemStack stack = entity.getMainHandStack();
        ItemStack stack2 = entity.getOffHandStack();

        if (stack != null) {
            if (EnchantmentManager.hasEnchant(stack, "luck")) {
                EtherealEnchantComponent enchant = EnchantmentManager.getEnchant(stack, "luck");
                assert enchant != null;
                i = enchant.level();
            }
        } else if (stack2 != null) {
            if (EnchantmentManager.hasEnchant(stack2, "luck")) {
                EtherealEnchantComponent enchant = EnchantmentManager.getEnchant(stack, "luck");
                assert enchant != null;
                if (enchant.level() > i) {
                    i = enchant.level();
                }
            }
        } else {
            return original.call(enchantment, entity);
        }


        return i;
    }
}
