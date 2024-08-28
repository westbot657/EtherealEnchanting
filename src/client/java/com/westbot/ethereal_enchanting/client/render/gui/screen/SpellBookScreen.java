package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
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
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class SpellBookScreen extends HandledScreen<SpellBookScreenHandler> {

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

    protected interface Element {
        void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }

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

    protected static abstract class Page implements Element {

        protected Page() {
        }

        public abstract void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler);
        public abstract void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler);
    }

    public static interface AltarDisplayInterface extends Element {
        void drawWithText(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler, String block1key, String block2key, String block3key, String block4key);
    }

    private static final AltarDisplayInterface ALTAR_DISPLAY = new AltarDisplayInterface() {
        @Override
        public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
            MatrixStack matrices = context.getMatrices();

            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            RenderSystem.enableBlend();
            context.drawTexture(GRADIENT, (handler.uiX)*2, (handler.uiY)*2, 0, 0, 512, 512, 512, 512);

            context.drawTexture(WIREFRAME, (handler.uiX)*2, (handler.uiY-64)*2, 0, 0, 512, 512, 512, 512);
            RenderSystem.disableBlend();

            matrices.pop();
        }

        @Override
        public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

        }

        public Text getDecryptedText(Text sgaText, SpellBookScreen handler) {
            StringBuilder b1 = new StringBuilder();
            for (String c : sgaText.getString().split("")) {
                if (handler.handler.cipher.contains(c.toLowerCase())) {
                    b1.append(c);
                } else {
                    b1.append(" ");
                }
            }
            return Text.literal(b1.toString());
        }

        // right-aligned
        private static final int b1X = 40/2;
        private static final int b1Y = 262/2;
        private static final int b2X = 33/2;
        private static final int b2Y = 186/2;

        // left-aligned
        private static final int b3X = 522/2;
        private static final int b3Y = 186/2;
        private static final int b4X = 515/2;
        private static final int b4Y = 262/2;

        @Override
        public void drawWithText(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler, String block1key, String block2key, String block3key, String block4key) {
            Text block1SGA = Text.translatable(block1key).setStyle(STYLE);
            Text block1 = getDecryptedText(block1SGA, handler);
            Text block2SGA = Text.translatable(block2key).setStyle(STYLE);
            Text block2 = getDecryptedText(block2SGA, handler);
            Text block3SGA = Text.translatable(block3key).setStyle(STYLE);
            Text block3 = getDecryptedText(block3SGA, handler);
            Text block4SGA = Text.translatable(block4key).setStyle(STYLE);
            Text block4 = getDecryptedText(block4SGA, handler);

            int sgaw1 = (int) (handler.textRenderer.getWidth(block1SGA)/1.1);
            int w1 = (int) (handler.textRenderer.getWidth(block1)/1.1);

            int sgaw2 = (int) (handler.textRenderer.getWidth(block2SGA)/1.1);
            int w2 = (int) (handler.textRenderer.getWidth(block2)/1.1);

            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            context.drawText(handler.textRenderer, block1SGA, (handler.uiX+b1X-sgaw1)*2, (handler.uiY+b1Y)*2, GRAY, false);
            context.drawText(handler.textRenderer, block1, (handler.uiX+b1X-w1)*2, (handler.uiY+b1Y+4)*2, WHITE, false);

            context.drawText(handler.textRenderer, block2SGA, (handler.uiX+b2X-sgaw2)*2, (handler.uiY+b2Y)*2, GRAY, false);
            context.drawText(handler.textRenderer, block2, (handler.uiX+b2X-w2)*2, (handler.uiY+b2Y+4)*2, WHITE, false);

            context.drawText(handler.textRenderer, block3SGA, (handler.uiX+b3X)*2, (handler.uiY+b3Y)*2, GRAY, false);
            context.drawText(handler.textRenderer, block3, (handler.uiX+b3X)*2, (handler.uiY+b3Y+4)*2, WHITE, false);

            context.drawText(handler.textRenderer, block4SGA, (handler.uiX+b4X)*2, (handler.uiY+b4Y)*2, GRAY, false);
            context.drawText(handler.textRenderer, block4, (handler.uiX+b4X)*2, (handler.uiY+b4Y+4)*2, WHITE, false);
            matrices.pop();

        }


    };

    private static final List<Page> PAGES = new ArrayList<>() {{

        add(new Page() {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
            }
            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        });

        add(new Page() {
            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                ALTAR_DISPLAY.drawBg(context, delta, mouseX, mouseY, handler);

                ALTAR_DISPLAY.drawWithText(context, mouseX, mouseY, delta, handler,
                    "block.minecraft.blue_ice",
                    "block.minecraft.bamboo_mosaic",
                    "block.minecraft.chiseled_copper",
                    "block.minecraft.chiseled_polished_blackstone"
                );
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {
                ALTAR_DISPLAY.draw(context, mouseX, mouseY, delta, handler);
            }
        });


    }};


    public SpellBookScreen(SpellBookScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
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
