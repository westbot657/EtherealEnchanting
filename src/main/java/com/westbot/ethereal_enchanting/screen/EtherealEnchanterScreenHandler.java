package com.westbot.ethereal_enchanting.screen;

import com.jcraft.jorbis.Block;
import com.westbot.ethereal_enchanting.EtherealEnchanting;
import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.blocks.ModBlocks;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class EtherealEnchanterScreenHandler extends ScreenHandler implements InventoryChangedListener {

    private final SimpleInventory inventory;
    private final PlayerInventory playerInventory;
    private final ScreenHandlerContext context;
    public final BlockPos pos;


    public final List<EtherealEnchantComponent> enchantList;
    public final int[] scrollY = new int[]{0};

    // [0]: dirty flag, [1]: player xp, [2]: tome xp
    public final int[] xp_tome_properties = new int[]{0, 0, 0};

    public ItemStack lastStack = ItemStack.EMPTY;
    public boolean dirty = false;


    public EtherealEnchanterScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, ScreenHandlerContext.EMPTY, new SimpleInventory(4), null);
    }

    /*
     * xp bar on ui: 55,77
     * xp slider start pos: 54,76
     * xp slider end pos: 117,76
     * xp overlay: 1,187          (64x6)
     * xp pointer: 1,194          (4x14)
     * xp pointer highlighted: 16,194
     * scroller: 6,194            (9x4)
     * scroller highlighted: 6,199
     * scroll top: 140,15
     * scroll bottom: 140,57
     * pos1: 38,16
     * pos2: 38,34
     * pos3: 38,52
     * slot0: 17,35
     * slot1: 17,65
     * slot2: 143,65
     * slot overlay: 66,187       (100x18)
     * cipher slot: 152,16
     */

    public void scrollUp() {
        int i = scrollY[0];
        scrollY[0] = Math.max(0, scrollY[0]-1);
    }

    public void scrollDown() {
        int i = scrollY[0];
        scrollY[0] = Math.min(scrollY[0]+1, Math.max(enchantList.size()-3, 0));
    }

    public EtherealEnchanterScreenHandler(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, SimpleInventory blockInventory, BlockPos pos) {
        super(ScreenHandlers.ENCHANTER, syncId);
        this.context = context;
        this.inventory = blockInventory;
        this.pos = pos;
        this.inventory.addListener(this);
        this.enchantList = new ArrayList<>();
        this.playerInventory = playerInventory;

        // SLOT 0: enchantment manager slot
        this.addSlot(new Slot(this.inventory, 0, 17, 35) {
            @Override
            public int getMaxItemCount() { return 1; }
        });

        // SLOT 1: xp tome manager
        this.addSlot(new Slot(this.inventory, 1, 17, 65) {
            @Override
            public int getMaxItemCount() { return 1; }
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.XP_TOME);
            }
        });

        // SLOT 2: xp tome battery
        this.addSlot(new Slot(this.inventory, 2, 143, 65) {
            @Override
            public int getMaxItemCount() { return 1; }
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.XP_TOME);
            }
        });

        // SLOT 3: enchanted cipher
        this.addSlot(new Slot(this.inventory, 3, 152, 16) {
            @Override
            public int getMaxItemCount() { return 1; }
            @Override
            public boolean canInsert(ItemStack stack) {
                return stack.isOf(ModItems.ENCHANTED_CIPHER) || stack.isOf(ModItems.WRITTEN_CIPHER);
            }
        });


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));

            }
        }

        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 162));
        }

        this.addProperty(Property.create(this.scrollY, 0));
        this.addProperty(Property.create(this.xp_tome_properties, 0));
        this.addProperty(Property.create(this.xp_tome_properties, 1));
        this.addProperty(Property.create(this.xp_tome_properties, 2));

    }

    public int getPlayerXp() {
        return playerInventory.player.totalExperience;
    }

    public void setPlayerXp(int xp) {
        playerInventory.player.totalExperience = xp;
    }

    @Override
    public boolean onButtonClick(PlayerEntity player, int id) {
        return super.onButtonClick(player, id);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasStack()) {
            ItemStack itemStack2 = slot.getStack();
            itemStack = itemStack2.copy();
            if (slotIndex == 0) {
                if (!this.insertItem(itemStack2, 4, 40, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex == 1) {
                if (!this.insertItem(itemStack2, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex == 2) {
                if (!this.insertItem(itemStack2, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex == 3) {
                if (!this.insertItem(itemStack2, 4, 40, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.isOf(ModItems.XP_TOME)) {
                if (!this.insertItem(itemStack2, 1, 2, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (itemStack2.isOf(ModItems.ENCHANTED_CIPHER) || itemStack2.isOf(ModItems.WRITTEN_CIPHER)) {
                if (!this.insertItem(itemStack2, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.getFirst().hasStack() || !this.slots.getFirst().canInsert(itemStack2)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.decrement(1);
                this.slots.getFirst().setStack(itemStack3);
            }

            if (itemStack2.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTakeItem(player, itemStack2);

        }

        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return ScreenHandler.canUse(this.context, player, ModBlocks.ETHEREAL_ENCHANTER_BLOCK);
    }

    @Override
    public void onContentChanged(Inventory inventory) {
        if (inventory == this.inventory) {
            dirty = true;
            ItemStack stack = inventory.getStack(0);
            if (stack != lastStack) {
                lastStack = stack;
                enchantList.clear();
                if (stack != ItemStack.EMPTY) {
                    List<EtherealEnchantComponent> enchants = stack.get(ModComponents.ETHEREAL_ENCHANTS);
                    if (enchants == null) return;
                    enchantList.addAll(enchants);
                }
            }
        }
    }


    @Override
    public void onClosed(PlayerEntity player) {
        this.inventory.removeListener(this);
        super.onClosed(player);
    }

    @Override
    public void onInventoryChanged(Inventory sender) {
        this.onContentChanged(sender);
    }



}
