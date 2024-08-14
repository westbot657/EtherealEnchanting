package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroup {

    public static final RegistryKey<ItemGroup> ETHEREAL_ENCHANTING_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of("ethereal_enchanting", "item_group"));
    public static final ItemGroup ETHEREAL_ENCHANTING_GROUP = FabricItemGroup.builder().icon(() -> new ItemStack(ModItems.XP_TOME)).displayName(Text.translatable("itemGroup.ethereal_enchanting.group1")).build();

    public static void initialize() {

        Registry.register(Registries.ITEM_GROUP, ETHEREAL_ENCHANTING_GROUP_KEY, ETHEREAL_ENCHANTING_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ETHEREAL_ENCHANTING_GROUP_KEY).register(group -> {
            // Blocks
            group.add(ModBlocks.ALTAR_BLOCK);
            group.add(ModBlocks.PEDESTAL_BLOCK);
            group.add(ModBlocks.ETHEREAL_ENCHANTER_BLOCK);

            // Items
            group.add(ModItems.XP_TOME);
            group.add(ModItems.ENCHANTED_RUNE);
            group.add(ModItems.WRITTEN_RUNE);
            group.add(ModItems.LOST_SOUL);
        });


    }

}
