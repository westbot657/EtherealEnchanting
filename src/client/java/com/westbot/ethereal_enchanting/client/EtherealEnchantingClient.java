package com.westbot.ethereal_enchanting.client;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.client.render.AltarBlockEntityRendererFactory;
import com.westbot.ethereal_enchanting.client.render.EtherealEnchanterBlockEntityRendererFactory;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;

import static net.minecraft.client.render.block.entity.BlockEntityRendererFactories.register;

@Environment(EnvType.CLIENT)
public class EtherealEnchantingClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        register(ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, new AltarBlockEntityRendererFactory());
        register(ModBlocks.ETHEREAL_ENCHANTER_BLOCK_ENTITY_TYPE, new EtherealEnchanterBlockEntityRendererFactory());

        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ALTAR_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.PEDESTAL_BLOCK, RenderLayer.getCutout());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.ETHEREAL_ENCHANTER_BLOCK, RenderLayer.getCutout());

        ModelPredicateProviderRegistry.register(Identifier.of("ethereal_enchanting", "rune_id"), (stack, world, entity, seed) -> {
            if (stack.contains(ModComponents.RUNE_ID)) {
                Float value = stack.get(ModComponents.RUNE_ID);
                // Log.info(LogCategory.LOG, "Rune ID: " + (value == null ? "null" : (value/100.0f)));
                return value == null ? 0.26f : (value/100.0f);
            }
            return 0;
        });

    }
}
