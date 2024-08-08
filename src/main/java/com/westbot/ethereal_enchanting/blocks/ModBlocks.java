package com.westbot.ethereal_enchanting.blocks;

import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {

    public static final AltarBlock ALTAR_BLOCK = (AltarBlock) register(new AltarBlock(
        AbstractBlock.Settings.create().luminance((BlockState state) -> 7).hardness(5f).resistance(1200f).requiresTool()
    ), "altar");


    public static final BlockEntityType<AltarBlockEntity> ALTAR_BLOCK_ENTITY_TYPE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of("ethereal_enchanting", "altar"),
        BlockEntityType.Builder.create(AltarBlockEntity::new, ALTAR_BLOCK).build()
    );;


    private static Block register(Block block, String path) {
        Registry.register(Registries.BLOCK, Identifier.of("ethereal_enchanting", path), block);
        Registry.register(Registries.ITEM, Identifier.of("ethereal_enchanting", path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {
    }

}
