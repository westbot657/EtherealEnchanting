package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;


public class AltarDisplay implements Element {


    private SpellBookScreen handler;
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
    private static final int b1X = 40/2;
    private static final int b1Y = 262/2;
    private static final int b2X = 33/2;
    private static final int b2Y = 186/2;

    // left-aligned
    private static final int b3X = 522/2;
    private static final int b3Y = 186/2;
    private static final int b4X = 515/2;
    private static final int b4Y = 262/2;

    public void setup(SpellBookScreen handler, String block1key, String block2key, String block3key, String block4key) {
        if (this.handler == null) {
            this.handler = handler;

            block1SGA = Text.translatable(block1key).setStyle(STYLE);
            block1 = getDecryptedText(block1SGA, handler);
            block2SGA = Text.translatable(block2key).setStyle(STYLE);
            block2 = getDecryptedText(block2SGA, handler);
            block3SGA = Text.translatable(block3key).setStyle(STYLE);
            block3 = getDecryptedText(block3SGA, handler);
            block4SGA = Text.translatable(block4key).setStyle(STYLE);
            block4 = getDecryptedText(block4SGA, handler);

            sgaw1 = (int) (handler.textRenderer.getWidth(block1SGA)/1.1);
            w1 = (int) (handler.textRenderer.getWidth(block1)/1.1);
            sgaw2 = (int) (handler.textRenderer.getWidth(block2SGA)/1.1);
            w2 = (int) (handler.textRenderer.getWidth(block2)/1.1);
        }
    }

    @Override
    public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
        setup(handler, b1, b2, b3, b4);
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

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {

    }
}



