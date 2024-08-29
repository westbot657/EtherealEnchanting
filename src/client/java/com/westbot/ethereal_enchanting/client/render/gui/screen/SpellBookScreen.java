package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
import com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book.AltarDisplay;
import com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book.Element;
import com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book.Page;
import com.westbot.ethereal_enchanting.screen.SpellBookScreenHandler;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class SpellBookScreen extends HandledScreen<SpellBookScreenHandler> {

    public SpellBookScreenHandler handler;
    public TextRenderer textRenderer;

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);


    public static final Identifier BACKGROUND = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/spell_book_screen_bg.png");

    public static final Identifier GRADIENT = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/gradient.png");
    public static final Identifier WIREFRAME = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/altar_wireframe.png");


    private final PlayerInventory playerInventory;
    private boolean pb_hovered = false;
    private boolean pf_hovered = false;

    // page forward/backward are 23x13
    public static final Identifier PAGE_BACKWARD = Identifier.of("ethereal_enchanting", "textures/gui/sprites/widget/page_backward.png");
    public static final Identifier PAGE_FORWARD = Identifier.of("ethereal_enchanting", "textures/gui/sprites/widget/page_forward.png");
    public static final Identifier PAGE_BACKWARD_HIGHLIGHTED = Identifier.of("ethereal_enchanting", "textures/gui/sprites/widget/page_backward_highlighted.png");
    public static final Identifier PAGE_FORWARD_HIGHLIGHTED = Identifier.of("ethereal_enchanting", "textures/gui/sprites/widget/page_forward_highlighted.png");

    protected static final int offsetY = 40;

    protected static int BLACK = MathHelper.packRgb(0,0,0);
    protected static int GRAY = MathHelper.packRgb(0.8f, 0.8f, 0.8f);
    protected static int WHITE = MathHelper.packRgb(1,1,1);

    // 46,151
    // 187, 151

    public int uiX;
    public int uiY;



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0) {
            int t = handler.page[0];
            if (pb_hovered) {
                handler.page[0] = Math.max(0, handler.page[0]-1);
                if (t != handler.page[0]) {
                    playerInventory.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.5f, 1);
                }
                return true;
            }
            if (pf_hovered) {
                handler.page[0] = Math.min(handler.page[0]+1, PAGES.size()-1);
                if (t != handler.page[0]) {
                    playerInventory.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.2f, 1);
                }
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static final List<Page> PAGES = new ArrayList<>() {{

        add(new Page() {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
            }
            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {

            }
        });

        add(new Page() {

            private final AltarDisplay altarDisplay = new AltarDisplay(
                    "block.minecraft.blue_ice",
                    "block.minecraft.bamboo_mosaic",
                    "block.minecraft.chiseled_copper",
                    "block.minecraft.chiseled_polished_blackstone"
            );

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                altarDisplay.draw(context, mouseX, mouseY, delta, handler);
            }
        });


    }};


    public SpellBookScreen(SpellBookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.handler = handler;
        playerInventory = inventory;

        backgroundWidth = 256;
        backgroundHeight = 256;
        ((HandledScreenMixinInterface) this).enchantingRework$setHidden(true);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        Element page = PAGES.get(handler.page[0]);

        uiX = (this.width - backgroundWidth) / 2;
        uiY = ((this.height - backgroundHeight) / 2);

        if (page != null) {
            page.drawBg(context, delta, mouseX, mouseY, this);
        }

        if (isPointWithinBounds(46, 151+offsetY, 22, 12, mouseX, mouseY)) {
            context.drawTexture(PAGE_BACKWARD_HIGHLIGHTED, uiX+46, uiY+151+offsetY, 0, 0, 23, 13, 23, 13);
            pb_hovered = true;
        } else {
            context.drawTexture(PAGE_BACKWARD, uiX+46, uiY+151+offsetY, 0, 0, 23, 13, 23, 13);
            pb_hovered = false;
        }

        if (isPointWithinBounds(187, 151+offsetY, 22, 12, mouseX, mouseY)) {
            context.drawTexture(PAGE_FORWARD_HIGHLIGHTED, uiX+187, uiY+151+offsetY, 0, 0, 23, 13, 23, 13);
            pf_hovered = true;
        } else {
            context.drawTexture(PAGE_FORWARD, uiX+187, uiY+151+offsetY, 0, 0, 23, 13, 23, 13);
            pf_hovered = false;
        }

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);
        context.drawText(this.textRenderer, Text.translatable("gui.ethereal_enchanting.spell_book.pages", handler.page[0]+1, PAGES.size()), (uiX+70)*2, (uiY+160+offsetY)*2, BLACK, false);
        matrices.pop();

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
