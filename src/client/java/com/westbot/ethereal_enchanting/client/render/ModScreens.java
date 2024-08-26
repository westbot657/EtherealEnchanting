package com.westbot.ethereal_enchanting.client.render;

import com.westbot.ethereal_enchanting.client.render.gui.screen.EnchanterScreen;
import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import com.westbot.ethereal_enchanting.screen.ScreenHandlers;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class ModScreens extends HandledScreens {

    public static void initialize() {}

    static {
        register(ScreenHandlers.ENCHANTER, EnchanterScreen::new);
        register(ScreenHandlers.SPELL_BOOK, SpellBookScreen::new);
    }

}
