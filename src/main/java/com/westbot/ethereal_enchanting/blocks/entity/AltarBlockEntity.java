package com.westbot.ethereal_enchanting.blocks.entity;

import com.westbot.ethereal_enchanting.ModSounds;
import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.blocks.AltarBlock;
import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.blocks.PedestalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Objects;


public class AltarBlockEntity extends BlockEntity {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> floorBlocks = DefaultedList.ofSize(4, ItemStack.EMPTY);


    private int rotation = 0;
    private final int rotation_max = 360/6;
    private boolean place_animation = true;
    private int animation_stage = 0;

    private int rotation_tick = 0;
    private int rotation_height = 0;

    private int animation_phase = 0;

    private int ticks = -1;

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, pos, state);

    }

    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    private void getFloorBlocks() {
        World world = this.getWorld();
        if (world == null) {
            return;
        }
        BlockState altarState = world.getBlockState(this.getPos());

        this.floorBlocks.set(0, world.getBlockState(this.getPos().offset(altarState.get(AltarBlock.FACING), 1).down()).getBlock().asItem().getDefaultStack());
        this.floorBlocks.set(1, world.getBlockState(this.getPos().offset(altarState.get(AltarBlock.FACING).rotateYClockwise(), 1).down()).getBlock().asItem().getDefaultStack());
        this.floorBlocks.set(2, world.getBlockState(this.getPos().offset(altarState.get(AltarBlock.FACING).getOpposite(), 1).down()).getBlock().asItem().getDefaultStack());
        this.floorBlocks.set(3, world.getBlockState(this.getPos().offset(altarState.get(AltarBlock.FACING).rotateYCounterclockwise(), 1).down()).getBlock().asItem().getDefaultStack());
    }

    public String getFloorPattern() {
        this.getFloorBlocks();

        StringBuilder out = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            out.append(this.floorBlocks.get(i).getTranslationKey()).append(";");
        }


        return out.toString();
    }

    public void dropItems(WorldAccess world, BlockState state) {
        if (world == null) {
            return;
        }

        World world1 = Objects.requireNonNull(world.getServer()).getWorld(World.OVERWORLD);
        assert world1 != null;

        if (!inventory.getFirst().isEmpty()) {

            ItemScatterer.spawn(world1, getPos().getX(), getPos().getY()+1.5, getPos().getZ(), inventory.getFirst());

            inventory.set(0, ItemStack.EMPTY);
        }

        dropAllPedestalItems(world1, state);

    }

    public boolean isValidItem(ItemStack stack) {
        switch (getFloorPattern().replace("waxed_","")) {
            case "block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;" -> {
                // Chilled
                // 1: slowness arrow
                // 3: ice, packed ice, blue ice (compound)
                return true;
            }
            case "block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;" -> {
                // Celestial Binding
                // 1: nether star
                // 2: end stone
                // 3: eye of ender
            }
            case "block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;" -> {
                // incindiary
                // 1: netherrack
                // 2: blaze_rod (optional
                // 3: blaze powder
            }
            case "block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;" -> {
                // Soulbound
                // 1: nether star
                // 2: soul soil
                // 3: eye of ender
            }
            case "block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;" -> {
                // Slashing
                // 1: strength potion I or II
                // 2: flint
                // 3: gold sword, iron sword, diamond sword (replace)
            }
            case "block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;" -> {
                // Weighted
                // 1/3: iron block
                // 2: anvil (increase damage stage instead of consume)
            }
            case "block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;" -> {
                // Conductive
                // 1: copper ingot
                // 2: lightning rod
                // 3: copper ingot
            }
            case "block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;" -> {
                // Inductive
                // 1: copper ingot
                // 2: redstone dust
                // 3: copper ingot
            }
            case "block.minecraft.emerald_block;block.minecraft.amethyst_block;block.minecraft.emerald_block;block.minecraft.amethyst_block;" -> {
                // Mending
                // 1: bottle of xp
                // 3: XP Tome (level determined by filled capacity)
            }
            case "block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone;block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone" -> {
                // Unbreaking
                // 1: iron block
                // 3: XP Tome (level determined by filled capacity)
            }
            case "block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;" -> {
                // Luck
                // 1: lapis
                // 2: redstone dust
                // 3: gold ingot
            }
            case "block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;" -> {
                // Padded
                // 1: wool for silence, stone for silktouch
                // 3: rabbit hide
            }
            case "block.minecraft.iron_block;block.minecraft.dried_kelp_block;block.minecraft.iron_block;block.minecraft.dried_kelp_block;" -> {
                // Resistive
                // 1: copper ingot
                // 2: dried kelp 1-4
                // 3: copper ingot
            }
            case "block.minecraft.iron_block;block.minecraft.smooth_stone;block.minecraft.iron_block;block.minecraft.smooth_stone;" -> {
                // Plated
                // 1: iron ingot
                // 2: heavy weighted pressure plate 1-4
                // 3: iron ingot
            }
            case "block.minecraft.iron_block;block.minecraft.white_wool;block.minecraft.iron_block;block.minecraft.white_wool;" -> {
                // Insulated
                // 1: magma block
                // 2: wool 1-4
                // 3: packed ice
            }
            case "block.minecraft.iron_block;block.minecraft.sponge;block.minecraft.iron_block;block.minecraft.sponge;" -> {
                // Elastic
                // 1: feather
                // 2: breeze rods 1-4
                // 3: phantom membrane
            }
            case "block.minecraft.iron_block;block.minecraft.magma_block;block.minecraft.iron_block;block.minecraft.magma_block;" -> {
                // Thorns
                // 1: cactus
                // 2: magma block 1-4
                // 3: berry bush
            }
            case "block.minecraft.iron_block;block.minecraft.slime_block;block.minecraft.iron_block;block.minecraft.slime_block;" -> {
                // Inertial
                // 1: phantom membrane
                // 2: nether star (1) slime block 0-3
                // 3: wind charge
            }
            case "block.minecraft.blue_ice;block.minecraft.packed_ice;block.minecraft.magma_block;block.minecraft.packed_ice;" -> {
                // Hydrodynamic
                // 1: gold pickaxe
                // 2: prismarine shard/crystal 1-3
                // 3: turtle scute
            }
            case "block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;" -> {
                // Swift Sneak
                // 1: echo shard
                // 2: XP Tome (level determined by filled capacity)
                // 3: echo shard
            }
            default -> {
                return false;
            }
        }

        return false;
    }


    public void dropPedestalItems(BlockState pedestalState, World world) {
        ItemStack item1 = getPedestalStack(0, pedestalState, world);
        ItemStack item2 = getPedestalStack(1, pedestalState, world);
        ItemStack item3 = getPedestalStack(2, pedestalState, world);
        ItemStack item4 = getPedestalStack(3, pedestalState, world);
        for (int i = 0; i < 4; i++) {
            setPedestalStack(i, ItemStack.EMPTY, pedestalState, world);
        }

        Vec3d pos = new Vec3d(getPos().getX()+0.5, getPos().getY(), getPos().getZ()+0.5).offset(pedestalState.get(PedestalBlock.ALTAR_DIRECTION).getOpposite(), 3);

        if (!item1.isEmpty()) {
            ItemScatterer.spawn(world, pos.x, pos.y+1.5, pos.z, item1);
        }
        if (!item2.isEmpty()) {
            ItemScatterer.spawn(world, pos.x, pos.y+1.5, pos.z, item2);
        }
        if (!item3.isEmpty()) {
            ItemScatterer.spawn(world, pos.x, pos.y+1.5, pos.z, item3);
        }
        if (!item4.isEmpty()) {
            ItemScatterer.spawn(world, pos.x, pos.y+1.5, pos.z, item4);
        }
    }

    private void dropAllPedestalItems(World world, BlockState state) {
        Vec3d leftPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).rotateYClockwise(), 3);
        Vec3d backPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).getOpposite(), 3);
        Vec3d rightPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
        for (int i = 1; i < 5; i++) {
            ItemStack item = getStack(i);
            if (!item.isEmpty()) {
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, leftPos.x, leftPos.y, leftPos.z, item);
            }
        }
        for (int i = 5; i < 9; i++) {
            ItemStack item = getStack(i);
            if (!item.isEmpty()) {
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, backPos.x, backPos.y, backPos.z, item);
            }
        }
        for (int i = 9; i < 13; i++) {
            ItemStack item = getStack(i);
            if (!item.isEmpty()) {
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, rightPos.x, rightPos.y, rightPos.z, item);
            }
        }
    }

    public void setPedestalStack(int slot, ItemStack stack, BlockState pedestalState, World world) {
        // Left pedestal gets slots 1, 2, 3, and 4
        // back pedestal gets slots 5, 6, 7, and 8
        // right pedestal gets slots 9, 10, 11, and 12
        // enchant item is slot 0
        // fuel/misc is 13-15

        Direction forward = world.getBlockState(this.getPos()).get(AltarBlock.FACING);

        if (forward.rotateYClockwise() == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the right direction
            switch (slot) {
                case 0 -> {
                    setStack(9, stack);
                }
                case 1 -> {
                    setStack(10, stack);
                }
                case 2 -> {
                    setStack(11, stack);
                }
                case 3 -> {
                    setStack(12, stack);
                }
                case 4 -> {
                    setStack(13, stack);
                }
            }
        } else if (forward == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the back direction
            switch (slot) {
                case 0 -> {
                    setStack(5, stack);
                }
                case 1 -> {
                    setStack(6, stack);
                }
                case 2 -> {
                    setStack(7, stack);
                }
                case 3 -> {
                    setStack(8, stack);
                }
                case 4 -> {
                    setStack(14, stack);
                }
            }
        } else if (forward.rotateYCounterclockwise() == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the left direction
            switch (slot) {
                case 0 -> {
                    setStack(1, stack);
                }
                case 1 -> {
                    setStack(2, stack);
                }
                case 2 -> {
                    setStack(3, stack);
                }
                case 3 -> {
                    setStack(4, stack);
                }
                case 4 -> {
                    setStack(15, stack);
                }
            }
        }

    }

    public ItemStack getPedestalStack(int slot, BlockState pedestalState, World world) {

        Direction forward = world.getBlockState(this.getPos()).get(AltarBlock.FACING);

        if (forward.rotateYClockwise() == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the right direction
            switch (slot) {
                case 0 -> {
                    return getStack(9);
                }
                case 1 -> {
                    return getStack(10);
                }
                case 2 -> {
                    return getStack(11);
                }
                case 3 -> {
                    return getStack(12);
                }
                case 4 -> {
                    return getStack(13);
                }
            }
        } else if (forward == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the back direction
            switch (slot) {
                case 0 -> {
                    return getStack(5);
                }
                case 1 -> {
                    return getStack(6);
                }
                case 2 -> {
                    return getStack(7);
                }
                case 3 -> {
                    return getStack(8);
                }
                case 4 -> {
                    return getStack(14);
                }
            }
        } else if (forward.rotateYCounterclockwise() == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the left direction
            switch (slot) {
                case 0 -> {
                    return getStack(1);
                }
                case 1 -> {
                    return getStack(2);
                }
                case 2 -> {
                    return getStack(3);
                }
                case 3 -> {
                    return getStack(4);
                }
                case 4 -> {
                    return getStack(15);
                }
            }
        }
        return ItemStack.EMPTY;

    }

    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        this.markDirty();
    }

    public void removeStack(int slot) {
        this.inventory.set(slot, ItemStack.EMPTY);
        this.markDirty();
    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {

        nbt.putInt("rotation", this.rotation);
        nbt.putBoolean("place_animation", this.place_animation);
        nbt.putInt("animation_phase", animation_phase);
        Inventories.writeNbt(nbt, inventory, wrapper);
        super.writeNbt(nbt, wrapper);

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        this.rotation = nbt.getInt("rotation");
        this.place_animation = nbt.getBoolean("place_animation");
        this.animation_phase = nbt.getInt("animation_phase");
        Inventories.readNbt(nbt, inventory, wrapper);

    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup wrapper) {
        return createNbt(wrapper);
    }

    @Override
    public void markDirty() {
        assert world != null;
        world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        super.markDirty();
    }

    private void particle(Vec3d pos, World world) {
        particle(pos, world, ParticleTypes.ENCHANT);
    }

    private void particle(Vec3d pos, World world, ParticleEffect particle) {

        if (world.isClient) {
            world.addParticle(particle, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }


    private void dualHelixAnimation(World world, BlockPos pos, int red, int green, int blue) {

        double y = pos.getY() + (Math.abs(this.rotation_height-100)/100.0);

        for (Vec3d point : Util.drawCircle(new Vec3d(pos.getX() + 0.5, y, pos.getZ() + 0.5), new Vec3d(0, 1, 0), 0.6, 2, this.rotation_tick)) {
            particle(point, world, new DustParticleEffect(new Vector3f(red, green, blue), 0.6f));
        }
        for (Vec3d point : Util.drawCircle(new Vec3d(pos.getX() + 0.5, y, pos.getZ() + 0.5), new Vec3d(0, 1, 0), 0.6, 2, -this.rotation_tick)) {
            particle(point, world, new DustParticleEffect(new Vector3f(red, green, blue), 0.6f));
        }

    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, AltarBlockEntity blockEntity) {

        if (blockState.get(AltarBlock.PLACE_ANIMATION)) {
            if (blockEntity.animation_stage == 0) {
                if (blockEntity.ticks == 0) {
                    blockEntity.animation_phase = 0;
                    world.playSound(null, blockPos, ModSounds.ALTAR_POWER_UP, SoundCategory.BLOCKS, 1, 0.9f);
                }
                blockEntity.ticks++;
                if (blockEntity.ticks > 10) {
                    blockEntity.animation_stage = 1;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            }
            else if (blockEntity.animation_stage == 1) {
                double radius = 2 * Util.easeOutBack(blockEntity.ticks / 100.0);
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), radius, 6, blockEntity.rotation)) {
                    blockEntity.particle(point, world);
                }
                blockEntity.ticks++;
                if (blockEntity.ticks > 100) {
                    blockEntity.animation_stage = 2;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                    world.playSound(null, blockPos, ModSounds.ALTAR_POWER_UP2, SoundCategory.BLOCKS, 1, 0.15f);
                }
            } else if (blockEntity.animation_stage == 2) {
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 2, 6, blockEntity.rotation)) {
                    blockEntity.particle(point, world);
                }
                blockEntity.ticks++;
                if (blockEntity.ticks > 20) {
                    blockEntity.animation_stage = 3;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }

            } else if (blockEntity.animation_stage == 3) {
                double radius = 2 + (Util.easeInBack(blockEntity.ticks / 100.0) * 20);
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), radius, 6, blockEntity.rotation)) {
                    blockEntity.particle(point, world);
                }
                blockEntity.ticks++;
                if (blockEntity.ticks > 100) {
                    blockEntity.animation_stage = 4;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            } else if (blockEntity.animation_stage == 4) {
                blockEntity.ticks++;
                if (blockEntity.ticks > 20) {
                    world.setBlockState(blockPos, blockState.with(AltarBlock.PLACE_ANIMATION, false));
                    blockEntity.animation_stage = 0;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            }

            blockEntity.rotation += 5;
            if (blockEntity.rotation > blockEntity.rotation_max) {
                blockEntity.rotation = 0;
            }
        } else if (blockState.get(AltarBlock.PEDESTALS) < 3) {
            blockEntity.rotation_tick+=5;
            blockEntity.rotation_height++;
            if (blockEntity.rotation_height >= 200) { blockEntity.rotation_height = 0; }
            if (blockEntity.rotation_tick >= 180) { blockEntity.rotation_tick = 0; }
            if (!blockState.get(AltarBlock.LEFT_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYClockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYClockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255, 0);
            }

            if (!blockState.get(AltarBlock.RIGHT_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255, 0);
            }

            if (!blockState.get(AltarBlock.BACK_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).getOpposite(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).getOpposite(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255, 0);
            }

        } else if (blockState.get(AltarBlock.PEDESTALS) == 3 && blockEntity.animation_phase == 0) {
            blockEntity.animation_phase = 1;
            blockEntity.markDirty();
        }


    }

}