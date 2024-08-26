package com.westbot.ethereal_enchanting.networking;

import com.westbot.ethereal_enchanting.EtherealEnchanting;
import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.blocks.entity.EtherealEnchanterBlockEntity;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.entity.CelestialTrailEntity;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import com.westbot.ethereal_enchanting.screen.EtherealEnchanterScreenHandler;
import com.westbot.ethereal_enchanting.screen.ScreenHandlers;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerSyncHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModNetworking {

    public static final Identifier SYNC_TRAIL = Identifier.of("ethereal_enchanting", "sync_trail");

    public static final Identifier BATCH_SYNC_TRAIL = Identifier.of("ethereal_enchanting", "batch_sync_trail");
    public static final Identifier BATCH_SYNC_TRAIL_REQUEST = Identifier.of("ethereal_enchanting", "batch_sync_trail_request");
    public static final Identifier SYNC_ALTAR_INVENTORY = Identifier.of("ethereal_enchanting", "sync_altar_inventory");

    public static final Identifier CLEAR_INGREDIENTS = Identifier.of("ethereal_enchanting", "clear_ingredients");

    public static final Identifier ENCHANTER_XP_SYNC = Identifier.of("ethereal_enchanting", "sync_enchanter_xp");

    public static final Identifier REQUEST_ENCHANT_REMOVAL = Identifier.of("ethereal_enchanting", "request_enchant_removal");

    public static void initialize() {
        PayloadTypeRegistry.playS2C().register(SyncTrailPayload.ID, SyncTrailPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(BatchSyncTrailPayload.ID, BatchSyncTrailPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ClearAltarIngredientsPayload.ID, ClearAltarIngredientsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SyncAltarInventoryPayload.ID, SyncAltarInventoryPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(BatchSyncTrailRequestPayload.ID, BatchSyncTrailRequestPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(EnchanterXPSyncPayload.ID, EnchanterXPSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestEnchantmentRemovalPayload.ID, RequestEnchantmentRemovalPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BatchSyncTrailRequestPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                List<UUID> uuids = new ArrayList<>();
                List<NbtCompound> compounds = new ArrayList<>();
                for (UUID uuid : payload.uuids()) {

                    CelestialTrailEntity entity = (CelestialTrailEntity) ((ServerWorld) context.player().getWorld()).getEntity(uuid);
                    if (entity == null) continue;
                    uuids.add(uuid);
                    compounds.add(entity.writeNbt(new NbtCompound()));
                }

                ServerPlayNetworking.send(context.player(), new BatchSyncTrailPayload(uuids, compounds));

            });
        });

        ServerPlayNetworking.registerGlobalReceiver(EnchanterXPSyncPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.server().getPlayerManager().getPlayer(payload.playerUuid());
                if (player == null) return;
                World world = player.getWorld();

                if (player.currentScreenHandler.syncId == payload.syncId()) {
                    EtherealEnchanterBlockEntity blockEntity = (EtherealEnchanterBlockEntity) world.getBlockEntity(((EtherealEnchanterScreenHandler) player.currentScreenHandler).pos);

                    if (blockEntity == null) return;

                    ItemStack tome = blockEntity.inventory.getStack(1);
                    if ((!tome.isEmpty()) && (tome.isOf(ModItems.XP_TOME))) {
                        EtherealEnchanting.LOGGER.info("Server Player xp before change: {}", player.totalExperience);
                        player.addExperience((-player.totalExperience)+payload.playerXp());
                        EtherealEnchanting.LOGGER.info("Server player xp after change: {}", player.totalExperience);

                        XPTomeItem.setXP(tome, payload.tomeXp());

                    }
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(RequestEnchantmentRemovalPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                ServerPlayerEntity player = context.server().getPlayerManager().getPlayer(payload.playerUuid());
                if (player == null) return;
                World world = player.getWorld();

                if (player.currentScreenHandler.syncId == payload.syncId()) {
                    EtherealEnchanterBlockEntity blockEntity = (EtherealEnchanterBlockEntity) world.getBlockEntity(((EtherealEnchanterScreenHandler) player.currentScreenHandler).pos);
                    if (blockEntity == null) return;

                    ItemStack enchanted = blockEntity.inventory.getStack(0);
                    if (enchanted.isEmpty()) return;

                    List<EtherealEnchantComponent> enchants = enchanted.get(ModComponents.ETHEREAL_ENCHANTS);
                    if (enchants == null) return;
                    enchants = new ArrayList<>(enchants);

                    enchants.removeIf(e -> e.enchant().equals(payload.enchant()));
                    enchanted.set(ModComponents.ETHEREAL_ENCHANTS, enchants);

                }
            });
        });

    }
}
