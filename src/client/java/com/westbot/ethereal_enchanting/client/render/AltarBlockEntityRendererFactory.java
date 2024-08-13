package com.westbot.ethereal_enchanting.client.render;

import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

@Environment(EnvType.CLIENT)
public class AltarBlockEntityRendererFactory implements BlockEntityRendererFactory<AltarBlockEntity> {

    @Override
    public BlockEntityRenderer<AltarBlockEntity> create(Context ctx) {
        return new AltarBlockEntityRenderer();
    }
}
