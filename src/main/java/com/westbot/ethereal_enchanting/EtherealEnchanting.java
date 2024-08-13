package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.loot_tables.ModLootTables;
import com.westbot.ethereal_enchanting.village.ModVillagerTrades;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

import java.util.logging.Logger;

public class EtherealEnchanting implements ModInitializer {

    public static final String MOD_ID = "ethereal_enchanting";


    @Override
    public void onInitialize() {

        Log.info(LogCategory.LOG, "Initializing Ethereal Enchanting...");

        ModItems.initialize();
        ModBlocks.initialize();
        ModSounds.initialize();
        ModItemGroup.initialize();
        ModLootTables.initialize();
        ModVillagerTrades.registerVillagerTrades();

        Log.info(LogCategory.LOG, "Ethereal Enchanting initialized!");
    }

}
