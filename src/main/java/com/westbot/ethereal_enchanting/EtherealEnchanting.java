package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.entity.ModEntities;
import com.westbot.ethereal_enchanting.loot_tables.ModLootTables;
import com.westbot.ethereal_enchanting.networking.ModNetworking;
import com.westbot.ethereal_enchanting.village.ModVillagerTrades;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EtherealEnchanting implements ModInitializer {

    public static final String MOD_ID = "ethereal_enchanting";

    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {

        LOGGER.info("Initializing Ethereal Enchanting...");

        ModItems.initialize();
        ModBlocks.initialize();
        ModSounds.initialize();
        ModItemGroup.initialize();
        ModLootTables.initialize();
        ModVillagerTrades.registerVillagerTrades();
        ModEntities.initialize();

        ModNetworking.initialize();

        LOGGER.info("Ethereal Enchanting initialized!");
    }

}
