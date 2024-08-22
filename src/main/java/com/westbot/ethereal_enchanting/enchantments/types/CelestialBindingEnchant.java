package com.westbot.ethereal_enchanting.enchantments.types;

import com.westbot.ethereal_enchanting.enchantments.Enchant;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class CelestialBindingEnchant extends Enchant {
    private static CelestialBindingEnchant instance;

    private CelestialBindingEnchant() {}

    public static CelestialBindingEnchant getInstance() {
        if (instance == null) {
            instance = new CelestialBindingEnchant();
        }
        return instance;
    }

    @Override
    public boolean postHit(LivingEntity target, PlayerEntity player) {
        return false;
    }

    @Override
    public boolean postDamageEntity(LivingEntity target, PlayerEntity player) {
        return false;
    }
}
