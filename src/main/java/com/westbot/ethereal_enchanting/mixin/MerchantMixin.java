package com.westbot.ethereal_enchanting.mixin;

import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.Npc;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;

@Mixin(MerchantEntity.class)
public abstract class MerchantMixin extends PassiveEntity implements InventoryOwner, Npc, Merchant {


    protected MerchantMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method="<init>", at=@At(value = "RETURN"))
    private void onInit(CallbackInfo ci) {
        // having this function here seems to make the mixin actually load

    }

    @Inject(method="fillRecipesFromPool", at=@At(value="INVOKE_ASSIGN", target="Lcom/google/common/collect/Lists;newArrayList([Ljava/lang/Object;)Ljava/util/ArrayList;", shift=At.Shift.AFTER), cancellable=true, locals=LocalCapture.CAPTURE_FAILHARD)
    protected void fillRecipesFromPool(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count, CallbackInfo ci, ArrayList<TradeOffers.Factory> arrayList) {
        int i = 0;
        while(i < count && !arrayList.isEmpty()) {
            TradeOffer tradeOffer = (arrayList.remove(this.random.nextInt(arrayList.size()))).create(this, this.random);

            if (tradeOffer != null && !tradeOffer.getSellItem().isOf(Items.ENCHANTED_BOOK)) {
                recipeList.add(tradeOffer);
                ++i;
            }
        }
        ci.cancel();

    }

}
