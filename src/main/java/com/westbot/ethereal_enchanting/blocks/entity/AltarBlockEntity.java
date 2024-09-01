package com.westbot.ethereal_enchanting.blocks.entity;

import com.westbot.ethereal_enchanting.EtherealEnchanting;
import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.ModSounds;
import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.blocks.AltarBlock;
import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.blocks.PedestalBlock;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.entity.LivingEntityExtension;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import com.westbot.ethereal_enchanting.networking.ClearAltarIngredientsPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.potion.Potions;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.BiomeKeys;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.*;


public class AltarBlockEntity extends BlockEntity {

    private enum PedestalPlacement {
        LEFT,
        BACK,
        RIGHT
    }

    private enum PedestalState {
        OFF(0),
        GREEN1(1),
        GREEN2(2),
        GREEN3(3),
        RED1(4),
        RED2(5),
        RED3(6),
        BLUE1(7),
        BLUE2(8),
        BLUE3(9),
        YELLOW(10),
        WHITE1(11),
        WHITE2(12),
        WHITE3(13);


        private final int state;

        PedestalState(int state) {
            this.state = state;
        }

        public int getState() {
            return state;
        }

    }

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);
    private final DefaultedList<ItemStack> floorBlocks = DefaultedList.ofSize(4, ItemStack.EMPTY);

    public String availableEnchant = "";
    public int availableLevel = 0;
    private int recipeCheck = 0;
    private final Random random;

    public static final int LEFT_0 = 1;
    public static final int LEFT_1 = 2;
    public static final int LEFT_2 = 3;
    public static final int LEFT_3 = 4;
    public static final int BACK_0 = 5;
    public static final int BACK_1 = 6;
    public static final int BACK_2 = 7;
    public static final int BACK_3 = 8;
    public static final int RIGHT_0 = 9;
    public static final int RIGHT_1 = 10;
    public static final int RIGHT_2 = 11;
    public static final int RIGHT_3 = 12;


    private int rotation = 0;
    private final int rotation_max = 360/6;
    private boolean place_animation = true;
    private int animation_stage = 0;

    private int rotation_tick = 0;
    private int rotation_height = 0;

    private int animation_phase = 0;

    private int ticks = -1;
    private final Box entityBox;

    private static final Map<String, List<Integer>> valid_enchants = new HashMap<>() {{
        put("celestial_binding", List.of(1));
        put("soulbound", List.of(1));
        put("mending", List.of(1,2,3));
        put("unbreaking", List.of(1,2,3));
        put("chilled", List.of(1,2,3));
        put("incendiary", List.of(1,2));
        put("slashing", List.of(1,2,3,4,5,6));
        put("weighted", List.of(-5,-4,-3,-2,-1));
        put("conductive", List.of(1,2));
        put("inductive", List.of(1,2));
        put("resistive", List.of(1,2,3,4));
        put("luck", List.of(1,2,3));
        put("padded", List.of(1));
        put("plated", List.of(1,2,3,4));
        put("insulated", List.of(1,2,3,4));
        put("elastic", List.of(1,2,3,4));
        put("thorns", List.of(1,2,3,4));
        put("inertial", List.of(1,2,3,4));
        put("hydrodynamic", List.of(1,2,3));
        put("swift_sneak", List.of(1,2,3));
        put("soul_speed", List.of(1,2,3));
        put("cruel_and_unusual", List.of(1,2,3,-6));
    }};

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, pos, state);
        random = Random.create();
        entityBox = new Box(
            getPos().toBottomCenterPos().add(new Vec3d(-5, -5, -5)),
            getPos().toBottomCenterPos().add(new Vec3d(5, 5, 5))
        );
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
        if (!altarState.isOf(ModBlocks.ALTAR_BLOCK)) return;

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

    private void setPedestalState(PedestalPlacement placement, PedestalState state) {
        World world = this.getWorld();

        if (world == null) {
            return;
        }

        BlockState altar = world.getBlockState(this.getPos());
        switch (placement) {
            case LEFT -> {
                BlockPos pos = this.getPos().offset(altar.get(AltarBlock.FACING).rotateYClockwise(), 3);
                BlockState pedestalState = world.getBlockState(pos);
                if (!pedestalState.isOf(ModBlocks.PEDESTAL_BLOCK)) return;
                world.setBlockState(pos, pedestalState.with(PedestalBlock.STATE, state.getState()), 3);
            }
            case BACK -> {
                BlockPos pos = this.getPos().offset(altar.get(AltarBlock.FACING).getOpposite(), 3);
                BlockState pedestalState = world.getBlockState(pos);
                if (!pedestalState.isOf(ModBlocks.PEDESTAL_BLOCK)) return;
                world.setBlockState(pos, pedestalState.with(PedestalBlock.STATE, state.getState()), 3);
            }
            case RIGHT -> {
                BlockPos pos = this.getPos().offset(altar.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
                BlockState pedestalState = world.getBlockState(pos);
                if (!pedestalState.isOf(ModBlocks.PEDESTAL_BLOCK)) return;
                world.setBlockState(pos, pedestalState.with(PedestalBlock.STATE, state.getState()), 3);
            }
        }


    }

    public void verifyEnchant() {
        World world = this.getWorld();

        if (world == null) {
            return;
        }

        this.recipeCheck = 0;
        this.availableEnchant = "";
        int lvl = 0;
        String enchant = "";

        switch (getFloorPattern().replace("waxed_","")) {
            case "block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;" -> {
                // Chilled
                // 1: slowness arrow
                // 3: ice, packed ice, blue ice (compound)
                ItemStack arrow1 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.SLOWNESS);
                ItemStack arrow2 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.STRONG_SLOWNESS);
                ItemStack arrow3 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.LONG_SLOWNESS);
                dropSlot(2,3,4, 5,6,7,8, 12);

                ItemStack left1 = getStack(LEFT_0);

                if (!left1.isEmpty()) {
                    if (!(ItemStack.areItemsAndComponentsEqual(arrow1, left1) || ItemStack.areItemsAndComponentsEqual(arrow2, left1) || ItemStack.areItemsAndComponentsEqual(arrow3, left1))) {
                        dropSlot(LEFT_0);
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    } else {
                        recipeCheck++;
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    }
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                }

                setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                if (getStack(RIGHT_0).isOf(Items.ICE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.GREEN1);
                    lvl = 1;
                } else {
                    dropSlot(RIGHT_0, RIGHT_1, RIGHT_2);
                }

                if (getStack(RIGHT_1).isOf(Items.PACKED_ICE)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.GREEN2);
                    lvl = 2;
                } else {
                    dropSlot(RIGHT_1, RIGHT_2);
                }

                if (getStack(RIGHT_2).isOf(Items.BLUE_ICE)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.GREEN3);
                    lvl = 3;
                } else {
                    dropSlot(RIGHT_2);
                }

                setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);

                if (recipeCheck == 2) {
                    this.availableEnchant = "chilled";
                    this.availableLevel = lvl;
                }



            }
            case "block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;" -> {
                // Celestial Binding
                // 1: nether star
                // 2: end stone
                // 3: eye of ender

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.NETHER_STAR)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(Items.END_STONE)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.ENDER_EYE)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "celestial_binding";
                    this.availableLevel = 1;
                }


            }
            case "block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;" -> {
                // Incendiary
                // 1: netherrack
                // 2: blaze_rod (optional)
                // 3: blaze powder

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.NETHERRACK)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }


                if (getStack(BACK_0).isOf(Items.BLAZE_ROD)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.YELLOW);
                    lvl = 2;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                    lvl = 1;
                }

                if (getStack(RIGHT_0).isOf(Items.BLAZE_POWDER)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 2) {
                    this.availableEnchant = "incendiary";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;" -> {
                // Soulbound
                // 1: nether star
                // 2: lost soul
                // 3: eye of ender

                if (getStack(LEFT_0).isOf(Items.NETHER_STAR)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(ModItems.LOST_SOUL)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.ENDER_EYE)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "soulbound";
                    this.availableLevel = 1;
                }

            }
            case "block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;" -> {
                // Slashing
                // 1: strength potion I or II
                // 2: flint
                // 3: gold sword, iron sword, diamond sword (replace)
                ItemStack strength1 = PotionContentsComponent.createStack(Items.POTION, Potions.STRENGTH);
                ItemStack strength2 = PotionContentsComponent.createStack(Items.POTION, Potions.STRONG_STRENGTH);
                ItemStack strength3 = PotionContentsComponent.createStack(Items.POTION, Potions.LONG_STRENGTH);
                ItemStack strength4 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRENGTH);
                ItemStack strength5 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRONG_STRENGTH);
                ItemStack strength6 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.LONG_STRENGTH);
                ItemStack strength7 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.STRENGTH);
                ItemStack strength8 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.STRONG_STRENGTH);
                ItemStack strength9 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.LONG_STRENGTH);

                ItemStack slot1 = getStack(LEFT_0);

                if (
                    ItemStack.areItemsAndComponentsEqual(slot1, strength2) ||
                        ItemStack.areItemsAndComponentsEqual(slot1, strength3) ||

                        ItemStack.areItemsAndComponentsEqual(slot1, strength5) ||
                        ItemStack.areItemsAndComponentsEqual(slot1, strength6) ||

                        ItemStack.areItemsAndComponentsEqual(slot1, strength8) ||
                        ItemStack.areItemsAndComponentsEqual(slot1, strength9)
                ) {
                    recipeCheck++;
                    lvl = 1;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else if (
                    ItemStack.areItemsAndComponentsEqual(slot1, strength1) ||
                        ItemStack.areItemsAndComponentsEqual(slot1, strength4) ||
                        ItemStack.areItemsAndComponentsEqual(slot1, strength7)
                ) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE2);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(Items.FLINT)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.GOLDEN_SWORD)){
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE1);
                    recipeCheck++;
                    lvl += 1;
                } else if (getStack(RIGHT_0).isOf(Items.IRON_SWORD)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE2);
                    recipeCheck++;
                    lvl += 3;
                } else if (getStack(RIGHT_0).isOf(Items.DIAMOND_SWORD)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                    lvl += 5;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }


                if (recipeCheck == 3) {
                    this.availableEnchant = "slashing";
                    this.availableLevel = lvl;
                }


            }
            case "block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;" -> {
                // Weighted
                // 1/3: iron block
                // 2: anvil (increase damage stage instead of consume)

                dropSlot(6,7,8);

                if (getStack(BACK_0).isOf(Items.ANVIL) || getStack(BACK_0).isOf(Items.DAMAGED_ANVIL) || getStack(BACK_0).isOf(Items.CHIPPED_ANVIL)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }


                if (getStack(LEFT_0).isOf(Items.IRON_BLOCK)) {
                    if (getStack(RIGHT_0).isOf(Items.IRON_BLOCK)) { // 1 - 1
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.RED1);
                        setPedestalState(PedestalPlacement.RIGHT, PedestalState.BLUE1);
                        recipeCheck++;
                        lvl = -3;
                        dropSlot(2,3,4, 10,11,12);
                    } else if (getStack(LEFT_1).isOf(Items.IRON_BLOCK)) { // 2 - 0
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.RED3);
                        setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                        recipeCheck++;
                        lvl = -5;
                        dropSlot(3,4, 9,10,11,12);
                    } else { // 1 - 0
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.RED2);
                        setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                        recipeCheck++;
                        lvl = -4;
                        dropSlot(2,3,4, 9,10,11,12);
                    }
                } else if (getStack(RIGHT_0).isOf(Items.IRON_BLOCK)) {
                    if (getStack(RIGHT_1).isOf(Items.IRON_BLOCK)) { // 0 - 2
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                        setPedestalState(PedestalPlacement.RIGHT, PedestalState.BLUE3);
                        recipeCheck++;
                        lvl = -1;
                        dropSlot(1,2,3,4, 11,12);
                    } else { // 0 - 1
                        setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                        setPedestalState(PedestalPlacement.RIGHT, PedestalState.BLUE2);
                        recipeCheck++;
                        lvl = -2;
                        dropSlot(1,2,3,4, 10,11,12);
                    }
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(1,2,3,4, 9,10,11,12);
                }

                if (recipeCheck == 2) {
                    this.availableEnchant = "weighted";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;" -> {
                // Conductive
                // 1: copper ingot
                // 2: lightning rod
                // 3: copper ingot

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.COPPER_INGOT)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(Items.LIGHTNING_ROD)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.COPPER_INGOT)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "conductive";
                    this.availableLevel = 1;

                    if (getWorld().isThundering()) {
                        this.availableLevel = 3;
                    } else if (getWorld().isRaining()) {
                        this.availableLevel = 2;
                    }
                }

            }
            case "block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;" -> {
                // Inductive
                // 1: copper ingot
                // 2: redstone dust
                // 3: copper ingot

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.COPPER_INGOT)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(Items.REDSTONE)) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.COPPER_INGOT)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "inductive";
                    this.availableLevel = 1;

                    if (getWorld().isThundering()) {
                        this.availableLevel = 3;
                    } else if (getWorld().isRaining()) {
                        this.availableLevel = 2;
                    }
                }

            }
            case "block.minecraft.emerald_block;block.minecraft.amethyst_block;block.minecraft.emerald_block;block.minecraft.amethyst_block;" -> {
                // Mending
                // 1: bottle of xp
                // 3: XP Tome (level determined by filled capacity)

                dropSlot(2,3,4, 5,6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.EXPERIENCE_BOTTLE)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(ModItems.XP_TOME)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;

                    ItemStack stack = getStack(RIGHT_0);
                    if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL30.getPoints()) {
                        lvl = 3;
                    } else if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL20.getPoints()) {
                        lvl = 2;
                    } else if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL10.getPoints()) {
                        lvl = 1;
                    }

                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 2) {
                    this.availableEnchant = "mending";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone;block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone" -> {
                // Unbreaking
                // 1: iron block
                // 3: XP Tome (level determined by filled capacity)

                dropSlot(2,3,4, 5,6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.IRON_BLOCK)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(ModItems.XP_TOME)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;

                    ItemStack stack = getStack(RIGHT_0);
                    if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL30.getPoints()) {
                        lvl = 3;
                    } else if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL20.getPoints()) {
                        lvl = 2;
                    } else if (XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL10.getPoints()) {
                        lvl = 1;
                    }

                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 2) {
                    this.availableEnchant = "unbreaking";
                    this.availableLevel = lvl;
                }
            }
            case "block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;" -> {
                // Luck
                // 1: lapis
                // 2: redstone dust
                // 3: gold ingot

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.LAPIS_LAZULI)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                    lvl = 1;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(BACK_0).isOf(Items.REDSTONE) && lvl == 1) {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                    lvl = 2;

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (getStack(RIGHT_0).isOf(Items.GOLD_INGOT) && lvl == 2) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    lvl = 3;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 1) {
                    this.availableEnchant = "luck";
                    this.availableLevel = lvl;
                }


            }
            case "block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;" -> {
                // Padded
                // 1: wool for silence, stone for silktouch
                // 3: rabbit hide

                dropSlot(2,3,4, 5,6,7,8, 10,11,12);

                if (getStack(LEFT_0).isIn(ItemTags.WOOL)) {
                    enchant = "silence";
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else if (getStack(LEFT_0).isOf(Items.STONE)) {
                    enchant = "silktouch";
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.RABBIT_HIDE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (recipeCheck == 2) {
                    this.availableEnchant = enchant;
                    this.availableLevel = 1;
                }


            }
            case "block.minecraft.iron_block;block.minecraft.dried_kelp_block;block.minecraft.iron_block;block.minecraft.dried_kelp_block;" -> {
                // Resistive
                // 1: copper ingot
                // 2: dried kelp 1-4
                // 3: copper ingot

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.COPPER_INGOT)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.COPPER_INGOT)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.DRIED_KELP)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.DRIED_KELP)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.DRIED_KELP)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isOf(Items.DRIED_KELP)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "resistive";
                    this.availableLevel = lvl;
                }


            }
            case "block.minecraft.iron_block;block.minecraft.smooth_stone;block.minecraft.iron_block;block.minecraft.smooth_stone;" -> {
                // Plated
                // 1: iron ingot
                // 2: heavy weighted pressure plate 1-4
                // 3: iron ingot

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.IRON_INGOT)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.IRON_INGOT)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "plated";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.iron_block;block.minecraft.white_wool;block.minecraft.iron_block;block.minecraft.white_wool;" -> {
                // Insulated
                // 1: magma block
                // 2: wool 1-4
                // 3: packed ice

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.MAGMA_BLOCK)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.PACKED_ICE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isIn(ItemTags.WOOL)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isIn(ItemTags.WOOL)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isIn(ItemTags.WOOL)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isIn(ItemTags.WOOL)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "insulated";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.iron_block;block.minecraft.sponge;block.minecraft.iron_block;block.minecraft.sponge;" -> {
                // Elastic
                // 1: feather
                // 2: breeze rods 1-4
                // 3: phantom membrane

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.FEATHER)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.PHANTOM_MEMBRANE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.BREEZE_ROD)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.BREEZE_ROD)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.BREEZE_ROD)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isOf(Items.BREEZE_ROD)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "elastic";
                    this.availableLevel = lvl;
                }
            }
            case "block.minecraft.iron_block;block.minecraft.magma_block;block.minecraft.iron_block;block.minecraft.magma_block;" -> {
                // Thorns
                // 1: cactus
                // 2: magma block 1-4
                // 3: berry bush

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.CACTUS)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.SWEET_BERRIES)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.MAGMA_BLOCK)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.MAGMA_BLOCK)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.MAGMA_BLOCK)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isOf(Items.MAGMA_BLOCK)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "thorns";
                    this.availableLevel = lvl;
                }
            }
            case "block.minecraft.iron_block;block.minecraft.slime_block;block.minecraft.iron_block;block.minecraft.slime_block;" -> {
                // Inertial
                // 1: phantom membrane
                // 2: nether star (1) slime block 0-3
                // 3: wind charge

                dropSlot(2,3,4, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.PHANTOM_MEMBRANE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.WIND_CHARGE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.NETHER_STAR)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.SLIME_BLOCK)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.SLIME_BLOCK)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                            if (getStack(BACK_3).isOf(Items.SLIME_BLOCK)) {
                                lvl = 4;
                                setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                            } else {
                                dropSlot(8);
                            }

                        } else {
                            dropSlot(7,8);
                        }

                    } else {
                        dropSlot(6,7,8);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7,8);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "inertial";
                    this.availableLevel = lvl;
                }
            }
            case "block.minecraft.blue_ice;block.minecraft.packed_ice;block.minecraft.magma_block;block.minecraft.packed_ice;" -> {
                // Hydrodynamic
                // 1: gold pickaxe
                // 2: prismarine shard/crystal 1-3
                // 3: turtle scute

                dropSlot(2,3,4, 8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.GOLDEN_PICKAXE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(LEFT_0);
                }

                if (getStack(RIGHT_0).isOf(Items.TURTLE_SCUTE)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(RIGHT_0);
                }

                if (getStack(BACK_0).isOf(Items.PRISMARINE_SHARD) || getStack(BACK_0).isOf(Items.PRISMARINE_CRYSTALS)) {
                    lvl = 1;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN1);
                    recipeCheck++;

                    if (getStack(BACK_1).isOf(Items.PRISMARINE_SHARD) || getStack(BACK_1).isOf(Items.PRISMARINE_CRYSTALS)) {
                        lvl = 2;
                        setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN2);

                        if (getStack(BACK_2).isOf(Items.PRISMARINE_SHARD) || getStack(BACK_2).isOf(Items.PRISMARINE_CRYSTALS)) {
                            lvl = 3;
                            setPedestalState(PedestalPlacement.BACK, PedestalState.GREEN3);

                        } else {
                            dropSlot(7);
                        }

                    } else {
                        dropSlot(6,7);
                    }

                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(5,6,7);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "inertial";
                    this.availableLevel = lvl;
                }
            }
            case "block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;" -> {
                // Swift Sneak
                // 1: echo shard
                // 2: XP Tome (level determined by filled capacity)
                // 3: echo shard

                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.ECHO_SHARD)) {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                    dropSlot(1);
                }

                if (getStack(RIGHT_0).isOf(Items.ECHO_SHARD)) {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                    dropSlot(1);
                }

                if (getStack(BACK_0).isOf(ModItems.XP_TOME)) {
                    int xp = XPTomeItem.getXP(getStack(BACK_0));
                    if (xp >= Util.XPLevel.LEVEL30.getPoints()) {
                        lvl = 3;
                    } else if (xp >= Util.XPLevel.LEVEL20.getPoints()) {
                        lvl = 2;
                    } else if (xp >= Util.XPLevel.LEVEL10.getPoints()) {
                        lvl = 1;
                    }
                    setPedestalState(PedestalPlacement.BACK, lvl == 3 ? PedestalState.WHITE3 : lvl == 2 ? PedestalState.WHITE2 : PedestalState.WHITE1);
                    recipeCheck++;
                } else {
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                    dropSlot(BACK_0);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "swift_sneak";
                    this.availableLevel = lvl;
                }

            }
            case "block.minecraft.soul_sand;block.minecraft.soul_sand;block.minecraft.soul_sand;block.minecraft.soul_sand;" -> {
                // Soul speed
                // 1: wither skull
                // 2: lost soul
                // 3: soul sand
                dropSlot(2,3,4, 6,7,8, 10,11,12);

                if (getStack(LEFT_0).isOf(Items.WITHER_SKELETON_SKULL)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.WHITE3);
                } else {
                    dropSlot(LEFT_0);
                    setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                }

                if (getStack(BACK_0).isOf(ModItems.LOST_SOUL)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.BACK, PedestalState.WHITE3);
                } else {
                    dropSlot(BACK_0);
                    setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                }

                if (getStack(RIGHT_0).isOf(Items.SOUL_SAND) || getStack(RIGHT_0).isOf(Items.SOUL_SOIL)) {
                    recipeCheck++;
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.WHITE3);
                } else {
                    dropSlot(RIGHT_0);
                    setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
                }

                if (recipeCheck == 3) {
                    this.availableEnchant = "soul_speed";
                    if (world.getBiome(getPos()) == BiomeKeys.SOUL_SAND_VALLEY) {
                        this.availableLevel = 3;
                    } else if (world.getBiome(getPos()).isIn(BiomeTags.IS_NETHER)) {
                        this.availableLevel = 2;
                    } else {
                        this.availableLevel = 1;
                    }
                }

            }
            default -> {
                dropSlot(1,2,3,4,5,6,7,8,9,10,11,12);
                setPedestalState(PedestalPlacement.LEFT, PedestalState.OFF);
                setPedestalState(PedestalPlacement.BACK, PedestalState.OFF);
                setPedestalState(PedestalPlacement.RIGHT, PedestalState.OFF);
            }
        }

        if (!getStack(0).isEmpty()) {
            if (!isItemEnchantable(getStack(0), availableEnchant, availableLevel)) {
                dropSlot(0);
            }
        }

        this.markDirty();

    }

    private static final List<Item> enchantable_items = List.of(
        // Boats
        Items.OAK_BOAT,
        Items.OAK_CHEST_BOAT,
        Items.BIRCH_BOAT,
        Items.BIRCH_CHEST_BOAT,
        Items.DARK_OAK_BOAT,
        Items.DARK_OAK_CHEST_BOAT,
        Items.ACACIA_BOAT,
        Items.ACACIA_CHEST_BOAT,
        Items.SPRUCE_BOAT,
        Items.SPRUCE_CHEST_BOAT,
        Items.JUNGLE_BOAT,
        Items.JUNGLE_CHEST_BOAT,
        Items.BAMBOO_RAFT,
        Items.BAMBOO_CHEST_RAFT,

        // Items that can't be enchanted via enchanting table in vanilla
        Items.SHEARS,
        Items.FLINT_AND_STEEL,
        Items.SHIELD,
        Items.CARROT_ON_A_STICK,
        Items.WARPED_FUNGUS_ON_A_STICK,
        Items.BRUSH,
        Items.ELYTRA,
        Items.CARVED_PUMPKIN,
        Items.CREEPER_HEAD,
        Items.DRAGON_HEAD,
        Items.PIGLIN_HEAD,
        Items.PLAYER_HEAD,
        Items.ZOMBIE_HEAD
    );

    private static final List<String> GLOBAL_SUPPORT = List.of(
            "mending", "unbreaking", "celestial_binding",
            "soulbound", "cruel_and_unusual", "hydrodynamic"
    );
    private static final List<String> SWORD_SUPPORT = List.of(
            "chilled", "incendiary", "slashing",
            "weighted", "conductive", "inductive",
            "luck"
    );
    private static final List<String> AXE_SUPPORT = List.of(
            "weighted", "conductive", "inductive",
            "padded"
    );
    private static final List<String> PICKAXE_SUPPORT = List.of(
            "weighted", "padded",
            "luck", "incendiary"
    );
    private static final List<String> SHOVEL_SUPPORT = List.of(
            "weighted", "padded", "luck"
    );
    private static final List<String> HOE_SUPPORT = List.of(
            "weighted", "padded", "luck"
    );
    private static final List<String> ARMOR_SUPPORT = List.of(
            "resistive", "plated", "insulated",
            "elastic", "thorns", "inertial"
    );
    private static final List<String> HELMET_SUPPORT = List.of(

    );
    private static final List<String> CHESTPLATE_SUPPORT = List.of(
            "chilled", "incendiary"
    );
    private static final List<String> LEGGINGS_SUPPORT = List.of(
            "swift_sneak", "chilled", "incendiary"
    );
    private static final List<String> BOOTS_SUPPORT = List.of(
            "soul_speed"
    );

    private static final List<String> PROTECTION_TYPES = List.of(
        "resistive", "plated", "insulated",
        "elastic", "thorns", "inertial"
    );

    public boolean checkProtection(ItemStack stack, String enchant) {
        if (!PROTECTION_TYPES.contains(enchant)) return true;

        List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);

        if (enchants == null) return true;

        int prot_count = 0;

        for (EtherealEnchantComponent e : enchants) {
            if (PROTECTION_TYPES.contains(e.enchant())) {
                prot_count++;
            }
        }

        return (prot_count < 4);

    }

    public boolean itemSupportsEnchant(ItemStack stack, String enchant) {
        if (GLOBAL_SUPPORT.contains(enchant)) {
            return true;
        }
        else if (stack.isIn(ItemTags.SWORDS)) {
            return SWORD_SUPPORT.contains(enchant);
        }
        else if (stack.isIn(ItemTags.AXES)) {
            return AXE_SUPPORT.contains(enchant);
        }
        else if (stack.isIn(ItemTags.PICKAXES)) {
            return PICKAXE_SUPPORT.contains(enchant);
        }
        else if (stack.isIn(ItemTags.HOES)) {
            return HOE_SUPPORT.contains(enchant);
        }
        else if (stack.isIn(ItemTags.SHOVELS)) {
            return SHOVEL_SUPPORT.contains(enchant);
        }

        else if (stack.isIn(ItemTags.HEAD_ARMOR)) {
            return checkProtection(stack, enchant) && (ARMOR_SUPPORT.contains(enchant) || HELMET_SUPPORT.contains(enchant));
        }
        else if (stack.isIn(ItemTags.CHEST_ARMOR)) {
            return checkProtection(stack, enchant) && (ARMOR_SUPPORT.contains(enchant) || CHESTPLATE_SUPPORT.contains(enchant));
        }
        else if (stack.isIn(ItemTags.LEG_ARMOR)) {
            return checkProtection(stack, enchant) && (ARMOR_SUPPORT.contains(enchant) || LEGGINGS_SUPPORT.contains(enchant));
        }
        else if (stack.isIn(ItemTags.FOOT_ARMOR)) {
            return checkProtection(stack, enchant) && (ARMOR_SUPPORT.contains(enchant) || BOOTS_SUPPORT.contains(enchant));
        }

        return false;
    }

    public boolean isItemEnchantable(ItemStack stack) {
        return isItemEnchantable(stack, availableEnchant, availableLevel);
    }

    public boolean isItemEnchantable(ItemStack stack, @Nullable String enchant, int level) {
        if (stack.isEnchantable() || enchantable_items.contains(stack.getItem())) {
            if (Objects.equals(enchant, "")) return true;

            if (valid_enchants.containsKey(enchant) && valid_enchants.get(enchant).contains(level)) {
                List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
                if (enchants == null) return true;
                for (EtherealEnchantComponent e : enchants) {
                    if (Objects.equals(e.enchant(), enchant)) {
                        return (e.level() > 0 && e.level() < level) || level < 0;
                    }
                }
                return itemSupportsEnchant(stack, enchant);
            }
        }

        return false;
    }

    public boolean isValidItem(ItemStack stack, BlockState pedestalState) {
        World world = this.getWorld();

        if (world == null) {
            return false;
        }

        BlockState altar = world.getBlockState(this.getPos());

        PedestalPlacement placement = PedestalPlacement.BACK;

        if (pedestalState.get(PedestalBlock.ALTAR_DIRECTION) == altar.get(AltarBlock.FACING).rotateYCounterclockwise()) {
            placement = PedestalPlacement.LEFT;
        } else if (pedestalState.get(PedestalBlock.ALTAR_DIRECTION) == altar.get(AltarBlock.FACING).rotateYClockwise()) {
            placement = PedestalPlacement.RIGHT;
        }


        switch (getFloorPattern().replace("waxed_","")) {
            case "block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;block.minecraft.blue_ice;" -> {
                // Chilled
                // 1: slowness arrow
                // 3: ice, packed ice, blue ice (compound)
                if (placement == PedestalPlacement.LEFT) {
                    ItemStack arrow1 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.SLOWNESS);
                    ItemStack arrow2 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.STRONG_SLOWNESS);
                    ItemStack arrow3 = PotionContentsComponent.createStack(Items.TIPPED_ARROW, Potions.LONG_SLOWNESS);

                    return getStack(LEFT_0).isEmpty() && (ItemStack.areItemsAndComponentsEqual(arrow1, stack) || ItemStack.areItemsAndComponentsEqual(arrow2, stack) || ItemStack.areItemsAndComponentsEqual(arrow3, stack));
                }
                else if (placement == PedestalPlacement.RIGHT) {
                    if (getStack(RIGHT_0).isEmpty()) {
                        return stack.isOf(Items.ICE);
                    } else if (getStack(RIGHT_1).isEmpty()) {
                        return stack.isOf(Items.PACKED_ICE);
                    } else if (getStack(RIGHT_2).isEmpty()) {
                        return stack.isOf(Items.BLUE_ICE);
                    }
                }

                return false;
            }
            case "block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;block.minecraft.end_stone;" -> {
                // Celestial Binding
                // 1: nether star
                // 2: end stone
                // 3: eye of ender

                if (placement == PedestalPlacement.LEFT) {
                    return getStack(LEFT_0).isEmpty() && stack.isOf(Items.NETHER_STAR);
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.END_STONE);
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && stack.isOf(Items.ENDER_EYE);
                }
            }
            case "block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;block.minecraft.magma_block;" -> {
                // Incendiary
                // 1: netherrack
                // 2: blaze_rod (optional)
                // 3: blaze powder

                if (placement == PedestalPlacement.LEFT) {
                    return getStack(LEFT_0).isEmpty() && stack.isOf(Items.NETHERRACK);
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.BLAZE_ROD);
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && stack.isOf(Items.BLAZE_POWDER);
                }

            }
            case "block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;block.minecraft.soul_soil;" -> {
                // Soulbound
                // 1: nether star
                // 2: soul soil
                // 3: eye of ender

                if (placement == PedestalPlacement.LEFT) {
                    return getStack(LEFT_0).isEmpty() && stack.isOf(Items.NETHER_STAR);
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(ModItems.LOST_SOUL);
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && stack.isOf(Items.ENDER_EYE);
                }

            }
            case "block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;block.minecraft.bamboo_mosaic;" -> {
                // Slashing
                // 1: strength potion I or II
                // 2: flint
                // 3: gold sword, iron sword, diamond sword (replace)
                ItemStack strength1 = PotionContentsComponent.createStack(Items.POTION, Potions.STRENGTH);
                ItemStack strength2 = PotionContentsComponent.createStack(Items.POTION, Potions.STRONG_STRENGTH);
                ItemStack strength3 = PotionContentsComponent.createStack(Items.POTION, Potions.LONG_STRENGTH);
                ItemStack strength4 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRENGTH);
                ItemStack strength5 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.STRONG_STRENGTH);
                ItemStack strength6 = PotionContentsComponent.createStack(Items.SPLASH_POTION, Potions.LONG_STRENGTH);
                ItemStack strength7 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.STRENGTH);
                ItemStack strength8 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.STRONG_STRENGTH);
                ItemStack strength9 = PotionContentsComponent.createStack(Items.LINGERING_POTION, Potions.LONG_STRENGTH);

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty()) && (
                            ItemStack.areItemsAndComponentsEqual(strength1, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength2, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength3, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength4, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength5, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength6, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength7, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength8, stack) ||
                            ItemStack.areItemsAndComponentsEqual(strength9, stack)
                        );
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.FLINT);
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && (
                            stack.isOf(Items.GOLDEN_SWORD) || stack.isOf(Items.IRON_SWORD) || stack.isOf(Items.DIAMOND_SWORD)
                        );
                }

            }
            case "block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;block.minecraft.iron_block;" -> {
                // Weighted
                // 1/3: iron block
                // 2: anvil (increase damage stage instead of consume)

                if (placement == PedestalPlacement.LEFT) {
                    return stack.isOf(Items.IRON_BLOCK) && (
                            (getStack(LEFT_0).isEmpty() && getStack(RIGHT_1).isEmpty()) ||
                            (getStack(LEFT_1).isEmpty() && getStack(RIGHT_0).isEmpty())
                        );
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && (
                        stack.isOf(Items.ANVIL) ||
                        stack.isOf(Items.CHIPPED_ANVIL) ||
                        stack.isOf(Items.DAMAGED_ANVIL)
                    );
                }
                else {
                    return stack.isOf(Items.IRON_BLOCK) && (
                            (getStack(RIGHT_0).isEmpty() && getStack(LEFT_1).isEmpty()) ||
                            (getStack(RIGHT_1).isEmpty() && getStack(LEFT_0).isEmpty())
                    );
                }

            }
            case "block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;block.minecraft.copper_block;" -> {
                // Conductive
                // 1: copper ingot
                // 2: lightning rod
                // 3: copper ingot

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.LIGHTNING_ROD);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }

            }
            case "block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;block.minecraft.chiseled_copper;" -> {
                // Inductive
                // 1: copper ingot
                // 2: redstone dust
                // 3: copper ingot

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.REDSTONE);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }

            }
            case "block.minecraft.emerald_block;block.minecraft.amethyst_block;block.minecraft.emerald_block;block.minecraft.amethyst_block;" -> {
                // Mending
                // 1: bottle of xp
                // 3: XP Tome (level determined by filled capacity)

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.EXPERIENCE_BOTTLE));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return false;
                }
                else {
                    // 10, 21, 32
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(ModItems.XP_TOME) && XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL10.getPoints());
                }

            }
            case "block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone;block.minecraft.emerald_block;block.minecraft.chiseled_polished_blackstone;" -> {
                // Unbreaking
                // 1: iron block
                // 3: XP Tome (level determined by filled capacity)

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.IRON_BLOCK));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return false;
                }
                else {
                    // 10, 21, 32
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(ModItems.XP_TOME) && XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL10.getPoints());
                }
            }
            case "block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;block.minecraft.moss_block;" -> {
                // Luck
                // 1: lapis
                // 2: redstone dust
                // 3: gold ingot

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.LAPIS_LAZULI));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(Items.REDSTONE) && getStack(LEFT_0).isOf(Items.LAPIS_LAZULI);
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && stack.isOf(Items.GOLD_INGOT) && getStack(LEFT_0).isOf(Items.LAPIS_LAZULI) && getStack(BACK_0).isOf(Items.REDSTONE);
                }

            }
            case "block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;block.minecraft.white_wool;" -> {
                // Padded
                // 1: wool for silence, stone for silktouch
                // 3: rabbit hide

                if (placement == PedestalPlacement.LEFT) {
                    return getStack(LEFT_0).isEmpty() && (stack.isOf(Items.WHITE_WOOL) || stack.isOf(Items.STONE));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return false;
                }
                else {
                    return getStack(RIGHT_0).isEmpty() && stack.isOf(Items.RABBIT_HIDE);
                }
            }
            case "block.minecraft.iron_block;block.minecraft.dried_kelp_block;block.minecraft.iron_block;block.minecraft.dried_kelp_block;" -> {
                // Resistive
                // 1: copper ingot
                // 2: dried kelp 1-4
                // 3: copper ingot

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_3).isEmpty() && stack.isOf(Items.DRIED_KELP);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.COPPER_INGOT));
                }

            }
            case "block.minecraft.iron_block;block.minecraft.smooth_stone;block.minecraft.iron_block;block.minecraft.smooth_stone;" -> {
                // Plated
                // 1: iron ingot
                // 2: heavy weighted pressure plate 1-4
                // 3: iron ingot

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.IRON_INGOT));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_3).isEmpty() && stack.isOf(Items.HEAVY_WEIGHTED_PRESSURE_PLATE);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.IRON_INGOT));
                }

            }
            case "block.minecraft.iron_block;block.minecraft.white_wool;block.minecraft.iron_block;block.minecraft.white_wool;" -> {
                // Insulated
                // 1: magma block
                // 2: wool 1-4
                // 3: packed ice

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.MAGMA_BLOCK));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_3).isEmpty() && stack.isOf(Items.WHITE_WOOL);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.PACKED_ICE));
                }

            }
            case "block.minecraft.iron_block;block.minecraft.sponge;block.minecraft.iron_block;block.minecraft.sponge;" -> {
                // Elastic
                // 1: feather
                // 2: breeze rods 1-4
                // 3: phantom membrane

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.FEATHER));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_3).isEmpty() && stack.isOf(Items.BREEZE_ROD);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.PHANTOM_MEMBRANE));
                }

            }
            case "block.minecraft.iron_block;block.minecraft.magma_block;block.minecraft.iron_block;block.minecraft.magma_block;" -> {
                // Thorns
                // 1: cactus
                // 2: magma block 1-4
                // 3: berry bush

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.CACTUS));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_3).isEmpty() && stack.isOf(Items.MAGMA_BLOCK);
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.SWEET_BERRIES));
                }
            }
            case "block.minecraft.iron_block;block.minecraft.slime_block;block.minecraft.iron_block;block.minecraft.slime_block;" -> {
                // Inertial
                // 1: phantom membrane
                // 2: nether star (1) slime block 0-3
                // 3: wind charge

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.PHANTOM_MEMBRANE));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return (
                        (getStack(BACK_0).isEmpty() && stack.isOf(Items.NETHER_STAR)) ||
                        (getStack(BACK_3).isEmpty() && stack.isOf(Items.SLIME_BLOCK))
                        );
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.WIND_CHARGE));
                }
            }
            case "block.minecraft.blue_ice;block.minecraft.packed_ice;block.minecraft.magma_block;block.minecraft.packed_ice;" -> {
                // Hydrodynamic
                // 1: gold pickaxe
                // 2: prismarine shard/crystal 1-3
                // 3: turtle scute

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.GOLDEN_PICKAXE));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_2).isEmpty() && (stack.isOf(Items.PRISMARINE_SHARD) || stack.isOf(Items.PRISMARINE_CRYSTALS));
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.TURTLE_SCUTE));
                }
            }
            case "block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;block.minecraft.sculk;" -> {
                // Swift Sneak
                // 1: echo shard
                // 2: XP Tome (level determined by filled capacity)
                // 3: echo shard

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.ECHO_SHARD));
                }
                else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(ModItems.XP_TOME) && XPTomeItem.getXP(stack) >= Util.XPLevel.LEVEL10.getPoints();
                }
                else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.ECHO_SHARD));
                }
            }
            case "block.minecraft.soul_sand;block.minecraft.soul_sand;block.minecraft.soul_sand;block.minecraft.soul_sand;" -> {
                // Soul speed
                // 1: wither skull
                // 2: lost soul
                // 3: soul sand

                if (placement == PedestalPlacement.LEFT) {
                    return (getStack(LEFT_0).isEmpty() && stack.isOf(Items.WITHER_SKELETON_SKULL));
                } else if (placement == PedestalPlacement.BACK) {
                    return getStack(BACK_0).isEmpty() && stack.isOf(ModItems.LOST_SOUL);
                } else {
                    return (getStack(RIGHT_0).isEmpty() && stack.isOf(Items.SOUL_SAND));
                }

            }
            default -> {
                return false;
            }
        }
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

    private void dropSlot(int... slots) {
        List<Integer> list = Arrays.asList(Arrays.stream(slots).boxed().toArray(Integer[]::new));
        World world = this.getWorld();
        if (world == null) {
            return;
        }
        BlockState state = world.getBlockState(this.getPos());

        Vec3d leftPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).rotateYClockwise(), 3);
        Vec3d backPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).getOpposite(), 3);
        Vec3d rightPos = new Vec3d(getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5).offset(state.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);

        if (list.contains(0)) {
            ItemStack stack = getStack(0);
            setStack(0, ItemStack.EMPTY);
            ItemScatterer.spawn(world, getPos().getX()+0.5, getPos().getY()+1.5, getPos().getZ()+0.5, stack);
        }

        for (int i = 1; i < 5; i++) {
            if (list.contains(i)) {
                ItemStack stack = getStack(i);
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, leftPos.x, leftPos.y, leftPos.z, stack);
            }
        }

        for (int i = 5; i < 9; i++) {
            if (list.contains(i)) {
                ItemStack stack = getStack(i);
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, backPos.x, backPos.y, backPos.z, stack);
            }
        }

        for (int i = 9; i < 13; i++) {
            if (list.contains(i)) {
                ItemStack stack = getStack(i);
                setStack(i, ItemStack.EMPTY);
                ItemScatterer.spawn(world, rightPos.x, rightPos.y, rightPos.z, stack);
            }
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
                case 0 -> setStack(9, stack);
                case 1 -> setStack(10, stack);
                case 2 -> setStack(11, stack);
                case 3 -> setStack(12, stack);
                case 4 -> setStack(13, stack);
            }
        } else if (forward == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the back direction
            switch (slot) {
                case 0 -> setStack(5, stack);
                case 1 -> setStack(6, stack);
                case 2 -> setStack(7, stack);
                case 3 -> setStack(8, stack);
                case 4 -> setStack(14, stack);
            }
        } else if (forward.rotateYCounterclockwise() == pedestalState.get(PedestalBlock.ALTAR_DIRECTION)) {
            // Pedestal is in the left direction
            switch (slot) {
                case 0 -> setStack(1, stack);
                case 1 -> setStack(2, stack);
                case 2 -> setStack(3, stack);
                case 3 -> setStack(4, stack);
                case 4 -> setStack(15, stack);
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
        nbt.putString("availableEnchant", availableEnchant);
        nbt.putInt("recipeCheck", recipeCheck);
        Inventories.writeNbt(nbt, inventory, wrapper);
        super.writeNbt(nbt, wrapper);

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        this.rotation = nbt.getInt("rotation");
        this.place_animation = nbt.getBoolean("place_animation");
        this.animation_phase = nbt.getInt("animation_phase");
        this.availableEnchant = nbt.getString("availableEnchant");
        this.recipeCheck = nbt.getInt("recipeCheck");
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

    private void dualHelixAnimation(World world, BlockPos pos, int red, int green) {

        double y = pos.getY() + (Math.abs(this.rotation_height-100)/100.0);

        for (Vec3d point : Util.drawCircle(new Vec3d(pos.getX() + 0.5, y, pos.getZ() + 0.5), new Vec3d(0, 1, 0), 0.6, 2, this.rotation_tick)) {
            particle(point, world, new DustParticleEffect(new Vector3f(red, green, 0), 0.6f));
        }
        for (Vec3d point : Util.drawCircle(new Vec3d(pos.getX() + 0.5, y, pos.getZ() + 0.5), new Vec3d(0, 1, 0), 0.6, 2, -this.rotation_tick)) {
            particle(point, world, new DustParticleEffect(new Vector3f(red, green, 0), 0.6f));
        }

    }

    public void clearIngredients() {

        for (int i = 1; i<13; i++) {
            if (getStack(i).isOf(ModItems.XP_TOME)) {
                setStack(i, new ItemStack(ModItems.XP_TOME));
                dropSlot(i);
            }
        }

        for (int i = 1; i<13; i++) {
            setStack(i, ItemStack.EMPTY);
        }

        if (!Objects.requireNonNull(getWorld()).isClient) {
            NbtCompound pos = new NbtCompound();
            pos.putInt("x", this.getPos().getX());
            pos.putInt("y", this.getPos().getY());
            pos.putInt("z", this.getPos().getZ());
            for (ServerPlayerEntity player : PlayerLookup.tracking(this)) {
                ServerPlayNetworking.send(player, new ClearAltarIngredientsPayload(pos));
            }
        }

        markDirty();

    }

    public void applyEnchant() {
        applyEnchant(availableEnchant, availableLevel);
    }

    public void applyEnchant(@NotNull String enchant, int level) {
        ItemStack stack = getStack(0);

        List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
        if (enchants == null) {
            enchants = new ArrayList<>();
        } else {
            enchants = new ArrayList<>(enchants);
        }

        for (EtherealEnchantComponent e : enchants.stream().toList()) {
            if (e.enchant().equals(enchant)) {
                enchants.remove(e);
                break;
            }
        }

        // clear/use recipe ingredients
        if (enchant.equals("weighted")) {
            ItemStack anvil = getStack(BACK_0);
            if (anvil.isOf(Items.ANVIL)) {
                setStack(BACK_0, new ItemStack(Items.CHIPPED_ANVIL));
                dropSlot(BACK_0);
                Objects.requireNonNull(getWorld()).playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS);
            } else if (anvil.isOf(Items.CHIPPED_ANVIL)) {
                setStack(BACK_0, new ItemStack(Items.DAMAGED_ANVIL));
                dropSlot(BACK_0);
                Objects.requireNonNull(getWorld()).playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS);
            } else {
                setStack(BACK_0, ItemStack.EMPTY);
                Objects.requireNonNull(getWorld()).playSound(null, this.getPos(), SoundEvents.BLOCK_ANVIL_BREAK, SoundCategory.BLOCKS);
            }
        }
        clearIngredients();


        enchants.add(new EtherealEnchantComponent(enchant, level));
        stack.set(ModComponents.ETHEREAL_ENCHANTS, enchants);

    }

    private static final Random RANDOM = Random.create();

    public static void tick(World world, BlockPos blockPos, BlockState blockState, AltarBlockEntity blockEntity) {
        blockEntity.verifyEnchant();
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
            blockEntity.animation_phase = 0;
            blockEntity.availableEnchant = "";
            blockEntity.rotation_tick += 5;
            blockEntity.rotation_height++;
            if (blockEntity.rotation_height >= 200) { blockEntity.rotation_height = 0; }
            if (blockEntity.rotation_tick >= 180) { blockEntity.rotation_tick = 0; }
            if (!blockState.get(AltarBlock.LEFT_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYClockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYClockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255);
            }

            if (!blockState.get(AltarBlock.RIGHT_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).rotateYCounterclockwise(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255);
            }

            if (!blockState.get(AltarBlock.BACK_PEDESTAL)) {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).getOpposite(), 3);
                blockEntity.dualHelixAnimation(world, pos, 255, 0);
            } else {
                BlockPos pos = blockPos.offset(blockState.get(AltarBlock.FACING).getOpposite(), 3);
                blockEntity.dualHelixAnimation(world, pos, 0, 255);
            }

        } else if (blockState.get(AltarBlock.PEDESTALS) == 3 && blockEntity.animation_phase == 0) {
            world.playSound(null, blockPos, ModSounds.PEDESTALS_PLACED, SoundCategory.BLOCKS, 1, 0.75f);
            blockEntity.animation_phase = 1;
            blockEntity.ticks = 0;
            blockEntity.markDirty();
        } else if (blockEntity.animation_phase == 1) {
            boolean deathActivation = false;
            boolean usedTotem = false;
            Entity deathActivator = null;

            if (!world.isClient) {
                for (LivingEntity entity :
                        world.getEntitiesByType(
                                TypeFilter.instanceOf(LivingEntity.class),
                                blockEntity.entityBox,
                                (e) -> true
                        )
                ) {
                    if (((LivingEntityExtension) entity).enchantingRework$usedTotem()) {
                        ((LivingEntityExtension) entity).enchantingRework$setUsedTotem(false);
                        deathActivation = true;
                        usedTotem = true;
                        deathActivator = entity;
                        break;
                    } else if (entity.isDead()) {
                        deathActivation = true;
                        deathActivator = entity;
                    }

                }
            }

            switch (blockEntity.availableEnchant) {
                case "chilled" -> {
                    blockEntity.ticks++;
                    if (blockEntity.ticks >= 360) {
                        blockEntity.ticks = 0;
                    }
                    if (blockEntity.ticks % 10 != 0) break;
                    for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 3, 8, blockEntity.ticks)) {
                        if (blockEntity.random.nextBetween(0, 8) <= 1) continue;
                        blockEntity.particle(point, world, ParticleTypes.SNOWFLAKE);
                    }
                }
                case "incendiary" -> {
                    blockEntity.ticks++;
                    if (blockEntity.ticks >= 360) {
                        blockEntity.ticks = 0;
                    }
                    if (blockEntity.ticks % 10 != 0) break;
                    for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 3, 8, blockEntity.ticks)) {
                        if (blockEntity.random.nextBetween(0, 8) <= 1) continue;
                        blockEntity.particle(point, world, ParticleTypes.FLAME);
                    }
                }
                case "celestial_binding" -> {
                    blockEntity.ticks++;
                    if (blockEntity.ticks >= 360) {
                        blockEntity.ticks = 0;
                    }
                    if (deathActivation) {
                        blockEntity.applyEnchant();
                    }
                    if (blockEntity.ticks % 10 != 0) break;
                    for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 3, 8, blockEntity.ticks)) {
                        if (blockEntity.random.nextBetween(0, 8) <= 1) continue;
                        blockEntity.particle(point, world, ParticleTypes.END_ROD);
                    }
                }
                case "soulbound" -> {
                    blockEntity.ticks++;
                    if (blockEntity.ticks >= 360) {
                        blockEntity.ticks = 0;
                    }

                    if (deathActivation) {
                        EtherealEnchanting.LOGGER.info("Applying soulbound enchant!");
                        blockEntity.applyEnchant();
                    }

                    if (blockEntity.ticks % 10 != 0) break;
                    Vec3d backPos = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5).offset(blockState.get(AltarBlock.FACING).getOpposite(), 3);
                    for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1.5, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 3, 8, blockEntity.ticks)) {
                        if (blockEntity.random.nextBetween(0, 8) <= 1) continue;
                        if (backPos.distanceTo(point) < 1) continue;

                        blockEntity.particle(point, world, ParticleTypes.SOUL);
                    }

                    for (Vec3d point : Util.drawCircle(backPos, new Vec3d(0, 1, 0), 1, 8, blockEntity.ticks * 2)) {
                        if (blockEntity.random.nextBetween(0, 6) <= 1) continue;
                        blockEntity.particle(point, world, ParticleTypes.SOUL);
                    }
                }
                case "slashing" -> {

                }
                case "weighted" -> {

                }
                case "conductive" -> {
                }
                case "inductive" -> {
                }
                case "mending" -> {
                }
                case "unbreaking" -> {
                }
                case "luck" -> {
                }
                case "padded" -> {
                }
                case "resistive" -> {
                }
                case "plated" -> {
                }
                case "insulated" -> {
                }
                case "elastic" -> {
                }
                case "thorns" -> {
                }
                case "inertial" -> {
                }
                case "hydrodynamic" -> {
                }
                case "swift_sneak" -> {
                }
                case "soul_speed" -> {
                }
            }

            if (deathActivation && deathActivator.getType().equals(EntityType.FOX)) {
                if (usedTotem) {
                    blockEntity.applyEnchant("cruel_and_unusual", -6);
                } else {
                    blockEntity.applyEnchant("cruel_and_unusual", RANDOM.nextBetween(1, 5));
                }
            }

        }


    }

}
