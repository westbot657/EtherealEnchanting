package com.westbot.ethereal_enchanting.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class ScreenHandlers {

    public static final ScreenHandlerType<EtherealEnchanterScreenHandler> ENCHANTER = register("enchanter", EtherealEnchanterScreenHandler::new);

    public static final ScreenHandlerType<SpellBookScreenHandler> SPELL_BOOK = register("spell_book", SpellBookScreenHandler::new);

    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String id, ScreenHandlerType.Factory<T> factory) {
        return Registry.register(Registries.SCREEN_HANDLER, id, new ScreenHandlerType<>(factory, FeatureFlags.VANILLA_FEATURES));
    }

    public static void initialize() {}

}
