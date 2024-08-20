package com.westbot.ethereal_enchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantmentsTooltipAppender;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Unique
    private static final EtherealEnchantmentsTooltipAppender etherealEnchantTooltipAppender = EtherealEnchantmentsTooltipAppender.getInstance();

    @WrapOperation(method = "getTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/component/DataComponentTypes;ENCHANTMENTS:Lnet/minecraft/component/ComponentType;"))
    private ComponentType<ItemEnchantmentsComponent> enchantingRework$getTooltip(Operation<ComponentType<ItemEnchantmentsComponent>> original, @Local() Consumer<Text> consumer) {

        etherealEnchantTooltipAppender.appendTooltip((ItemStack) (Object) this, consumer);

        return null;
    }

}