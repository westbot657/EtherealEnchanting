package com.westbot.ethereal_enchanting.client.render.item;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class XpBarRenderer {

    private static final int bgStartX = 2;
    private static final int bgStartY = 13;
    private static final int bgEndX = 15;
    private static final int bgEndY = 15;

    private static final int GREEN = MathHelper.packRgb(0.01f, 1f, 0.1f);
    private static final int YELLOW = MathHelper.packRgb(0.9f, 0.9f, 0.01f);
    private static final int RED = MathHelper.packRgb(0.9f, 0.01f, 0.01f);

    public static void render(DrawContext context, ItemStack stack, int x, int y) {

        if (stack.isOf(ModItems.XP_TOME)) {

            Integer xp = stack.get(ModComponents.TOME_XP_POINTS);
            if (xp == null) return;

            int lvl = 0;

            if (xp < 352) {
                lvl = (int) Math.sqrt(xp+9)-3;
            } else if (xp < 1507) {
                lvl = (int) ((81.0/10.0) + Math.sqrt((2.0/5.0)*(xp - (7839.0/40.0))));
            } else {
                lvl = 32;
            }


            int width = (int) ((bgEndX-bgStartX) * (lvl/32.0));

            if (width == (bgEndX-bgStartX)) return;

            context.fill(RenderLayer.getGuiOverlay(), bgStartX+x, bgStartY+y, bgEndX+x, bgEndY+y, -16777216);
            if (width == 0) return;

            int color = lvl >= 10 ? GREEN : lvl > 5 ? YELLOW : RED;

            context.fill(RenderLayer.getGuiOverlay(), bgStartX+x, bgStartY+y, x+bgStartX+width, y+bgEndY-1, -16777216|color);
        }
    }

}
