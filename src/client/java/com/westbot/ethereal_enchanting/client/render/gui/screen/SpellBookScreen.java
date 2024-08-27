package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
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

    public static final Identifier BACKGROUND = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/spell_book_screen_bg.png");

    private boolean pb_hovered = false;
    private boolean pf_hovered = false;

    // page forward/backward are 23x13
    public static final Identifier PAGE_BACKWARD = Identifier.of("minecraft", "textures/gui/sprites/widget/page_backward.png");
    public static final Identifier PAGE_FORWARD = Identifier.of("minecraft", "textures/gui/sprites/widget/page_forward.png");
    public static final Identifier PAGE_BACKWARD_HIGHLIGHTED = Identifier.of("minecraft", "textures/gui/sprites/widget/page_backward_highlighted.png");
    public static final Identifier PAGE_FORWARD_HIGHLIGHTED = Identifier.of("minecraft", "textures/gui/sprites/widget/page_forward_highlighted.png");

    // 46,151
    // 187, 151

    public int uiX;
    public int uiY;

    protected interface Element {
        void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0) {
            if (pb_hovered) {
                handler.page[0] = Math.max(0, handler.page[0]-1);
                return true;
            }
            if (pf_hovered) {
                handler.page[0] = Math.min(handler.page[0]+1, PAGES.size());
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected static abstract class Page implements Element {

        protected Page() {
        }

        public abstract void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        public abstract void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }

    private static final List<Page> PAGES = new ArrayList<>() {{

        add(new Page() {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY, 0, 0, 256, 256);
            }
            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        });

        add(new Page() {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY, 0, 0, 256, 256);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        });


    }};


    public SpellBookScreen(SpellBookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        backgroundWidth = 256;
        backgroundHeight = 256;
        ((HandledScreenMixinInterface) this).enchantingRework$setHidden(true);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Element page = PAGES.get(handler.page[0]);

        uiX = (this.width - backgroundWidth) / 2;
        uiY = (this.height - backgroundHeight) / 2;

        if (page != null) {
            page.drawBg(context, delta, mouseX, mouseY, this);
        }

        if (isPointWithinBounds(46, 151, 22, 12, mouseX, mouseY)) {
            context.drawTexture(PAGE_BACKWARD_HIGHLIGHTED, uiX+46, uiY+151, 0, 0, 23, 13);
            pb_hovered = true;
        } else {
            context.drawTexture(PAGE_BACKWARD, uiX+46, uiY+151, 0, 0, 23, 13);
            pb_hovered = false;
        }

        if (isPointWithinBounds(187, 151, 22, 12, mouseX, mouseY)) {
            context.drawTexture(PAGE_FORWARD_HIGHLIGHTED, uiX+187, uiY+151, 0, 0, 23, 13);
            pf_hovered = true;
        } else {
            context.drawTexture(PAGE_FORWARD, uiX+187, uiY+151, 0, 0, 23, 13);
            pf_hovered = false;
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
