package com.westbot.ethereal_enchanting.village;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.items.WrittenRuneItem;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradedItem;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;

public class ModVillagerTrades {

    public static void registerVillagerTrades() {

        TradeOfferHelper.registerVillagerOffers(VillagerProfession.LIBRARIAN, 1, factories -> {
            factories.add((entity, random) -> new TradeOffer(
                new TradedItem(Items.BOOK, 1),
                Optional.of(new TradedItem(Items.EMERALD, random.nextBetween(10, 20))),
                new ItemStack(ModItems.WRITTEN_RUNE, 1),
                3, 8, 0.05f
            ));
        });

    }

}
