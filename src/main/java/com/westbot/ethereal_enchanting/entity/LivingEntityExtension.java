package com.westbot.ethereal_enchanting.entity;

import net.minecraft.entity.Entity;

import java.util.concurrent.atomic.AtomicBoolean;

public interface LivingEntityExtension {

    boolean enchantingRework$usedTotem();

    void enchantingRework$setUsedTotem(boolean value);

    boolean enchantingRework$shouldHideSpellBook();
    void enchantingRework$setHideSpellBook(boolean value);

}
