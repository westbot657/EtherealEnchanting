package com.westbot.ethereal_enchanting.items;

import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class WrittenCipherItem extends Item {

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

    private static final String[] LETTERS = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};


    public WrittenCipherItem() {
        super(new Settings().component(ModComponents.CIPHER_LETTERS, "").maxCount(1));
    }

    public static void addLetter(ItemStack stack, String letter) {
        String letters = stack.get(ModComponents.CIPHER_LETTERS);
        if (letters == null) return;

        if (letters.contains(letter)) return;

        letters += letter;

        StringBuilder sorted = new StringBuilder();

        for (String l : LETTERS) {
            if (letters.contains(l)) {
                sorted.append(l);
            }
        }

        stack.set(ModComponents.CIPHER_LETTERS, sorted.toString());
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        String letters = stack.get(ModComponents.CIPHER_LETTERS);
        if (letters == null) return;

        StringBuilder line1 = new StringBuilder();
        StringBuilder line2 = new StringBuilder();
        StringBuilder line3 = new StringBuilder();
        StringBuilder line4 = new StringBuilder();

        int line = 0;

        boolean col = false;
        boolean missing = false;
        for (String l : LETTERS) {
            if (line1.length()/4 > 6 && line2.isEmpty()) {line = 1; col = true;}
            else if (line2.length()/4 > 6 && line3.isEmpty()) {line = 2; col = false;}
            else if (line3.length()/4 > 6 && line4.isEmpty()) {line = 3; col = true;}
            if (letters.contains(l)) {
                if (line == 0) {
                    line1.append(col ? "§7" : "§8").append(l).append(" ");
                } else if (line == 1) {
                    line2.append(col ? "§7" : "§8").append(l).append(" ");
                } else if (line == 2) {
                    line3.append(col ? "§7" : "§8").append(l).append(" ");
                } else {
                    line4.append(col ? "§7" : "§8").append(l).append(" ");
                }
            } else {
                missing = true;
            }
            col = !col;
        }

        if (!line1.isEmpty()) {
            tooltip.add(Text.literal(line1.toString()).setStyle(STYLE));
            tooltip.add(Text.literal(line1.toString()));
        }

        if (!line2.isEmpty()) {
            tooltip.add(Text.literal(line2.toString()).setStyle(STYLE));
            tooltip.add(Text.literal(line2.toString()));
        }

        if (!line3.isEmpty()) {
            tooltip.add(Text.literal(line3.toString()).setStyle(STYLE));
            tooltip.add(Text.literal(line3.toString()));
        }

        if (!line4.isEmpty()) {
            tooltip.add(Text.literal(line4.toString()).setStyle(STYLE));
            tooltip.add(Text.literal(line4.toString()));
        }

        if (missing) {
            tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.cipher.missing"));
        } else {
            tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.cipher.full"));
        }

    }

}
