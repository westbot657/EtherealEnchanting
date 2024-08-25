package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.items.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;


public class ModItems {


    public static final Item XP_TOME = register(new XPTomeItem(), "xp_tome");
    public static final Item WRITTEN_RUNE = register(new WrittenRuneItem(), "written_rune");
    public static final Item ENCHANTED_RUNE = register(new EnchantedRuneItem(), "enchanted_rune");
    public static final Item LOST_SOUL = register(new LostSoulItem(), "lost_soul");
    public static final Item WRITTEN_CIPHER = register(new WrittenCipherItem(), "written_cipher");
    public static final Item ENCHANTED_CIPHER = register(new EnchantedCipherItem(), "enchanted_cipher");

    public static Item register(Item item, String id) {

        return Registry.register(Registries.ITEM, Identifier.of("ethereal_enchanting", id), item);
    }

    public static void initialize() {

    }

}
