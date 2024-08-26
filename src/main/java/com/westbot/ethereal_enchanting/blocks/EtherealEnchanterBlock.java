package com.westbot.ethereal_enchanting.blocks;

import com.mojang.serialization.MapCodec;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import com.westbot.ethereal_enchanting.screen.EtherealEnchanterScreenHandler;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.Nameable;
import net.minecraft.util.hit.BlockHitResult;
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

    @Nullable
    @Override
    protected NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EtherealEnchanterBlockEntity) {
            Text text = ((Nameable)blockEntity).getDisplayName();
            return new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new EtherealEnchanterScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos), ((EtherealEnchanterBlockEntity) blockEntity).inventory, pos), text
            );
        } else {
            return null;
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient) {
            return ActionResult.SUCCESS;
        } else {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.CONSUME;
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        EtherealEnchanterBlockEntity blockEntity = (EtherealEnchanterBlockEntity) world.getBlockEntity(pos);
        if (blockEntity != null) {
            ItemScatterer.spawn(world, pos, blockEntity.inventory);
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }
}
