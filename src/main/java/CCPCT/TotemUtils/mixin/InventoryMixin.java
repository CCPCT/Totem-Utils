package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
public class InventoryMixin {
    @Mixin(MinecraftClient.class)
    public static class OnOpenInventory {
        @Inject(method = "setScreen", at = @At("TAIL"))
        private void onSetScreen(Screen screen, CallbackInfo ci) {
            if (!(screen instanceof InventoryScreen && ModConfig.get().snapOnTotem)) return;
            if (!TotemUtilsClient.popped) return;
            // cancel popped
            // no totem in inv or (totem on offhand and totem on hotbar(if hotbar not full))
            if (Logic.getSlotWithSpareTotem(0)<=8 || (Logic.totemOnOffhand() && (Logic.getSlotWithItemInHotbar(Items.TOTEM_OF_UNDYING,0)>=0 || Logic.getEmptySlotInHotbar(0)==-1))){
                TotemUtilsClient.popped = false;
                return;
            }
            TotemUtilsClient.moveMouseToTotem = true;
        }
    }

    @Mixin(HandledScreen.class)
    public abstract static class HandledScreenMixin<T extends ScreenHandler> {
        @Shadow
        protected int x;
        @Shadow
        protected int y;
        @Final
        @Shadow
        protected T handler;

        @Inject(method = "renderMain", at = @At("TAIL"))
        private void onRender(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
            if (!TotemUtilsClient.moveMouseToTotem) return;
            TotemUtilsClient.moveMouseToTotem = false;
            System.out.println("moved mouse to totem slot");

            MinecraftClient client = MinecraftClient.getInstance();
            double scale = client.getWindow().getScaleFactor();
            long window = client.getWindow().getHandle();
            int replenishTotem = Logic.getSlotWithSpareTotem(0);
            if (replenishTotem<=8) return;
            Slot totemSlot = handler.slots.get(replenishTotem);
            int totemX = x+totemSlot.x;
            int totemY = y+totemSlot.y;

            GLFW.glfwSetCursorPos(window, (totemX+8)*scale, (totemY+8)*scale);

        }
    }
}


