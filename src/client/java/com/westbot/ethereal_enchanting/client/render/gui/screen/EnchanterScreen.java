package com.westbot.ethereal_enchanting.client.render.gui.screen;

import com.westbot.ethereal_enchanting.ModItems;
import com.westbot.ethereal_enchanting.Util;
import com.westbot.ethereal_enchanting.data_components.EtherealEnchantComponent;
import com.westbot.ethereal_enchanting.data_components.ModComponents;
import com.westbot.ethereal_enchanting.enchantments.EnchantmentManager;
import com.westbot.ethereal_enchanting.items.XPTomeItem;
import com.westbot.ethereal_enchanting.screen.EtherealEnchanterScreenHandler;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.westbot.ethereal_enchanting.enchantments.EnchantmentManager.*;

public class EnchanterScreen extends HandledScreen<EtherealEnchanterScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of("ethereal_enchanting", "textures/gui/ethereal_enchanter.png");

    private static final Identifier FONT_ID = Identifier.ofVanilla("alt");
    private static final Style STYLE = Style.EMPTY.withFont(FONT_ID);

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

    private int xpSliderX = 0;
    private int scrollY = 0;
    private int scrollMax = 44;
    private int xpSliderMax = 64;
    private boolean holdingXpSlider = false;
    private boolean holdingScroller = false;
    private boolean hoverXpSlider = false;
    private boolean hoverScroller = false;
    private int uiX = 0;
    private int uiY = 0;

    private static final int RUNE_COLOR = ColorHelper.Argb.getArgb(60, 0, 0, 0);
    private static final int DECRYPT_COLOR = ColorHelper.Argb.getArgb(200, 0, 0, 0);

    private Text slot1Title;
    private Text slot2Title;
    private Text slot3Title;

    private Text slot1Lore;
    private Text slot2Lore;
    private Text slot3Lore;

    private Text slot1LoreSGA;
    private Text slot2LoreSGA;
    private Text slot3LoreSGA;

    private Text slot1DescriptionSGA;
    private Text slot2DescriptionSGA;
    private Text slot3DescriptionSGA;

    private Text slot1Description;
    private Text slot2Description;
    private Text slot3Description;

    private final PlayerInventory playerInventory;

    public EnchanterScreen(EtherealEnchanterScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        backgroundWidth = 176;
        backgroundHeight = 186;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        this.playerInventory = inventory;
        refreshEnchants();
    }

    public void refreshEnchants() {
        int index = handler.scrollY[0];

        if ((!holdingScroller) && handler.enchantList.size() - 3 > 0) {
            int s = Math.max(handler.enchantList.size() - 3, 1);
            scrollY = Math.clamp((long) (scrollMax / s) * index, 0, scrollMax);
        }

        String cipher = "";
        ItemStack stack = handler.getSlot(3).getStack();
        if (!stack.isEmpty() && stack.isOf(ModItems.ENCHANTED_CIPHER)) {
            cipher = stack.get(ModComponents.CIPHER_LETTERS);
            assert cipher != null;
            cipher += ",':.()?!\"/|";
        }

        if (handler.enchantList.size() > index) {
            EtherealEnchantComponent enchant = handler.enchantList.get(index);
            slot1Title = Text.literal(
                Text.translatable(
                    "tooltip.ethereal_enchanting.enchant." + enchant.enchant(),
                        (Objects.equals(enchant.level(), MAX_LEVELS.get(enchant.enchant())) ? MAX_LEVEL : LOWER_LEVEL) +
                            ROMAN_NUMERALS.getOrDefault(
                                enchant.level(),
                                ""+enchant.level()
                            ) + "§r"
                    )
                    .getString().strip()
            ).formatted(
                EnchantmentManager.ENCHANT_ORDER.get(enchant.enchant())
            );
            slot1LoreSGA = Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant())
                .setStyle(STYLE);
            StringBuilder loreText = new StringBuilder();
            for (String c : Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant()).getString().split("")) {
                loreText.append(cipher.contains(c) ? c : cipher.contains(c.toLowerCase()) ? c : " ");
            }
            slot1Lore = Text.literal(loreText.toString());
        }
        if (handler.enchantList.size() > index + 1) {
            EtherealEnchantComponent enchant = handler.enchantList.get(index+1);
            slot2Title = Text.literal(
                Text.translatable(
                        "tooltip.ethereal_enchanting.enchant." + enchant.enchant(),
                        (Objects.equals(enchant.level(), MAX_LEVELS.get(enchant.enchant())) ? MAX_LEVEL : LOWER_LEVEL) +
                            ROMAN_NUMERALS.getOrDefault(
                                enchant.level(),
                                ""+enchant.level()
                            ) + "§r"
                    )
                    .getString().strip()
            ).formatted(
                EnchantmentManager.ENCHANT_ORDER.get(enchant.enchant())
            );
            slot2LoreSGA = Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant())
                .setStyle(STYLE);
            StringBuilder loreText = new StringBuilder();
            for (String c : Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant()).getString().split("")) {
                loreText.append(cipher.contains(c) ? c : cipher.contains(c.toLowerCase()) ? c : " ");
            }
            slot2Lore = Text.literal(loreText.toString());
        }
        if (handler.enchantList.size() > index + 2) {
            EtherealEnchantComponent enchant = handler.enchantList.get(index+2);
            slot3Title = Text.literal(
                Text.translatable(
                        "tooltip.ethereal_enchanting.enchant." + enchant.enchant(),
                        (Objects.equals(enchant.level(), MAX_LEVELS.get(enchant.enchant())) ? MAX_LEVEL : LOWER_LEVEL) +
                            ROMAN_NUMERALS.getOrDefault(
                                enchant.level(),
                                ""+enchant.level()
                            ) + "§r"
                    )
                    .getString().strip()
            ).formatted(
                EnchantmentManager.ENCHANT_ORDER.get(enchant.enchant())
            );
            slot3LoreSGA = Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant())
                .setStyle(STYLE);
            StringBuilder loreText = new StringBuilder();
            for (String c : Text.translatable("lore.ethereal_enchanting.enchant." + enchant.enchant()).getString().split("")) {
                loreText.append(cipher.contains(c) ? c : cipher.contains(c.toLowerCase()) ? c : " ");
            }
            slot3Lore = Text.literal(loreText.toString());
        }

        ItemStack tome = handler.getSlot(1).getStack();

        if (!tome.isEmpty() && tome.isOf(ModItems.XP_TOME)) {
            int xp = XPTomeItem.getXP(tome);
            xpSliderX = ((int) (Util.getXpLevelFromPoints(xp)*2));

        } else {
            xpSliderX = 0;
        }


    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.refreshEnchants();
        boolean c = super.mouseClicked(mouseX, mouseY, button);

        if (hoverScroller && button == 0) {
            holdingScroller = true;
        }
        if (hoverXpSlider && button == 0) {
            holdingXpSlider = true;
        }
        return c;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.refreshEnchants();
        if (button == 0) {
            holdingScroller = false;
            holdingXpSlider = false;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        if (holdingXpSlider) {
            int x = xpSliderX;
            xpSliderX = (int) Math.clamp(
                mouseX - (uiX+53),
                0,
                xpSliderMax
            );

            if (x != xpSliderX) {
                ItemStack tome = handler.getSlot(1).getStack();

                if ((!tome.isEmpty()) && tome.isOf(ModItems.XP_TOME)) {
                    int xpOld = Util.getXpPointsFromLevel(x/2.0);
                    int xp = Util.getXpPointsFromLevel(xpSliderX/2.0);

                    int delta = Math.abs(xp-xpOld);

                    // delta is guaranteed to always be available from the
                    // tome when moving to the player
                    if (!playerInventory.player.isCreative()) {
                        if (xp > xpOld) {
                            if (handler.getPlayerXp() > delta) {
                                handler.setPlayerXp(
                                    handler.getPlayerXp() - delta
                                );
                            } else {
                                xpSliderX = x; // cap the position of the xp bar
                                return;
                            }
                        } else {
                            handler.setPlayerXp(
                                handler.getPlayerXp() + delta
                            );
                        }
                    }

                    // TODO: fix desync from this code only happening client-side

                    XPTomeItem.setXP(tome, xp);

                }


            }

        }

        if (holdingScroller) {
            int ly = scrollY;
            scrollY = (int) Math.clamp(
                mouseY - (uiY+14),
                0,
                scrollMax
            );

            // scrollY = Math.clamp((long) (scrollMax / handler.enchantList.size()) * index, 0, scrollMax);
            if (handler.enchantList.size() -3 > 0 && ly != scrollY) {
                int s = Math.max(handler.enchantList.size() - 3, 1);
                handler.scrollY[0] = Math.clamp((scrollY / (scrollMax / s)), 0, s);
                refreshEnchants();
            }
        }

    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {

        if (super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) {
            return true;
        } else {
            if (verticalAmount > 0) {
                handler.scrollUp();
                this.refreshEnchants();
                return true;
            } else {
                handler.scrollDown();
                this.refreshEnchants();
                return true;
            }
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        uiX = (this.width - backgroundWidth) / 2;
        uiY = (this.height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, uiX, uiY, 0, 0, backgroundWidth, backgroundHeight);

        if (xpSliderX > 0) {
            context.drawTexture(TEXTURE, uiX + 55, uiY + 77, 1, 187, xpSliderX, 6);
        }

        hoverXpSlider = isPointWithinBounds(53 + xpSliderX, 76, 4, 14, mouseX, mouseY);
        hoverScroller = isPointWithinBounds(139, 14+scrollY, 9, 4, mouseX, mouseY);

        if (hoverXpSlider || holdingXpSlider) {
            context.drawTexture(TEXTURE, uiX+53+xpSliderX, uiY+76, 16, 194, 4, 14);
        } else {
            context.drawTexture(TEXTURE, uiX+53+xpSliderX, uiY+76, 1, 194, 4, 14);
        }

        if (hoverScroller || holdingScroller) {
            context.drawTexture(TEXTURE, uiX+139, uiY+14+scrollY, 6, 199, 9, 4);
        } else {
            context.drawTexture(TEXTURE, uiX+139, uiY+14+scrollY, 6, 194, 9, 4);
        }


        int index = handler.scrollY[0];
        if (handler.enchantList.size() > index && slot1Title != null && slot1LoreSGA != null && slot1Lore != null) {
            context.drawTexture(TEXTURE, uiX+38, uiY+16, 66, 187, 100, 18);
            EtherealEnchantComponent enchant = handler.enchantList.get(index);
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            context.drawText(this.textRenderer, this.slot1Title, (uiX+40)*2, (uiY+18)*2, MathHelper.packRgb(0, 0, 0), true);
            context.drawTextWrapped(this.textRenderer, this.slot1LoreSGA, (uiX+40)*2, (uiY+23)*2, 195, RUNE_COLOR);
            context.drawTextWrapped(this.textRenderer, this.slot1Lore, (uiX+40)*2, (uiY+24)*2, 195, DECRYPT_COLOR);
            matrices.pop();
        }
        if (handler.enchantList.size() > index+1 && slot2Title != null && slot2LoreSGA != null && slot2Lore != null) {
            context.drawTexture(TEXTURE, uiX+38, uiY+34, 66, 187, 100, 18);
            EtherealEnchantComponent enchant = handler.enchantList.get(index+1);
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            context.drawText(this.textRenderer, this.slot2Title, (uiX+40)*2, (uiY+36)*2, MathHelper.packRgb(0, 0, 0), true);
            context.drawTextWrapped(this.textRenderer, this.slot2LoreSGA, (uiX+40)*2, (uiY+41)*2, 195, RUNE_COLOR);
            context.drawTextWrapped(this.textRenderer, this.slot2Lore, (uiX+40)*2, (uiY+42)*2, 195, DECRYPT_COLOR);
            matrices.pop();
        }
        if (handler.enchantList.size() > index+2 && slot3Title != null && slot3LoreSGA != null && slot3Lore != null) {
            context.drawTexture(TEXTURE, uiX+38, uiY+52, 66, 187, 100, 18);
            EtherealEnchantComponent enchant = handler.enchantList.get(index+2);
            MatrixStack matrices = context.getMatrices();
            matrices.push();
            matrices.scale(0.5f, 0.5f, 0.5f);
            context.drawText(this.textRenderer, this.slot3Title, (uiX+40)*2, (uiY+54)*2, MathHelper.packRgb(0, 0, 0), true);
            context.drawTextWrapped(this.textRenderer, this.slot3LoreSGA, (uiX+40)*2, (uiY+59)*2, 195, RUNE_COLOR);
            context.drawTextWrapped(this.textRenderer, this.slot3Lore, (uiX+40)*2, (uiY+60)*2, 195, DECRYPT_COLOR);
            matrices.pop();
        }


    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        if (focusedSlot != null && !focusedSlot.hasStack()) {
            List<Text> hover_text = new ArrayList<>();
            if (focusedSlot.id == 3) {
                hover_text.add(Text.translatable("gui.ethereal_enchanting.enchanter.cipher_slot.hover"));
            } else if (focusedSlot.id == 2) {
                hover_text.add(Text.translatable("gui.ethereal_enchanting.enchanter.battery_slot.hover1"));
                hover_text.add(Text.translatable("gui.ethereal_enchanting.enchanter.battery_slot.hover2"));
            } else if (focusedSlot.id == 1) {
                hover_text.add(Text.translatable("gui.ethereal_enchanting.enchanter.xp_tome.hover"));
            }

            if (!hover_text.isEmpty()) {
                context.drawTooltip(this.textRenderer, hover_text, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        if (handler.dirty) {
            refreshEnchants();
            handler.dirty = false;
        }
    }
}
