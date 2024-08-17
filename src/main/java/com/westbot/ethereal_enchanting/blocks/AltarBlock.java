package com.westbot.ethereal_enchanting.blocks;

import com.mojang.serialization.MapCodec;
import com.westbot.ethereal_enchanting.ModSounds;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class AltarBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final MapCodec<AltarBlock> CODEC = createCodec(AltarBlock::new);

    public static final BooleanProperty ON_FIRE = BooleanProperty.of("on_fire");

    protected static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 1.0, 15.0),
        Block.createCuboidShape(2, 1, 2, 14, 2, 14),
        Block.createCuboidShape(7, 0, 0, 9, 2, 2),
        Block.createCuboidShape(0, 0, 7, 2, 2, 9),
        Block.createCuboidShape(7, 0, 14, 9, 2, 16),
        Block.createCuboidShape(14, 0, 7, 16, 2, 9),
        Block.createCuboidShape(5, 2, 5, 11, 9, 11),
        Block.createCuboidShape(0, 9, 0, 16, 16, 16)
    );

    public static final BooleanProperty PLACE_ANIMATION = BooleanProperty.of("place_animation");
    public static final IntProperty PEDESTALS = IntProperty.of("pedestals", 0, 3);

    public static final BooleanProperty LEFT_PEDESTAL = BooleanProperty.of("left_pedestal");
    public static final BooleanProperty RIGHT_PEDESTAL = BooleanProperty.of("right_pedestal");
    public static final BooleanProperty BACK_PEDESTAL = BooleanProperty.of("back_pedestal");

    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;



    public MapCodec<AltarBlock> getCodec() {
        return CODEC;
    }

    public AltarBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState()
            .with(PLACE_ANIMATION, true).with(PEDESTALS, 0)
            .with(LEFT_PEDESTAL, false).with(RIGHT_PEDESTAL, false)
            .with(BACK_PEDESTAL, false).with(ON_FIRE, false));

    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(PLACE_ANIMATION, PEDESTALS, FACING, LEFT_PEDESTAL, RIGHT_PEDESTAL, BACK_PEDESTAL, ON_FIRE);
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new AltarBlockEntity(pos, state);
    }


    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, AltarBlockEntity::tick);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }



    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        AltarBlockEntity blockEntity = (AltarBlockEntity) world.getBlockEntity(pos);
        assert blockEntity != null;

        HitResult lookingAt = player.raycast(6, 0, false);

        if (lookingAt.getPos().y <= pos.getY() + 3/16.0) {
            if (player.getMainHandStack().getItem() == Items.FLINT_AND_STEEL && !state.get(ON_FIRE)) {
                ItemStack stack = player.getMainHandStack();
                player.swingHand(Hand.MAIN_HAND);
                stack.damage(1, player, LivingEntity.getSlotForHand(Hand.MAIN_HAND));
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
                world.setBlockState(pos, state.with(ON_FIRE, true), 3);
            } else if (state.get(ON_FIRE)) {
                player.swingHand(Hand.MAIN_HAND);
                world.setBlockState(pos, state.with(ON_FIRE, false), 3);
                world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 1.6F + 1.8F);
            }
        } else {


            if (blockEntity.getStack(0) == ItemStack.EMPTY) {

                if (!world.isClient) {
                    ItemStack stack = player.getInventory().getMainHandStack().copyWithCount(1);
                    player.getInventory().getMainHandStack().decrementUnlessCreative(1, player);
                    blockEntity.setStack(0, stack);
                    world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 1.8F + 1.6F);
                }

            } else {
                ItemStack stack = blockEntity.getStack(0);
                blockEntity.removeStack(0);

                if (!world.isClient) {
                    player.getInventory().offerOrDrop(stack);
                    blockEntity.markDirty();
                    world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.5F, world.getRandom().nextFloat() * 1.8F + 1.6F);
                }
            }
        }
        blockEntity.verifyEnchant();
        return ActionResult.CONSUME;
    }



    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockState blockLeft = world.getBlockState(pos.offset(state.get(FACING).rotateYClockwise(), 3));
            BlockState blockBack = world.getBlockState(pos.offset(state.get(FACING).getOpposite(), 3));
            BlockState blockRight = world.getBlockState(pos.offset(state.get(FACING).rotateYCounterclockwise(), 3));

            if (blockLeft.isOf(ModBlocks.PEDESTAL_BLOCK)) {
                if (!blockLeft.get(PedestalBlock.LINKED)) {
                    world.setBlockState(
                        pos.offset(state.get(FACING).rotateYClockwise(), 3),
                        blockLeft.with(PedestalBlock.LINKED, true).with(PedestalBlock.ALTAR_DIRECTION, state.get(FACING).rotateYCounterclockwise())
                    );
                    world.setBlockState(pos, state.with(LEFT_PEDESTAL, true).with(PEDESTALS, 1));
                }
            }

            if (blockRight.isOf(ModBlocks.PEDESTAL_BLOCK)) {
                if (!blockRight.get(PedestalBlock.LINKED)) {
                    world.setBlockState(
                        pos.offset(state.get(FACING).rotateYCounterclockwise(), 3),
                        blockRight.with(PedestalBlock.LINKED, true).with(PedestalBlock.ALTAR_DIRECTION, state.get(FACING).rotateYClockwise())
                    );
                    world.setBlockState(pos, world.getBlockState(pos).with(RIGHT_PEDESTAL, true).with(PEDESTALS, world.getBlockState(pos).get(PEDESTALS) + 1));
                }
            }

            if (blockBack.isOf(ModBlocks.PEDESTAL_BLOCK)) {
                if (!blockBack.get(PedestalBlock.LINKED)) {
                    world.setBlockState(
                        pos.offset(state.get(FACING).getOpposite(), 3),
                        blockBack.with(PedestalBlock.LINKED, true).with(PedestalBlock.ALTAR_DIRECTION, state.get(FACING))
                    );
                    world.setBlockState(pos, world.getBlockState(pos).with(BACK_PEDESTAL, true).with(PEDESTALS, world.getBlockState(pos).get(PEDESTALS) + 1));
                }
            }


        }
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AltarBlockEntity) {
                ((AltarBlockEntity) blockEntity).dropItems(world, state);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {

        if (world.isClient()) {
            return;
        }

        world.playSound(null, pos, ModSounds.ALTAR_POWER_DOWN, SoundCategory.BLOCKS, 1, 0.75f);

        BlockState blockLeft = world.getBlockState(pos.offset(state.get(FACING).rotateYClockwise(), 3));
        BlockState blockBack = world.getBlockState(pos.offset(state.get(FACING).getOpposite(), 3));
        BlockState blockRight = world.getBlockState(pos.offset(state.get(FACING).rotateYCounterclockwise(), 3));

        if (blockLeft.isOf(ModBlocks.PEDESTAL_BLOCK)) {
            if (blockLeft.get(PedestalBlock.LINKED) && blockLeft.get(PedestalBlock.ALTAR_DIRECTION) == state.get(FACING).rotateYCounterclockwise()) {
                ((World)world).setBlockState(
                    pos.offset(state.get(FACING).rotateYClockwise(), 3),
                    blockLeft.with(PedestalBlock.LINKED, false).with(PedestalBlock.STATE, 0)
                );
            }
        }

        if (blockRight.isOf(ModBlocks.PEDESTAL_BLOCK)) {
            if (blockRight.get(PedestalBlock.LINKED) && blockRight.get(PedestalBlock.ALTAR_DIRECTION) == state.get(FACING).rotateYClockwise()) {
                ((World)world).setBlockState(
                    pos.offset(state.get(FACING).rotateYCounterclockwise(), 3),
                    blockRight.with(PedestalBlock.LINKED, false).with(PedestalBlock.STATE, 0)
                );
            }
        }

        if (blockBack.isOf(ModBlocks.PEDESTAL_BLOCK)) {
            if (blockBack.get(PedestalBlock.LINKED) && blockBack.get(PedestalBlock.ALTAR_DIRECTION) == state.get(FACING)) {
                ((World)world).setBlockState(
                    pos.offset(state.get(FACING).getOpposite(), 3),
                    blockBack.with(PedestalBlock.LINKED, false).with(PedestalBlock.STATE, 0)
                );
            }
        }



    }

}
