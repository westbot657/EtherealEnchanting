package com.westbot.ethereal_enchanting.items;

import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class WrittenRuneItem extends Item {

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

    private static final String[] letters = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    public WrittenRuneItem() {
        super(new Settings().maxCount(16).component(ModComponents.RUNE_LETTER, ""));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()) {
            if (Objects.equals(stack.get(ModComponents.RUNE_LETTER), "")) {
                Random random = world.getRandom();
                String letter = letters[random.nextInt(letters.length)];
                setLetter(stack, letter);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        if (stack.contains(ModComponents.RUNE_LETTER)) {
            String letter = stack.get(ModComponents.RUNE_LETTER);
            if (letter == null || Objects.equals(letter, "")) {
                return;
            }

            tooltip.add(Text.literal(letter.toLowerCase()).fillStyle(STYLE).formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.literal(letter).formatted(Formatting.LIGHT_PURPLE));
            tooltip.add(Text.translatable("itemTooltip.ethereal_enchanting.written_rune_tooltip_line2").formatted(Formatting.GRAY));
        }

    }

    public String getLetter(ItemStack stack) {
        if (stack.contains(ModComponents.RUNE_LETTER)) {
            return stack.get(ModComponents.RUNE_LETTER);
        }
        return null;
    }


    public void setLetter(ItemStack stack, String letter) {
        if (stack.contains(ModComponents.RUNE_LETTER)) {
            stack.set(ModComponents.RUNE_LETTER, letter);
        }
    }


}
