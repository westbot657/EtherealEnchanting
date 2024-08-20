package com.westbot.ethereal_enchanting.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.entity.ModEntities;
import com.westbot.ethereal_enchanting.entity.LivingEntityExtension;
import com.westbot.ethereal_enchanting.items.EtherealItemEntityMix;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements LivingEntityExtension {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @WrapOperation(method="dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at=@At(value="INVOKE", target="Lnet/minecraft/entity/ItemEntity;setPickupDelay(I)V"))
    private void dropItemWrap(ItemEntity itemEntity, int pickupDelay, Operation<Void> original, @Local(argsOnly = true, ordinal = 1) LocalBooleanRef retainOwnership) {
        original.call(itemEntity, pickupDelay);

        List<EtherealEnchantComponent> enchants = itemEntity.getStack().get(ModComponents.ETHEREAL_ENCHANTS);

        if (enchants != null) {
            for (EtherealEnchantComponent enchant : enchants) {
                if (enchant.enchant().equals("celestial_binding")) {
                    itemEntity.setNeverDespawn();
                    itemEntity.setNoGravity(true);
                    itemEntity.setInvulnerable(true);
                    retainOwnership.set(true);
                    CelestialTrailEntity trail = new CelestialTrailEntity(ModEntities.CELESTIAL_TRAIL_TYPE, getWorld());
                    trail.trackEntity(itemEntity);
                    trail.setPos(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
                    getWorld().spawnEntity(trail);
                    ((EtherealItemEntityMix) itemEntity).enchantingRework$setCelestialBound(true);

                } else if (enchant.enchant().equals("soulbound")) {
                    itemEntity.setNeverDespawn();
                    retainOwnership.set(true);
                    ((EtherealItemEntityMix) itemEntity).enchantingRework$setSoulbound(true);
                }
            }

        }
    }

}