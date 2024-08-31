package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class RuneDisplay implements Element {

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

    protected static int BLACK = MathHelper.packRgb(0,0,0);
    protected static int GRAY = MathHelper.packRgb(0.8f, 0.8f, 0.8f);
    protected static int WHITE = MathHelper.packRgb(1,1,1);


    // Textures are 40x40
    public enum RuneType {
        SINGLE_ITEM("rune1"),
        DOUBLE_ITEM("rune2"),
        TRIPLE_ITEM("rune3"),
        QUAD_ITEM("rune4"),

        SEQUENCE_0("rune0"),
        SEQUENCE_1("rune-1"),
        SEQUENCE_2("rune-2"),

        EITHER("rune2alt"),
        SCALE_3("rune3scale"),
        SCALE_4("rune4scale");

        public final Identifier texture;

        RuneType(String texture) {
            this.texture = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/runes/" + texture + ".png");
        }
    }

    public RuneType type;
    public Text t1SGA;
    public Text t1;
    public Text t2SGA;
    public Text t2;
    public Text t3SGA;
    public Text t3;
    public Text t4SGA;
    public Text t4;

    public int x;
    public int y;
    public String cipher = "";
    private SpellBookScreen handler;

    private int w1SGA = 0;
    private int w2SGA = 0;
    private int w3SGA = 0;
    private int w4SGA = 0;
    private int w1 = 0;
    private int w2 = 0;
    private int w3 = 0;
    private int w4 = 0;

    public Text getDecryptedText(Text sgaText, com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen handler) {
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

    public void setup(SpellBookScreen handler) {
        if (this.handler == null || !this.cipher.equals(handler.handler.cipher)) {
            this.cipher = handler.handler.cipher;
            this.handler = handler;

            t1 = getDecryptedText(t1SGA, handler);
            t2 = getDecryptedText(t2SGA, handler);
            t3 = getDecryptedText(t3SGA, handler);
            t4 = getDecryptedText(t4SGA, handler);

        }
    }

    public RuneDisplay(RuneType type, String t1, String t2, String t3, String t4, int x, int y) {
        this.type = type;
        this.t1SGA = Text.translatable(t1).setStyle(STYLE);
        this.t2SGA = Text.translatable(t2).setStyle(STYLE);
        this.t3SGA = Text.translatable(t3).setStyle(STYLE);
        this.t4SGA = Text.translatable(t4).setStyle(STYLE);
        this.x = x;
        this.y = y;
    }

    public void drawRune(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
        setup(handler);

        RenderSystem.enableBlend();
        context.drawTexture(type.texture, handler.uiX+x, handler.uiY+y, 0, 0, 40, 40, 40, 40);
        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);

        switch (type) {
            case SINGLE_ITEM -> {
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1SGA, (handler.uiX+x+20)*2, (handler.uiY+y+19)*2, GRAY);
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1, (handler.uiX+x+20)*2, (handler.uiY+y+23)*2, WHITE);
            }
            case DOUBLE_ITEM -> {
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1SGA, (handler.uiX+x+20)*2, (handler.uiY+y+5)*2, GRAY);
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1, (handler.uiX+x+20)*2, (handler.uiY+y+9)*2, WHITE);

                context.drawCenteredTextWithShadow(handler.textRenderer2, t2SGA, (handler.uiX+x+20)*2, (handler.uiY+y+35)*2, GRAY);
                context.drawCenteredTextWithShadow(handler.textRenderer2, t2, (handler.uiX+x+20)*2, (handler.uiY+y+39)*2, WHITE);
            }
            case TRIPLE_ITEM -> {
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1SGA, (handler.uiX+x+20)*2, (handler.uiY+y+5)*2, GRAY);
                context.drawCenteredTextWithShadow(handler.textRenderer2, t1, (handler.uiX+x+20)*2, (handler.uiY+y+9)*2, WHITE);

            }
            case QUAD_ITEM -> {}
            case SEQUENCE_0 -> {}
            case SEQUENCE_1 -> {}
            case SEQUENCE_2 -> {}
            case SCALE_3 -> {}
            case SCALE_4 -> {}
            case EITHER -> {}
        }

        matrices.pop();
        RenderSystem.disableBlend();
    }

    @Override
    public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
        drawRune(context, delta, mouseX, mouseY, handler);
    };

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

    }
}
