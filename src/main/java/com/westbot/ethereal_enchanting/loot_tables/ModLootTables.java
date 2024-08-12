package com.westbot.ethereal_enchanting.loot_tables;

import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public class ModLootTables {


    public static void initialize() {
        LootTableEvents.MODIFY.register((lootTableRegistryKey, builder, source, wrapper) -> {

            if (lootTableRegistryKey.getValue().getNamespace().equals("minecraft") && lootTableRegistryKey.getValue().getPath().startsWith("chests/")) {
                builder.modifyPools(pool -> {
                    pool.apply(new RemoveEnchantedBookLootFunction()).build();
                }).build();
            }
        });
    }

}
