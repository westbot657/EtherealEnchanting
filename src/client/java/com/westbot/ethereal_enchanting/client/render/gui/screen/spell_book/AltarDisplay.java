package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.mojang.blaze3d.systems.RenderSystem;
import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.Objects;


public class AltarDisplay implements Element {
    public static final Identifier GRADIENT = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/gradient.png");
    public static final Identifier WIREFRAME = Identifier.of("ethereal_enchanting", "textures/gui/spell_book/altar_wireframe.png");

    private SpellBookScreen handler;
    private String cipher = "";
    private Text block1SGA;
    private Text block1;
    private Text block2SGA;
    private Text block2;
    private Text block3SGA;
    private Text block3;
    private Text block4SGA;
    private Text block4;

    private int sgaw1;
    private int w1;
    private int sgaw2;
    private int w2;

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

    protected static int BLACK = MathHelper.packRgb(0,0,0);
    protected static int GRAY = MathHelper.packRgb(0.8f, 0.8f, 0.8f);
    protected static int WHITE = MathHelper.packRgb(1,1,1);

    private final String b1;
    private final String b2;
    private final String b3;
    private final String b4;

    public AltarDisplay(String b1, String b2, String b3, String b4) {
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        this.b4 = b4;
    }


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

    // right-aligned
    private static final int b1X = 20;
    private static final int b1Y = 394;
    private static final int b2X = 13;
    private static final int b2Y = 321;

    // left-aligned
    private static final int b3X = 502;
    private static final int b3Y = 323;
    private static final int b4X = 495;
    private static final int b4Y = 396;

    public void setup(SpellBookScreen handler, String block1key, String block2key, String block3key, String block4key) {
        if (this.handler == null || !this.cipher.equals(handler.handler.cipher)) {
            this.cipher = handler.handler.cipher;
            this.handler = handler;

            block1SGA = Text.translatable(block1key).setStyle(STYLE);
            block1 = getDecryptedText(block1SGA, handler);
            block2SGA = Text.translatable(block2key).setStyle(STYLE);
            block2 = getDecryptedText(block2SGA, handler);
            block3SGA = Text.translatable(block3key).setStyle(STYLE);
            block3 = getDecryptedText(block3SGA, handler);
            block4SGA = Text.translatable(block4key).setStyle(STYLE);
            block4 = getDecryptedText(block4SGA, handler);

            sgaw1 = (handler.textRenderer2.getWidth(block1SGA));
            w1 = (handler.textRenderer2.getWidth(block1));
            sgaw2 = (handler.textRenderer2.getWidth(block2SGA));
            w2 = (handler.textRenderer2.getWidth(block2));
        }
    }


    private static final float SCALE = 2.5f;
    private static final float FACTOR = 1.0f/SCALE;
    private static final int DELTA_X = (int) (64/SCALE);
    private static final int DELTA_Y = (int) (64/SCALE);
    private static final boolean SHADOW = true;
    @Override
    public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
        setup(handler, b1, b2, b3, b4);
        MatrixStack matrices = context.getMatrices();
        RenderSystem.enableBlend();
        matrices.push();
        matrices.scale(0.5f, 0.5f, 0.5f);
        context.drawTexture(GRADIENT, (handler.uiX)*2, (handler.uiY+16)*2, 0, 0, 512, 512, 512, 512);
        matrices.pop();

        matrices.push();
        matrices.scale(FACTOR, FACTOR, FACTOR);
        context.drawTexture(WIREFRAME, (int) ((handler.uiX+DELTA_X)*SCALE), (int) ((handler.uiY+DELTA_Y)*SCALE), 0, 0, 512, 512, 512, 512);
        RenderSystem.disableBlend();

        context.drawText(handler.textRenderer2, block1SGA, (int) ((handler.uiX+DELTA_X)*SCALE+b1X-sgaw1), (int) ((handler.uiY+DELTA_Y)*SCALE+b1Y), GRAY, SHADOW);
        context.drawText(handler.textRenderer2, block1, (int) ((handler.uiX+DELTA_X)*SCALE+b1X-w1), (int) ((handler.uiY+4+DELTA_Y)*SCALE+b1Y), WHITE, SHADOW);

        context.drawText(handler.textRenderer2, block2SGA, (int) ((handler.uiX+DELTA_X)*SCALE+b2X-sgaw2), (int) ((handler.uiY+DELTA_Y)*SCALE+b2Y), GRAY, SHADOW);
        context.drawText(handler.textRenderer2, block2, (int) ((handler.uiX+DELTA_X)*SCALE+b2X-w2), (int) ((handler.uiY+4+DELTA_Y)*SCALE+b2Y), WHITE, SHADOW);

        context.drawText(handler.textRenderer2, block3SGA, (int) ((handler.uiX+DELTA_X)*SCALE+b3X), (int) ((handler.uiY+DELTA_Y)*SCALE+b3Y), GRAY, SHADOW);
        context.drawText(handler.textRenderer2, block3, (int) ((handler.uiX+DELTA_X)*SCALE+b3X), (int) ((handler.uiY+4+DELTA_Y)*SCALE+b3Y), WHITE, SHADOW);

        context.drawText(handler.textRenderer2, block4SGA, (int) ((handler.uiX+DELTA_X)*SCALE+b4X), (int) ((handler.uiY+DELTA_Y)*SCALE+b4Y), GRAY, SHADOW);
        context.drawText(handler.textRenderer2, block4, (int) ((handler.uiX+DELTA_X)*SCALE+b4X), (int) ((handler.uiY+4+DELTA_Y)*SCALE+b4Y), WHITE, SHADOW);
        matrices.pop();
    }

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        return false;
    }
}



