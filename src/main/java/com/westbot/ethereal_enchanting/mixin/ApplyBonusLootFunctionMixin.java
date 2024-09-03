package com.westbot.ethereal_enchanting.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.enchantments.EnchantmentManager;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.ApplyBonusLootFunction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ApplyBonusLootFunction.class)
public class ApplyBonusLootFunctionMixin {

    @Shadow @Final private ApplyBonusLootFunction.Formula formula;

    @Inject(method = "process", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/loot/context/LootContext;get(Lnet/minecraft/loot/context/LootContextParameter;)Ljava/lang/Object;"), cancellable = true)
    public void injectProcess(ItemStack stack, LootContext context, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = context.get(LootContextParameters.TOOL);
        if (itemStack != null) {
            if (EnchantmentManager.hasEnchant(itemStack, "luck")) {
                EtherealEnchantComponent enchant = EnchantmentManager.getEnchant(itemStack, "luck");
                assert enchant != null;
                int count = this.formula.getValue(context.getRandom(), stack.getCount(), enchant.level());
                stack.setCount(count);
                cir.setReturnValue(stack);
                cir.cancel();
            }
        }
    }
}
