package com.westbot.ethereal_enchanting.client;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

import static net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register;

@Environment(EnvType.CLIENT)
public class EtherealEnchantingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        register(ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, new AltarBlockEntityRendererFactory());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALTAR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL_BLOCK, RenderLayer.getCutout());

    }
}
