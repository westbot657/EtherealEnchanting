package com.westbot.ethereal_enchanting.blocks;

import com.mojang.serialization.MapCodec;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EtherealEnchanterBlock extends BlockWithEntity {

    public static final MapCodec<EtherealEnchanterBlock> CODEC = createCodec(EtherealEnchanterBlock::new);

    protected static final VoxelShape SHAPE = Block.createCuboidShape(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);

    protected EtherealEnchanterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }


    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EtherealEnchanterBlockEntity(pos, state);
    }


    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.ETHEREAL_ENCHANTER_BLOCK_ENTITY_TYPE, EtherealEnchanterBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
