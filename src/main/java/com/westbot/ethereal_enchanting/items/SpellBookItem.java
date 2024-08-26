package com.westbot.ethereal_enchanting.items;

import com.westbot.ethereal_enchanting.screen.SpellBookScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SpellBookItem extends Item {
    public SpellBookItem() {
        super(new Settings().maxCount(1).fireproof());
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.openHandledScreen(
            new SimpleNamedScreenHandlerFactory(
                (syncId, inventory, player) -> new SpellBookScreenHandler(syncId, inventory),
                Text.translatable("gui.ethereal_enchanting.spell_book.title")
            )
        );
        return TypedActionResult.consume(user.getStackInHand(hand));
    }
}