package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.screen.SpellBookScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SpellBookScreen extends HandledScreen<SpellBookScreenHandler> {

    protected interface Element {
        void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }


    protected static abstract class Page implements Element {
        protected Identifier TEXTURE;

        protected Page(@Nullable Identifier texture) {
            TEXTURE = texture;
        }

        public abstract void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        public abstract void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }

    private static final List<Page> PAGES = new ArrayList<>() {{

        add(new Page(null) {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {

            }
            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        });



    }};


    public SpellBookScreen(SpellBookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Element page = PAGES.get(handler.page[0]);
        if (page != null) {
            page.drawBg(context, delta, mouseX, mouseY, this);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        Element page = PAGES.get(handler.page[0]);
        if (page != null) {
            page.draw(context, mouseX, mouseY, delta, this);
        }
    }
}
