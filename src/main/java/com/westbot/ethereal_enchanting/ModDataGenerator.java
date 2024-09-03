package com.westbot.ethereal_enchanting;

import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.advancement.criterion.TickCriterion;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class ModDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();

        pack.addProvider(AdvancementsProvider::new);
    }

    static class AdvancementsProvider extends FabricAdvancementProvider {

        protected AdvancementsProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
            super(output, registryLookup);
        }

        @Override
        public void generateAdvancement(RegistryWrapper.WrapperLookup registryLookup, Consumer<AdvancementEntry> consumer) {
            AdvancementEntry rootAdvancement = Advancement.Builder.create()
                .display(
                    ModItems.ENCHANTED_RUNE,
                    Text.translatable("advancements.ethereal_enchanting.root.label"),
                    Text.translatable("advancements.ethereal_enchanting.root.description"),
                    Identifier.of("ethereal_enchanting","textures/gui/advancements/ethereal_enchanting.png"),
                    AdvancementFrame.TASK,
                    false,
                    false,
                    false
                )
                .criterion("root", TickCriterion.Conditions.createTick())
                .build(consumer, EtherealEnchanting.MOD_ID + "/root");


            AdvancementEntry getEnchanter = Advancement.Builder.create()
                .display(
                    ModBlocks.ETHEREAL_ENCHANTER_BLOCK,
                    Text.translatable("advancements.ethereal_enchanting.enchanter.label"),
                    Text.translatable("advancements.ethereal_enchanting.enchanter.description"),
                    Identifier.of("ethereal_enchanting", "textures/gui/advancements/ethereal_enchanting.png"),
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )
                .criterion("ethereal_enchanting_got_enchanter", InventoryChangedCriterion.Conditions.items(ModBlocks.ETHEREAL_ENCHANTER_BLOCK))
                .parent(rootAdvancement)
                .build(consumer, EtherealEnchanting.MOD_ID + "/get_enchanter");

            AdvancementEntry getAltar = Advancement.Builder.create()
                .display(
                    ModBlocks.ALTAR_BLOCK,
                    Text.translatable("advancements.ethereal_enchanting.altar.label"),
                    Text.translatable("advancements.ethereal_enchanting.altar.description"),
                    Identifier.of("ethereal_enchanting", "textures/gui/advancements/ethereal_enchanting.png"),
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )
                .criterion("ethereal_enchanting_got_altar", InventoryChangedCriterion.Conditions.items(ModItems.SPELL_BOOK))
                .parent(getEnchanter)
                .build(consumer, EtherealEnchanting.MOD_ID + "/get_altar");


            AdvancementEntry getSpellBook = Advancement.Builder.create()
                .display(
                    ModItems.SPELL_BOOK,
                    Text.translatable("advancements.ethereal_enchanting.spell_book.label"),
                    Text.translatable("advancements.ethereal_enchanting.spell_book.description"),
                    Identifier.of("ethereal_enchanting", "textures/gui/advancements/ethereal_enchanting.png"),
                    AdvancementFrame.TASK,
                    true,
                    true,
                    false
                )
                .criterion("ethereal_enchanting_got_spell_book", InventoryChangedCriterion.Conditions.items(ModItems.SPELL_BOOK))
                .parent(getAltar)
                .build(consumer, EtherealEnchanting.MOD_ID + "/get_spell_book");

        }
    }

}
