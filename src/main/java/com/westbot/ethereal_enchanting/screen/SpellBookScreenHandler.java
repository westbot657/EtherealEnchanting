package com.westbot.ethereal_enchanting.screen;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;


public class SpellBookScreenHandler extends ScreenHandler implements InventoryChangedListener {

    private final PlayerInventory inventory;
    public String cipher = "";
    public int[] page = new int[]{0};

    public SpellBookScreenHandler(int syncId, PlayerInventory inventory) {
        super(ScreenHandlers.SPELL_BOOK, syncId);
        this.inventory = inventory;
        addProperty(Property.create(page, 0));
        buildCipher();
    }

    private void buildCipher() {
        this.cipher = "";

        for (int i = 0; i<inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);

            if (stack.isOf(ModItems.ENCHANTED_CIPHER)) {
                String c = stack.get(ModComponents.CIPHER_LETTERS);
                if (c == null) continue;

                for (String l : c.split("")) {
                    if (!cipher.contains(l)) {
                        cipher += l;
                    }
                }
            }
        }

        if (!cipher.isEmpty()) {
            cipher += "/,.!?-+|()&%<>*'\"";
        }
    }


    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        buildCipher();
    }
}
