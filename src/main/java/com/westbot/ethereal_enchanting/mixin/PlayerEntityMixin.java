package com.westbot.ethereal_enchanting.mixin;

import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.items.EtherealItemEntityMix;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At(value = "HEAD"), cancellable = true)
    private void dropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.isEmpty()) {
            cir.setReturnValue(null);
            cir.cancel();
        } else {
            if (this.getWorld().isClient) {
                this.swingHand(Hand.MAIN_HAND);
            }

            double d = this.getEyeY() - 0.3F;
            ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), d, this.getZ(), stack);
            itemEntity.setPickupDelay(40);

            List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
            if (enchants != null) {
                for (EtherealEnchantComponent enchant : enchants) {
                    if (enchant.enchant().equals("celestial_binding")) {
                        itemEntity.setNeverDespawn();
                        itemEntity.setNoGravity(true);
                        itemEntity.setInvulnerable(true);
                        retainOwnership = true;
                        ((EtherealItemEntityMix) itemEntity).enchantingRework$setCelestialBound(true);
                    } else if (enchant.enchant().equals("soulbound")) {
                        itemEntity.setNeverDespawn();
                        retainOwnership = true;
                        ((EtherealItemEntityMix) itemEntity).enchantingRework$setSoulbound(true);
                    }
                }
            }

            if (retainOwnership) {
                itemEntity.setThrower(this);
            }

            if (throwRandomly) {
                float f = this.random.nextFloat() * 0.5F;
                float g = this.random.nextFloat() * (float) (Math.PI * 2);
                itemEntity.setVelocity((-MathHelper.sin(g) * f), 0.2F, (MathHelper.cos(g) * f));
            } else {
                float g = MathHelper.sin(this.getPitch() * (float) (Math.PI / 180.0));
                float h = MathHelper.cos(this.getPitch() * (float) (Math.PI / 180.0));
                float i = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0));
                float j = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0));
                float k = this.random.nextFloat() * (float) (Math.PI * 2);
                float l = 0.02F * this.random.nextFloat();
                itemEntity.setVelocity(
                    (double)(-i * h * 0.3F) + Math.cos(k) * (double)l,
                    (-g * 0.3F + 0.1F + (this.random.nextFloat() - this.random.nextFloat()) * 0.1F),
                    (double)(j * h * 0.3F) + Math.sin(k) * (double)l
                );
            }
            cir.setReturnValue(itemEntity);
            cir.cancel();
        }
    }

}
