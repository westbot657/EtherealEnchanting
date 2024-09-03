package com.westbot.ethereal_enchanting.client.mixin;

import com.westbot.ethereal_enchanting.client.accessors.HandledScreenMixinInterface;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> extends Screen implements ScreenHandlerProvider<T>, HandledScreenMixinInterface {

    @Unique
    public boolean ethereal_enchanting$hideInventoryTitle = false;


    protected HandledScreenMixin(Text title) {
        super(title);
    }


    @Inject(method="drawForeground", at=@At("HEAD"), cancellable = true)
    protected void wrapDrawInventoryTitle(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        if (this.ethereal_enchanting$hideInventoryTitle) {
            ci.cancel();
        }
    }

    @Unique
    public void enchantingRework$setHidden(boolean val) {
        this.ethereal_enchanting$hideInventoryTitle = val;
    }


}
