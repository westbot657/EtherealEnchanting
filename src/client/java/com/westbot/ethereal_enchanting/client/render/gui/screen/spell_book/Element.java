package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;

public interface Element {
    void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
    void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
}