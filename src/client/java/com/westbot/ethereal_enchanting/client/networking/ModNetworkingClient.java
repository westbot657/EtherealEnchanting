package com.westbot.ethereal_enchanting.client.networking;

import com.westbot.ethereal_enchanting.blocks.entity.AltarBlockEntity;
import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.networking.BatchSyncTrailPayload;
import com.westbot.ethereal_enchanting.networking.ClearAltarIngredientsPayload;
import com.westbot.ethereal_enchanting.networking.SyncAltarInventoryPayload;
import com.westbot.ethereal_enchanting.networking.SyncTrailPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.Inventories;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public class ModNetworkingClient {

    public static void initialize() {
        ClientPlayNetworking.registerGlobalReceiver(SyncTrailPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;

                for (Entity entity : world.getEntities()) {
                    if (entity.getUuid().equals(payload.uuid())) {
                        CelestialTrailEntity entity1 = (CelestialTrailEntity) entity;
                        entity1.sync(payload.corners());
                        return;
                    }
                }

            });
        });


        ClientPlayNetworking.registerGlobalReceiver(BatchSyncTrailPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;
                for (Entity entity : world.getEntities()) {
                    if (payload.uuids().contains(entity.getUuid())) {
                        CelestialTrailEntity entity1 = (CelestialTrailEntity) entity;
                        entity1.sync(payload.compounds().get(payload.uuids().indexOf(entity.getUuid())));
                        return;
                    }
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(ClearAltarIngredientsPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                ClientWorld world = context.client().world;
                if (world == null) return;
                BlockPos blockPos = new BlockPos(
                        payload.blockPos().getInt("x"),
                        payload.blockPos().getInt("y"),
                        payload.blockPos().getInt("z")
                        );

                AltarBlockEntity altar = (AltarBlockEntity) world.getBlockEntity(blockPos);
                if (altar == null) return;

                altar.clearIngredients();

            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SyncAltarInventoryPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                World world = context.client().world;
                if (world == null) return;

                BlockPos pos = new BlockPos(
                    payload.blockPos().getInt("x"),
                    payload.blockPos().getInt("y"),
                    payload.blockPos().getInt("z")
                    );


                AltarBlockEntity blockEntity = (AltarBlockEntity) world.getBlockEntity(pos);
                if (blockEntity == null) return;

                // TODO: figure out how to access the WrapperLookup on the client.
                // Inventories.readNbt(payload.inventory(), blockEntity.inventory);
            });
        });


    }

}
