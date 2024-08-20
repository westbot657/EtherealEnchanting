package com.westbot.ethereal_enchanting.client.render.block;

import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;

@Environment(EnvType.CLIENT)
public class EtherealEnchanterBlockEntityRendererFactory implements BlockEntityRendererFactory<EtherealEnchanterBlockEntity> {
    @Override
    public BlockEntityRenderer<EtherealEnchanterBlockEntity> create(Context ctx) {
        return new EtherealEnchanterBlockEntityRenderer(ctx);
    }
}
