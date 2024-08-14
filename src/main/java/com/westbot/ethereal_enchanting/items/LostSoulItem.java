package com.westbot.ethereal_enchanting.items;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public class LostSoulItem extends Item {

    public LostSoulItem() {
        super(new Settings().maxCount(1).fireproof().rarity(Rarity.RARE));
    }
}
