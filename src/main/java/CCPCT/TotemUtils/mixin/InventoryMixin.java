package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
public class InventoryMixin {
    @Mixin(HandledScreen.class)
    public abstract static class HandledScreenMixin<T extends ScreenHandler> {
        @Shadow
        protected int x;
        @Shadow
        protected int y;
        @Final
        @Shadow
        protected T handler;

        @Inject(method = "render", at = @At("TAIL"))
        private void onRender(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
            if (!TotemUtilsClient.moveMouseToTotem) return;
            TotemUtilsClient.moveMouseToTotem = false;

            MinecraftClient client = MinecraftClient.getInstance();
            double scale = client.getWindow().getScaleFactor();
            long window = client.getWindow().getHandle();
            int replanishTotem = Logic.getSlotWithSpareTotem(0);
            if (replanishTotem<=8) return;
            Slot totemSlot = handler.slots.get(replanishTotem);
            int totemX = x+totemSlot.x;
            int totemY = y+totemSlot.y;
            System.out.println(TotemUtilsClient.startX);
            System.out.println(totemX);

            GLFW.glfwSetCursorPos(window, (totemX+8)*scale, (totemY+8)*scale);

        }
    }
}


