package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
import com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book.*;
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
    public TextRenderer textRenderer2;

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);


    public static final Identifier BACKGROUND = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/spell_book_screen_bg.png");


    public final PlayerInventory playerInventory;
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

    public static final int lRuneX = 0;
    public static final int lRuneY = 64;

    public static final int mRuneX = 108;
    public static final int mRuneY = 40;

    public static final int rRuneX = 216;
    public static final int rRuneY = 64;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        Page page = PAGES.get(handler.page[0]);

        if (button == 0) {
            int t = handler.page[0];
            if (pb_hovered) {
                handler.page[0] = Math.max(0, handler.page[0]-1);
                if (t != handler.page[0]) {
                    playerInventory.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.5f, 1);
                    handler.buildCipher();
                }
                return true;
            }
            if (pf_hovered) {
                handler.page[0] = Math.min(handler.page[0]+1, PAGES.size()-1);
                if (t != handler.page[0]) {
                    playerInventory.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.2f, 1);
                    handler.buildCipher();
                }
                return true;
            }
        }

        if (page.onClick(mouseX, mouseY, button)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }


    public boolean collides(int x, int y, int width, int height, double px, double py) {
        return isPointWithinBounds(x, y, width, height, px, py);
    }

    private static final List<Page> PAGES = new ArrayList<>() {{

        add(new Page() { // Pg 0 : table of contents
            private SpellBookScreen handler;

            private final List<TextLink> links = new ArrayList<>() {{
                add(new TextLink(0, 60, "spell.chilled", 1));
                add(new TextLink(0, 80, "spell.incendiary", 2));
                add(new TextLink(0, 100, "spell.luck", 3));
                add(new TextLink(0, 120, "spell.swift_sneak", 4));
                add(new TextLink(0, 140, "spell.soul_speed", 5));
            }};

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                this.handler = handler;
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                for (TextLink link : links) {
                    link.drawBg(context, delta, mouseX, mouseY, handler);
                }
            }
            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                for (TextLink link : links) {
                    link.draw(context, mouseX, mouseY, delta, handler);
                }
            }
            @Override
            public boolean onClick(double mouseX, double mouseY, int button) {
                if (this.handler == null) return false;
                for (TextLink link : links) {
                    if (link.onClick(mouseX, mouseY, button)) {
                        return true;
                    }
                }
                return false;
            }
        }); // Pg 0 : table of contents

        add(new Page() { // Pg 1 : Chilled

            private final AltarDisplay altarDisplay = new AltarDisplay(
                    "block.minecraft.blue_ice",
                    "block.minecraft.blue_ice",
                    "block.minecraft.blue_ice",
                    "block.minecraft.blue_ice"
            );

            private final RuneDisplay leftRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.chilled.arrow",
                lRuneX, lRuneY
            );

            private final RuneDisplay rightRune = new RuneDisplay(
                RuneDisplay.RuneType.SCALE_3,
                "spell.chilled.ice",
                "spell.chilled.packed_ice",
                "spell.chilled.blue_ice",
                "",
                rRuneX, rRuneY
            );

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
                leftRune.drawRune(context, delta, mouseX, mouseY, handler);
                rightRune.drawRune(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
                altarDisplay.draw(context, mouseX, mouseY, delta, handler);
            }

        }); // Pg 1 : Chilled

        add(new Page() { // Pg 2 : Incendiary

            private final AltarDisplay altarDisplay = new AltarDisplay(
                "block.minecraft.magma_block",
                "block.minecraft.magma_block",
                "block.minecraft.magma_block",
                "block.minecraft.magma_block"
            );

            private final RuneDisplay leftRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.incendiary.netherrack",
                lRuneX, lRuneY
            );

            private final RuneDisplay middleRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.incendiary.blaze_rod",
                mRuneX, mRuneY
            );

            private final RuneDisplay rightRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.incendiary.blaze_powder",
                rRuneX, rRuneY
            );

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
                leftRune.drawRune(context, delta, mouseX, mouseY, handler);
                middleRune.drawRune(context, delta, mouseX, mouseY, handler);
                rightRune.drawRune(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }

        }); // Pg 2 : Incendiary

        add(new Page() { // Pg 3 : Luck

            private final AltarDisplay altarDisplay = new AltarDisplay(
                "block.minecraft.moss_block",
                "block.minecraft.moss_block",
                "block.minecraft.moss_block",
                "block.minecraft.moss_block"
            );

            private final RuneDisplay leftRune = new RuneDisplay(
                RuneDisplay.RuneType.SEQUENCE_0,
                "spell.luck.lapis",
                lRuneX, lRuneY
            );

            private final RuneDisplay middleRune = new RuneDisplay(
                RuneDisplay.RuneType.SEQUENCE_1,
                "spell.luck.redstone_dust",
                mRuneX, mRuneY
            );

            private final RuneDisplay rightRune = new RuneDisplay(
                RuneDisplay.RuneType.SEQUENCE_2,
                "spell.luck.gold_ingot",
                rRuneX, rRuneY
            );


            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
                leftRune.drawRune(context, delta, mouseX, mouseY, handler);
                middleRune.drawRune(context, delta, mouseX, mouseY, handler);
                rightRune.drawRune(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        }); // Pg 3 : Luck

        add(new Page() { // Pg 4 : Swift sneak

            private final AltarDisplay altarDisplay = new AltarDisplay(
                "block.minecraft.sculk",
                "block.minecraft.sculk",
                "block.minecraft.sculk",
                "block.minecraft.sculk"
            );

            private final RuneDisplay leftRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.swift_sneak.echo_shard",
                lRuneX, lRuneY
            );

            private final RuneDisplay middleRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.swift_sneak.xp_tome",
                mRuneX, mRuneY
            );

            private final RuneDisplay rightRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.swift_sneak.echo_shard",
                rRuneX, rRuneY
            );

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
                leftRune.drawRune(context, delta, mouseX, mouseY, handler);
                middleRune.drawRune(context, delta, mouseX, mouseY, handler);
                rightRune.drawRune(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        }); // Pg 4 : Swift sneak

        add(new Page() { // Pg 5 : Soul speed

            private final AltarDisplay altarDisplay = new AltarDisplay(
                "block.minecraft.soul_sand",
                "block.minecraft.soul_sand",
                "block.minecraft.soul_sand",
                "block.minecraft.soul_sand"
            );

            private final RuneDisplay leftRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.soul_speed.wither_skull",
                lRuneX, lRuneY
            );

            private final RuneDisplay middleRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.soul_speed.lost_soul",
                mRuneX, mRuneY
            );

            private final RuneDisplay rightRune = new RuneDisplay(
                RuneDisplay.RuneType.SINGLE_ITEM,
                "spell.soul_speed.soul_sand",
                rRuneX, rRuneY
            );

            @Override
            public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
                context.drawTexture(BACKGROUND, handler.uiX, handler.uiY+offsetY, 0, 0, 256, 256);
                altarDisplay.drawBg(context, delta, mouseX, mouseY, handler);
                leftRune.drawRune(context, delta, mouseX, mouseY, handler);
                middleRune.drawRune(context, delta, mouseX, mouseY, handler);
                rightRune.drawRune(context, delta, mouseX, mouseY, handler);
            }

            @Override
            public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

            }
        }); // Pg 5 : Soul speed

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
        this.textRenderer2 = this.textRenderer;
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
        context.drawText(this.textRenderer, Text.translatable("gui.ethereal_enchanting.spell_book.pages", handler.page[0]+1, PAGES.size()), (uiX+70)*2, (uiY+160+offsetY)*2, WHITE, true);
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
