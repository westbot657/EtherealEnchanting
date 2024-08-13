package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.items.EnchantedRuneItem;
import com.westbot.ethereal_enchanting.items.WrittenRuneItem;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {


    public static final Item XP_TOME = register(new XPTomeItem(), "xp_tome");
    public static final Item WRITTEN_RUNE = register(new WrittenRuneItem(), "written_rune");

    public static final Item ENCHANTED_RUNE = register(new EnchantedRuneItem(), "enchanted_rune");


    public static Item register(Item item, String id) {

        return Registry.register(Registries.ITEM, Identifier.of("ethereal_enchanting", id), item);
    }

    public static void initialize() {

    }

}
