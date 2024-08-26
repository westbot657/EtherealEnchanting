package com.westbot.ethereal_enchanting.enchantments;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("SameReturnValue")
public abstract class Enchant {

    public boolean postHit(LivingEntity target, PlayerEntity player) {
        return true;
    }
    public void postDamageEntity(LivingEntity target, PlayerEntity player) {

    }
    public void postMine(World world, BlockState state, BlockPos pos, PlayerEntity miner) {

    }




    static Enchant getInstance() {
        return null;
    }


}
