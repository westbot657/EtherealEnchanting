package com.westbot.ethereal_enchanting.client;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.client.networking.ModNetworkingClient;
import com.westbot.ethereal_enchanting.client.render.ModScreens;
import com.westbot.ethereal_enchanting.client.render.block.AltarBlockEntityRendererFactory;
import com.westbot.ethereal_enchanting.client.render.block.EtherealEnchanterBlockEntityRendererFactory;
import com.westbot.ethereal_enchanting.client.render.entity.CelestialTrailEntityModel;
import com.westbot.ethereal_enchanting.client.render.entity.CelestialTrailEntityRenderer;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
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

        ModNetworkingClient.initialize();

        ModScreens.initialize();

        ModelPredicateProviderRegistry.register(Identifier.of("ethereal_enchanting", "rune_id"), (stack, world, entity, seed) -> {
            if (stack.contains(ModComponents.RUNE_ID)) {
                Float value = stack.get(ModComponents.RUNE_ID);
                return value == null ? 0.26f : (value/100.0f);
            }
            return 0;
        });

        EntityRendererRegistry.register(
            ModEntities.CELESTIAL_TRAIL_TYPE,
            CelestialTrailEntityRenderer::new
        );

        EntityModelLayerRegistry.registerModelLayer(
            CelestialTrailEntityRenderer.MODEL_LAYER, CelestialTrailEntityModel::getTexturedModelData
        );

    }
}
