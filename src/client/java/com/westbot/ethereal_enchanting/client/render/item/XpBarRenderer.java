package com.westbot.ethereal_enchanting.client.render.item;

import com.westbot.ethereal_enchanting.ModItems;
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
    private static final int bgEndX = 14;
    private static final int bgEndY = 15;


    public static void render(DrawContext context, ItemStack stack, int x, int y) {

        if (stack.isOf(ModItems.XP_TOME)) {

            Integer xp = stack.get(ModComponents.TOME_XP_POINTS);
            if (xp == null) return;

            int width = (int) (14 * Math.floor(xp/1628.0));

            Log.info(LogCategory.LOG, "width: " + width);

            if (width == 14) return;

            context.fill(RenderLayer.getGuiOverlay(), bgStartX+x, bgStartY+y, bgEndX+x, bgEndY+y, MathHelper.packRgb(0, 0, 0));

            if (width == 0) return;
            context.fill(RenderLayer.getGuiOverlay(), bgStartX+x, bgStartY+y, x+bgStartX+width, y+bgEndY-1, MathHelper.packRgb(10, 255, 40));
        }
    }

}
