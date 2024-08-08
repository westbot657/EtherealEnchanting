package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

public class EtherealEnchanting implements ModInitializer {


    @Override
    public void onInitialize() {

        Log.info(LogCategory.LOG, "Initializing Ethereal Enchanting...");

        ModBlocks.initialize();


        Log.info(LogCategory.LOG, "Ethereal Enchanting initialized!");
    }

}
