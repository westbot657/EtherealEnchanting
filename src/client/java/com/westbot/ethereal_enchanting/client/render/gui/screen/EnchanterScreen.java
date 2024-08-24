package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.screen.EtherealEnchanterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class EnchanterScreen extends HandledScreen<EtherealEnchanterScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("ethereal_enchanting", "textures/gui/ethereal_enchanter.png");

    /*
     * xp bar on ui: 55,67
     * xp slider start pos: 54,66
     * xp slider end pos: 117,66
     * xp overlay: 1,167 (64x6)
     * xp pointer: 1,174 (4x14)
     * scroller: 6,174 (9x4)
     * scroll top: 140,5
     * scroll bottom: 140,47
     * pos1: 38,6
     * pos2: 38,24
     * pos3: 38,42
     * slot0: 17,25
     * slot1: 17,55
     * slot2: 143,55
     * slot overlay: 66,167 (100x18)
     */



    public EnchanterScreen(EtherealEnchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 186;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        int midX = (this.width - backgroundWidth) / 2;
        int midY = (this.height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, midX, midY, 0, 0, backgroundWidth, backgroundHeight);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }
}
