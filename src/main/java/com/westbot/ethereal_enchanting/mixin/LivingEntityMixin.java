package com.westbot.ethereal_enchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.westbot.ethereal_enchanting.entity.LivingEntityExtension;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.Attackable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements Attackable, LivingEntityExtension {

    @Unique
    public boolean enchantingRework$used_totem = false;

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @WrapOperation(method="tryUseTotem", at=@At(value="INVOKE", target="Lnet/minecraft/entity/LivingEntity;setHealth(F)V"))
    private void enchantingRework$tryUseTotemWrapSetHealth(LivingEntity instance, float health, Operation<Void> original) {
        original.call(instance, health);
        Log.info(LogCategory.LOG, "Used totem!");
        enchantingRework$used_totem = true;
    }


    @Unique
    public boolean enchantingRework$usedTotem() {
        return enchantingRework$used_totem;
    }

    @Unique
    public void enchantingRework$setUsedTotem(boolean usedTotem) {
        enchantingRework$used_totem = usedTotem;
    }

}
