package com.westbot.ethereal_enchanting.client.render.gui.screen.spell_book;

import com.westbot.ethereal_enchanting.EtherealEnchanting;
import com.westbot.ethereal_enchanting.client.render.gui.screen.SpellBookScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class TextLink implements Element {

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

    protected static int GRAY = MathHelper.packRgb(0.8f, 0.8f, 0.8f);
    protected static int WHITE = MathHelper.packRgb(1,1,1);

    protected static int BLUE1 = MathHelper.packRgb(0.03f, 0.03f, 0.9f);
    protected static int BLUE2 = MathHelper.packRgb(0.03f, 0.03f, 0.7f);

    private static final float SCALE = 1.4f;
    private static final float FACTOR = 1/SCALE;
    private boolean hovered = false;
    public int pageId;
    private String cipher = "";
    private final int x;
    private final int y;
    private int width;
    private final int height = (int) (14 * SCALE);
    private final Text textSGA;
    private Text text;
    private SpellBookScreen handler;

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

    public TextLink(int x, int y, String key, int pageId) {
        this.x = x;
        this.y = y;
        this.pageId = pageId;
        this.textSGA = Text.translatable(key).setStyle(STYLE);

    }

    @Override
    public void drawBg(DrawContext context, float delta, int mouseX, int mouseY, SpellBookScreen handler) {
        if (this.handler != handler || !handler.handler.cipher.equals(cipher)) {
            this.handler = handler;
            this.cipher = handler.handler.cipher;
            this.text = getDecryptedText(this.textSGA, handler);
            width = (int) (Math.max(this.handler.textRenderer2.getWidth(this.text), this.handler.textRenderer2.getWidth(this.textSGA)) * SCALE);
        }
        this.hovered = handler.collides((int) (x * FACTOR), (int) (y * FACTOR), (int) ((width* FACTOR)-1), (int) ((height * FACTOR)-1), mouseX, mouseY);
    }

    @Override
    public void draw(DrawContext context, int mouseX, int mouseY, float delta, SpellBookScreen handler) {
        if (this.text == null || this.textSGA == null) return;

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(FACTOR, FACTOR, FACTOR);
        if (this.hovered) {

            context.drawText(handler.textRenderer2, textSGA, (int) ((handler.uiX+x)*SCALE), (int) ((handler.uiY+y)), BLUE2, true);
            context.drawText(handler.textRenderer2, text, (int) ((handler.uiX+x)*SCALE), (int) ((handler.uiY+y+9)), BLUE1, true);

        } else {
            context.drawText(handler.textRenderer2, textSGA, (int) ((handler.uiX+x)*SCALE), (int) ((handler.uiY+y)), GRAY, true);
            context.drawText(handler.textRenderer2, text, (int) ((handler.uiX+x)*SCALE), (int) ((handler.uiY+y+9)), WHITE, true);

        }
        matrices.pop();
    }

    @Override
    public boolean onClick(double mouseX, double mouseY, int button) {
        if (hovered && button == 0) {
            handler.handler.page[0] = this.pageId;
            handler.playerInventory.player.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.5f, 1);
            return true;
        }
        return false;
    }
}
