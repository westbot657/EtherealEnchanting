package com.westbot.ethereal_enchanting.blocks.entity;

import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class AltarBlockEntity extends BlockEntity {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(16, ItemStack.EMPTY);

    private int rotation = 0;
    private final int rotation_max = 360/5;
    private boolean place_animation = true;
    private int animation_stage = 0;

    private int clicks = 0;

    private int ticks = -1;

    public int getClicks() {
        return clicks;
    }
    public void addClick() {
        clicks++;
        markDirty();
    }

    public AltarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ALTAR_BLOCK_ENTITY_TYPE, pos, state);

    }

    @Override
    public void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {

        nbt.putInt("rotation", this.rotation);
        nbt.putBoolean("place_animation", this.place_animation);
        nbt.putInt("clicks", this.clicks);
//        nbt.putInt("animation_stage", animation_stage);
//        nbt.putInt("ticks", ticks);
        super.writeNbt(nbt, wrapper);

    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup wrapper) {
        super.readNbt(nbt, wrapper);

        this.rotation = nbt.getInt("rotation");
        this.place_animation = nbt.getBoolean("place_animation");
        this.clicks = nbt.getInt("clicks");
//        animation_stage = nbt.getInt("animation_stage");
//        ticks = nbt.getInt("ticks");

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


        for (ServerPlayerEntity player : world.getServer().getOverworld().getPlayers()) {

            player.sendMessage(Text.literal("particle"));
            world.getServer().getWorld(world.getRegistryKey()).spawnParticles(
                player, ParticleTypes.ENCHANT, true, pos.x, pos.y, pos.z, 1, 0, 0, 0, 1
            );

        }

    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, AltarBlockEntity blockEntity) {

        if (world.isClient) {
            return;
        }

        if (blockEntity.place_animation) {
            if (blockEntity.animation_stage == 0) {
                blockEntity.ticks++;
                if (blockEntity.ticks > 10) {
                    blockEntity.animation_stage = 1;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            }
            else if (blockEntity.animation_stage == 1) {
                double radius = 2 * Util.easeOutBack(blockEntity.ticks / 100.0);
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), radius, 5, blockEntity.rotation)) {
                    blockEntity.particle(point, world);
                }
                blockEntity.ticks++;
                if (blockEntity.ticks > 100) {
                    blockEntity.animation_stage = 2;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            } else if (blockEntity.animation_stage == 2) {
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), 2, 5, blockEntity.rotation)) {
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
                for (Vec3d point : Util.drawCircle(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 1, blockPos.getZ() + 0.5), new Vec3d(0, 1, 0), radius, 5, blockEntity.rotation)) {
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
                    blockEntity.place_animation = false;
                    blockEntity.animation_stage = 0;
                    blockEntity.ticks = 0;
                    blockEntity.markDirty();
                }
            }

            blockEntity.rotation += 5;
            if (blockEntity.rotation > blockEntity.rotation_max) {
                blockEntity.rotation = 0;
            }
        }


    }

}
