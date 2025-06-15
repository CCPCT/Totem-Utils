package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.util.totemlogic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import CCPCT.TotemUtils.config.ModConfig;

public class RenderOverlay {
    @Mixin(InGameHud.class)
    public static class InGameHudMixin {
        @Inject(method = "render", at = @At("TAIL"))
        private void onRender(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (totemlogic.overlayactive) {
                int width = client.getWindow().getScaledWidth();
                int height = client.getWindow().getScaledHeight();
                int argb = (ModConfig.get().totemPopScreenAlpha << 24) | ModConfig.get().totemPopScreenColour;
                int centerX = width / 2;
                int centerY = height / 2;
                int holeHeight = height - ModConfig.get().totemPopScreenWidth;
                int holeWidth = width - ModConfig.get().totemPopScreenWidth;
                // Draw top of screen to above the hole
                context.fill(0, 0, width, centerY - holeHeight / 2, argb);

                // Draw bottom of screen to below the hole
                context.fill(0, centerY + holeHeight / 2, width, height, argb);

                // Draw left of screen to left of hole
                context.fill(0, centerY - holeHeight / 2, centerX - holeWidth / 2, centerY + holeHeight / 2, argb);

                // Draw right of hole to end of screen
                context.fill(centerX + holeWidth / 2, centerY - holeHeight / 2, width, centerY + holeHeight / 2, argb);
            }
            if (totemlogic.totemCountActive || ModConfig.get().totemCountTime == -1){
                int argb = (ModConfig.get().totemCountAlpha << 24) | ModConfig.get().totemCountColour;
                context.drawText(client.textRenderer, String.valueOf(totemlogic.getTotemCount()), ModConfig.get().totemCountx, ModConfig.get().totemCounty, argb, true);
            }
        }
    }
}


