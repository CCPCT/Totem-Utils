package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Logic;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class InGameHudMixin {
    @Unique
    int totemCount = 0;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor context, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (Logic.overlayactive) {
            int width = TotemUtilsClient.RenderHelper.width;
            int height = TotemUtilsClient.RenderHelper.height;
            int argb = TotemUtilsClient.RenderHelper.argb;
            int centerX = TotemUtilsClient.RenderHelper.centerX;
            int centerY = TotemUtilsClient.RenderHelper.centerY;
            int holeHeight = TotemUtilsClient.RenderHelper.holeHeight;
            int holeWidth = TotemUtilsClient.RenderHelper.holeWidth;

            // top
            context.fill(0, 0, width, centerY - holeHeight / 2, argb);
            // bottom
            context.fill(0, centerY + holeHeight / 2, width, height, argb);
            // left
            context.fill(0, centerY - holeHeight / 2, centerX - holeWidth / 2, centerY + holeHeight / 2, argb);
            // right
            context.fill(centerX + holeWidth / 2, centerY - holeHeight / 2, width, centerY + holeHeight / 2, argb);
        }
        if (Logic.totemCountActive || ModConfig.get().totemCountTime == -1){
            Minecraft client = Minecraft.getInstance();
            int argb = (ModConfig.get().totemCountAlpha << 24) | ModConfig.get().totemCountColour;
            context.text(client.font, String.valueOf(totemCount), ModConfig.get().totemCountx, ModConfig.get().totemCounty, argb, true);
        }
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void onTick(CallbackInfo ci){
        if (Logic.totemCountActive) {
            totemCount = Logic.getTotemCount(true);
        }
        if (Logic.overlayactive) {
            Window window = Minecraft.getInstance().getWindow();
            TotemUtilsClient.RenderHelper.width = window.getWidth();
            TotemUtilsClient.RenderHelper.height = window.getHeight();
        }
    }
}



