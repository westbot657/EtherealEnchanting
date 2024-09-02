package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;

public abstract class Page implements Element {

    protected Page() {
    }

    public abstract void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
    public abstract void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);

    public boolean onClick(double mouseX, double mouseY, int button) {
        return false;
    }
}

