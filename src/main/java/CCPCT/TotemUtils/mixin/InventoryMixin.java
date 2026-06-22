package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public abstract class InventoryMixin<T extends AbstractContainerMenu> {
    @Shadow
    protected int leftPos;
    @Shadow
    protected int topPos;
    @Final
    @Shadow
    protected T menu;

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void onRender(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
        if (!TotemUtilsClient.moveMouseToTotem) return;
        TotemUtilsClient.moveMouseToTotem = false;
        System.out.println("moved mouse to totem slot");

        Minecraft client = Minecraft.getInstance();
        double scale = client.getWindow().getGuiScale();
        long window = client.getWindow().handle();
        int replenishTotem = Logic.getSlotWithSpareTotem(0);
        if (replenishTotem <= 8) return;
        Slot totemSlot = menu.slots.get(replenishTotem);
        int totemX = leftPos + totemSlot.x;
        int totemY = topPos + totemSlot.y;

        GLFW.glfwSetCursorPos(window, (totemX + 8) * scale, (totemY + 8) * scale);

    }
}



