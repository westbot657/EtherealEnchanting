package com.westbot.ethereal_enchanting.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>, HandledScreenMixinInterface {

    @Unique
    public boolean ethereal_enchanting$hideInventoryTitle = false;


    protected HandledScreenMixin(Text title) {
        super(title);
    }


    @WrapOperation(method="drawForeground", at= @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", ordinal = 1))
    protected int wrapDrawInventoryTitle(DrawContext instance, TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, Operation<Integer> original) {

        if (this.ethereal_enchanting$hideInventoryTitle) {
            return 0;
        }
        return original.call(instance, textRenderer, text, x, y, color, shadow);
    }

    @Unique
    public void enchantingRework$setHidden(boolean val) {
        this.ethereal_enchanting$hideInventoryTitle = val;
    }


}
