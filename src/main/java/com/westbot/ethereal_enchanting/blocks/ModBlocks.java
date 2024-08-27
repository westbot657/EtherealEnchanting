package com.westbot.ethereal_enchanting.blocks;

import com.westbot.ethereal_enchanting.blocks.AltarBlock;
import com.westbot.ethereal_enchanting.blocks.PedestalBlock;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlocks {

    public static final AltarBlock ALTAR_BLOCK = (AltarBlock) register(new AltarBlock(
        AbstractBlock.Settings.create().luminance((BlockState state) -> state.get(AltarBlock.ON_FIRE) ? 12 : 7).hardness(5f).resistance(1200f).requiresTool().pistonBehavior(PistonBehavior.BLOCK).nonOpaque()
    ), "altar");
    public static final PedestalBlock PEDESTAL_BLOCK = (PedestalBlock) register(new PedestalBlock(
        AbstractBlock.Settings.create().luminance((BlockState state) -> 4).hardness(3f).resistance(120f).requiresTool().pistonBehavior(PistonBehavior.BLOCK)
    ), "pedestal");

    public static final EtherealEnchanterBlock ETHEREAL_ENCHANTER_BLOCK = (EtherealEnchanterBlock) register(new EtherealEnchanterBlock(
        AbstractBlock.Settings.create().luminance((BlockState state) -> 4).hardness(5f).resistance(1200f).requiresTool().pistonBehavior(PistonBehavior.BLOCK)
    ), "ethereal_enchanter");


    public static final BlockEntityType<AltarBlockEntity> ALTAR_BLOCK_ENTITY_TYPE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of("ethereal_enchanting", "altar"),
        BlockEntityType.Builder.create(AltarBlockEntity::new, ALTAR_BLOCK).build()
    );
    public static final BlockEntityType<EtherealEnchanterBlockEntity> ETHEREAL_ENCHANTER_BLOCK_ENTITY_TYPE = Registry.register(
        Registries.BLOCK_ENTITY_TYPE,
        Identifier.of("ethereal_enchanting", "ethereal_enchanter"),
        BlockEntityType.Builder.create(EtherealEnchanterBlockEntity::new, ETHEREAL_ENCHANTER_BLOCK).build()
    );


    private static Block register(Block block, String path) {
        Registry.register(Registries.BLOCK, Identifier.of("ethereal_enchanting", path), block);
        Registry.register(Registries.ITEM, Identifier.of("ethereal_enchanting", path), new BlockItem(block, new Item.Settings()));
        return block;
    }

    public static void initialize() {
    }

}
