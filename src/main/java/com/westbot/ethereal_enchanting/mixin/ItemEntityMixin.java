package com.westbot.ethereal_enchanting.mixin;


import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.enchantments.EnchantmentManager;
import com.westbot.ethereal_enchanting.items.EtherealItemEntityMix;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.Ownable;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements Ownable, EtherealItemEntityMix {

    @Shadow public abstract @Nullable Entity getOwner();

    @Shadow public abstract void setPickupDelay(int pickupDelay);

    @Shadow @Final private static TrackedData<ItemStack> STACK;

    @Shadow public abstract ItemStack getStack();

    @Unique
    boolean is_soulbound = false;
    @Unique
    boolean is_celestial_bound = false;

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/ItemEntity;)V", at = @At("TAIL"))
    private void onInit(ItemEntity itemEntity, CallbackInfo ci) {
        this.enchantingRework$setCelestialBound(((EtherealItemEntityMix)itemEntity).enchantingRework$isCelestialBound());
        this.enchantingRework$setSoulbound(((EtherealItemEntityMix)itemEntity).enchantingRework$isSoulbound());
    }

    @Unique
    @Override
    public boolean enchantingRework$isSoulbound() {
        return is_soulbound;
    }

    @Unique
    @Override
    public boolean enchantingRework$isCelestialBound() {
        return is_celestial_bound;
    }

    @Unique
    @Override
    public void enchantingRework$setSoulbound(boolean isSoulbound) {
        this.is_soulbound = isSoulbound;
    }

    @Unique
    @Override
    public void enchantingRework$setCelestialBound(boolean isCelestialBound) {
        this.is_celestial_bound = isCelestialBound;
    }

    @Inject(method = "damage", at=@At("HEAD"), cancellable = true)
    private void injectDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (
            EnchantmentManager.hasEnchant(this.getStack(), "celestial_binding") ||
                EnchantmentManager.hasEnchant(this.getStack(), "soulbound")
        ) {
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "tick", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/MathHelper;floor(D)I", ordinal = 0, shift = At.Shift.BEFORE))
    private void onTick(CallbackInfo ci) {
        if (this.enchantingRework$isSoulbound()) {
            if (this.isOnGround()) {
                World world = this.getWorld();
                if (world != null) {
                    if (!(
                        world.getBlockState(this.getBlockPos().down()).isOf(Blocks.SOUL_SAND) ||
                        world.getBlockState(this.getBlockPos().down()).isOf(Blocks.SOUL_SOIL) ||
                        world.getBlockState(this.getBlockPos()).isOf(Blocks.SOUL_SAND)
                    )) {
                        Entity owner = this.getOwner();
                        if (owner != null) {
                            this.teleportTo(new TeleportTarget((ServerWorld) owner.getWorld(), owner.getPos(), new Vec3d(0, 0, 0), owner.getYaw(), owner.getPitch(), TeleportTarget.NO_OP));
                            this.setPickupDelay(0);
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    private void onWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("enchantingRework$isSoulbound", this.enchantingRework$isSoulbound());
        nbt.putBoolean("enchantingRework$isCelestialBound", this.enchantingRework$isCelestialBound());
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    private void onReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        this.enchantingRework$setSoulbound(nbt.getBoolean("enchantingRework$isSoulbound"));
        this.enchantingRework$setCelestialBound(nbt.getBoolean("enchantingRework$isCelestialBound"));
    }

    public void tickInVoid() {

        List<EtherealEnchantComponent> enchants = this.getStack().get(ModComponents.ETHEREAL_ENCHANTS);

        if (enchants == null) {
            this.discard();
            return;
        }

        for (EtherealEnchantComponent enchant : enchants) {
            if (enchant.enchant().equals("celestial_binding")) {
                if (this.getWorld().isClient) return;
                this.teleportTo(new TeleportTarget((ServerWorld) this.getWorld(), this.getPos().withAxis(Direction.Axis.Y, this.getWorld().getBottomY()+5.5), new Vec3d(0, 0, 0), this.getYaw(), this.getPitch(), TeleportTarget.NO_OP));
                return;
            } else if (enchant.enchant().equals("soulbound") ) {
                if (this.getWorld().isClient) return;
                Entity owner = this.getOwner();
                if (owner != null) {
                    this.teleportTo(new TeleportTarget((ServerWorld) owner.getWorld(), owner.getPos(), new Vec3d(0, 0, 0), owner.getYaw(), owner.getPitch(), TeleportTarget.NO_OP));
                    this.setPickupDelay(0);
                    return;
                }
            }
        }

        this.discard();

    }


}
