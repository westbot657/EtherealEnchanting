package com.westbot.ethereal_enchanting.blocks;

import com.westbot.ethereal_enchanting.ModSounds;
import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import net.minecraft.block.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.Objects;

public class PedestalBlock extends Block {

    public static final IntProperty STATE = IntProperty.of("state", 0, 13);

    public static final DirectionProperty ALTAR_DIRECTION = DirectionProperty.of("altar_direction");

    public static final BooleanProperty LINKED = BooleanProperty.of("linked");

    public static final SoundEvent PEDESTAL_SOUND = ModSounds.PEDESTAL_PLACE;
    public static final SoundEvent PEDESTAL_BREAK_SOUND = ModSounds.PEDESTAL_BREAK;
    public static final int sound_volume = 1;
    public static final float sound_pitch = 1f;

    protected static final VoxelShape SHAPE = VoxelShapes.union(
        Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 2.0, 15.0),
        Block.createCuboidShape(3.0, 2.0, 3.0, 13, 12, 13),
        Block.createCuboidShape(2.0, 12.0, 2.0, 14.0, 14.0, 14.0),
        Block.createCuboidShape(0, 14, 0, 16, 16, 16),
        Block.createCuboidShape(1, 16, 1, 15, 17, 15)
    );

    public PedestalBlock(Settings settings) {
        super(settings);

        setDefaultState(getDefaultState().with(STATE, 0));
        setDefaultState(getDefaultState().with(ALTAR_DIRECTION, Direction.NORTH));
        setDefaultState(getDefaultState().with(LINKED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STATE, ALTAR_DIRECTION, LINKED);
    }

    protected boolean hasSidedTransparency(BlockState state) {
        return true;
    }

    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient) {
            BlockState blockNorth = world.getBlockState(pos.north(3));
            BlockState blockSouth = world.getBlockState(pos.south(3));
            BlockState blockEast = world.getBlockState(pos.east(3));
            BlockState blockWest = world.getBlockState(pos.west(3));

            if (blockNorth.isOf(ModBlocks.ALTAR_BLOCK)) {
                if (blockNorth.get(AltarBlock.FACING) == Direction.SOUTH) {
                    return; // do nothing because we don't care about a pedestal in front of an altar
                } else if (blockNorth.get(AltarBlock.FACING) == Direction.NORTH) {
                    world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.BACK_PEDESTAL, true));
                } else if (blockNorth.get(AltarBlock.FACING) == Direction.EAST) {
                    world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.LEFT_PEDESTAL, true));
                } else if (blockNorth.get(AltarBlock.FACING) == Direction.WEST) {
                    world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.RIGHT_PEDESTAL, true));
                }
                world.setBlockState(pos, state.with(ALTAR_DIRECTION, Direction.NORTH).with(LINKED, true));
                world.setBlockState(pos.north(3), world.getBlockState(pos.north(3)).with(AltarBlock.PEDESTALS, blockNorth.get(AltarBlock.PEDESTALS) + 1));
                world.playSound(null, pos, PEDESTAL_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            } else if (blockSouth.isOf(ModBlocks.ALTAR_BLOCK)) {
                if (blockSouth.get(AltarBlock.FACING) == Direction.SOUTH) {
                    world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.BACK_PEDESTAL, true));
                } else if (blockSouth.get(AltarBlock.FACING) == Direction.NORTH) {
                    return; // world.setBlockState(pos.north(3), blockSouth.with(AltarBlock.LEFT_PEDESTAL, true));
                } else if (blockSouth.get(AltarBlock.FACING) == Direction.EAST) {
                    world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.RIGHT_PEDESTAL, true));
                } else if (blockSouth.get(AltarBlock.FACING) == Direction.WEST) {
                    world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.LEFT_PEDESTAL, true));
                }
                world.setBlockState(pos, state.with(ALTAR_DIRECTION, Direction.SOUTH).with(LINKED, true));
                world.setBlockState(pos.south(3), world.getBlockState(pos.south(3)).with(AltarBlock.PEDESTALS, blockSouth.get(AltarBlock.PEDESTALS) + 1));
                world.playSound(null, pos, PEDESTAL_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            } else if (blockEast.isOf(ModBlocks.ALTAR_BLOCK)) {
                if (blockEast.get(AltarBlock.FACING) == Direction.SOUTH) {
                    world.setBlockState(pos.east(3), blockEast.with(AltarBlock.LEFT_PEDESTAL, true));
                } else if (blockEast.get(AltarBlock.FACING) == Direction.NORTH) {
                    world.setBlockState(pos.east(3), blockEast.with(AltarBlock.RIGHT_PEDESTAL, true));
                } else if (blockEast.get(AltarBlock.FACING) == Direction.EAST) {
                    world.setBlockState(pos.east(3), blockEast.with(AltarBlock.BACK_PEDESTAL, true));
                } else if (blockEast.get(AltarBlock.FACING) == Direction.WEST) {
                    return; // world.setBlockState(pos.west(3), blockEast.with(AltarBlock.BACK_PEDESTAL, true));
                }
                world.setBlockState(pos, state.with(ALTAR_DIRECTION, Direction.EAST).with(LINKED, true));
                world.setBlockState(pos.east(3), world.getBlockState(pos.east(3)).with(AltarBlock.PEDESTALS, blockEast.get(AltarBlock.PEDESTALS) + 1));
                world.playSound(null, pos, PEDESTAL_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            } else if (blockWest.isOf(ModBlocks.ALTAR_BLOCK)) {
                if (blockWest.get(AltarBlock.FACING) == Direction.SOUTH) {
                    world.setBlockState(pos.west(3), blockWest.with(AltarBlock.RIGHT_PEDESTAL, true));
                } else if (blockWest.get(AltarBlock.FACING) == Direction.NORTH) {
                    world.setBlockState(pos.west(3), blockWest.with(AltarBlock.LEFT_PEDESTAL, true));
                } else if (blockWest.get(AltarBlock.FACING) == Direction.EAST) {
                    return; // world.setBlockState(pos.east(3), blockWest.with(AltarBlock.BACK_PEDESTAL, true));
                } else if (blockWest.get(AltarBlock.FACING) == Direction.WEST) {
                    world.setBlockState(pos.west(3), blockWest.with(AltarBlock.BACK_PEDESTAL, true));
                }
                world.setBlockState(pos, state.with(ALTAR_DIRECTION, Direction.WEST).with(LINKED, true));
                world.setBlockState(pos.west(3), world.getBlockState(pos.west(3)).with(AltarBlock.PEDESTALS, blockWest.get(AltarBlock.PEDESTALS) + 1));
                world.playSound(null, pos, PEDESTAL_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            }

        }
    }



    private void removePedestal(World world, BlockPos pos, BlockState state) {
        BlockState blockNorth = world.getBlockState(pos.north(3));
        BlockState blockSouth = world.getBlockState(pos.south(3));
        BlockState blockEast = world.getBlockState(pos.east(3));
        BlockState blockWest = world.getBlockState(pos.west(3));

        if (blockNorth.isOf(ModBlocks.ALTAR_BLOCK) && state.get(PedestalBlock.ALTAR_DIRECTION) == Direction.NORTH) {
            if (blockNorth.get(AltarBlock.FACING) == Direction.SOUTH) {
                return; // do nothing because we don't care about a pedestal in front of an altar
            } else if (blockNorth.get(AltarBlock.FACING) == Direction.NORTH) {
                world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.BACK_PEDESTAL, false));
            } else if (blockNorth.get(AltarBlock.FACING) == Direction.EAST) {
                world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.LEFT_PEDESTAL, false));
            } else if (blockNorth.get(AltarBlock.FACING) == Direction.WEST) {
                world.setBlockState(pos.north(3), blockNorth.with(AltarBlock.RIGHT_PEDESTAL, false));
            }
            ((AltarBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos.north(3)))).dropPedestalItems(state, world);
            if (!world.isClient) {
                world.setBlockState(pos.north(3), world.getBlockState(pos.north(3)).with(AltarBlock.PEDESTALS, blockNorth.get(AltarBlock.PEDESTALS) - 1));
                world.playSound(null, pos, PEDESTAL_BREAK_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            }
        } else if (blockSouth.isOf(ModBlocks.ALTAR_BLOCK) && state.get(PedestalBlock.ALTAR_DIRECTION) == Direction.SOUTH) {
            if (blockSouth.get(AltarBlock.FACING) == Direction.SOUTH) {
                world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.BACK_PEDESTAL, false));
            } else if (blockSouth.get(AltarBlock.FACING) == Direction.NORTH) {
                return; // world.setBlockState(pos.north(3), blockSouth.with(AltarBlock.LEFT_PEDESTAL, true));
            } else if (blockSouth.get(AltarBlock.FACING) == Direction.EAST) {
                world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.RIGHT_PEDESTAL, false));
            } else if (blockSouth.get(AltarBlock.FACING) == Direction.WEST) {
                world.setBlockState(pos.south(3), blockSouth.with(AltarBlock.LEFT_PEDESTAL, false));
            }
            ((AltarBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos.south(3)))).dropPedestalItems(state, world);
            if (!world.isClient) {
                world.setBlockState(pos.south(3), world.getBlockState(pos.south(3)).with(AltarBlock.PEDESTALS, blockSouth.get(AltarBlock.PEDESTALS) - 1));
                world.playSound(null, pos, PEDESTAL_BREAK_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            }
        } else if (blockEast.isOf(ModBlocks.ALTAR_BLOCK) && state.get(PedestalBlock.ALTAR_DIRECTION) == Direction.EAST) {
            if (blockEast.get(AltarBlock.FACING) == Direction.SOUTH) {
                world.setBlockState(pos.east(3), blockEast.with(AltarBlock.LEFT_PEDESTAL, false));
            } else if (blockEast.get(AltarBlock.FACING) == Direction.NORTH) {
                world.setBlockState(pos.east(3), blockEast.with(AltarBlock.RIGHT_PEDESTAL, false));
            } else if (blockEast.get(AltarBlock.FACING) == Direction.EAST) {
                world.setBlockState(pos.east(3), blockEast.with(AltarBlock.BACK_PEDESTAL, false));
            } else if (blockEast.get(AltarBlock.FACING) == Direction.WEST) {
                return; // world.setBlockState(pos.west(3), blockEast.with(AltarBlock.BACK_PEDESTAL, true));
            }
            ((AltarBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos.east(3)))).dropPedestalItems(state, world);
            if (!world.isClient) {
                world.setBlockState(pos.east(3), world.getBlockState(pos.east(3)).with(AltarBlock.PEDESTALS, blockEast.get(AltarBlock.PEDESTALS) - 1));
                world.playSound(null, pos, PEDESTAL_BREAK_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            }
        } else if (blockWest.isOf(ModBlocks.ALTAR_BLOCK) && state.get(PedestalBlock.ALTAR_DIRECTION) == Direction.WEST) {
            if (blockWest.get(AltarBlock.FACING) == Direction.SOUTH) {
                world.setBlockState(pos.west(3), blockWest.with(AltarBlock.RIGHT_PEDESTAL, false));
            } else if (blockWest.get(AltarBlock.FACING) == Direction.NORTH) {
                world.setBlockState(pos.west(3), blockWest.with(AltarBlock.LEFT_PEDESTAL, false));
            } else if (blockWest.get(AltarBlock.FACING) == Direction.EAST) {
                return; // world.setBlockState(pos.east(3), blockWest.with(AltarBlock.BACK_PEDESTAL, true));
            } else if (blockWest.get(AltarBlock.FACING) == Direction.WEST) {
                world.setBlockState(pos.west(3), blockWest.with(AltarBlock.BACK_PEDESTAL, false));
            }
            ((AltarBlockEntity) Objects.requireNonNull(world.getBlockEntity(pos.west(3)))).dropPedestalItems(state, world);
            if (!world.isClient) {
                world.setBlockState(pos.west(3), world.getBlockState(pos.west(3)).with(AltarBlock.PEDESTALS, blockWest.get(AltarBlock.PEDESTALS) - 1));
                world.playSound(null, pos, PEDESTAL_BREAK_SOUND, SoundCategory.BLOCKS, sound_volume, sound_pitch);
            }
        }


    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {

//        if (world.isClient()) {
//            return;
//        }

        removePedestal((World) world, pos, state);
    }


    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (state.get(LINKED)) {
            AltarBlockEntity altarEntity = (AltarBlockEntity) world.getBlockEntity(pos.offset(state.get(ALTAR_DIRECTION), 3));
            ItemStack held_item = player.getStackInHand(Hand.MAIN_HAND);
            if (altarEntity == null) {
                return ActionResult.PASS;
            }

            if (held_item.isEmpty()) { // try to grab item from pedestal
                for (int i = 3; i >= 0; i--) {
                    ItemStack stack = altarEntity.getPedestalStack(i, state, world);

                    if (!stack.isEmpty()) {
                        player.getInventory().offerOrDrop(stack);
                        altarEntity.setPedestalStack(i, ItemStack.EMPTY, state, world);
                        world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.1F, world.getRandom().nextFloat() * 1.8F + 1.6F);
                        altarEntity.verifyEnchant();
                        return ActionResult.SUCCESS;
                    }

                }
            } else { // try to put item on pedestal
                for (int i = 0; i <= 3; i++) {
                    ItemStack stack = altarEntity.getPedestalStack(i, state, world);
                    if (stack.isEmpty()) {
                        ItemStack display_item = held_item.copyWithCount(1);
                        if (altarEntity.isValidItem(display_item, state)) {
                            held_item.decrementUnlessCreative(1, player);
                            altarEntity.setPedestalStack(i, display_item, state, world);
                            altarEntity.verifyEnchant();
                            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.1F, world.getRandom().nextFloat() * 1.8F + 1.6F);
                            return ActionResult.SUCCESS;
                        } else {
                            altarEntity.verifyEnchant();
                            return ActionResult.SUCCESS_NO_ITEM_USED;
                        }
                    }
                }
            }

            altarEntity.verifyEnchant();
            return ActionResult.CONSUME;
        } else {

            return ActionResult.SUCCESS_NO_ITEM_USED;
        }
    }


}
