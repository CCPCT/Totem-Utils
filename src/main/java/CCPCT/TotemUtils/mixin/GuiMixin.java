package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;
import org.lwjgl.sdl.SDLMouse;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof InventoryScreen && ModConfig.get().snapOnTotem)) return;
        if (!TotemUtilsClient.popped) return;
        // cancel popped
        // no totem in inv or (totem on offhand and totem on hotbar(if hotbar not full))
        int totemIndex = Logic.getSlotWithSpareTotem(0);
        if (totemIndex<=8 || (Logic.totemOnOffhand() && (Logic.getSlotWithItemInHotbar(Items.TOTEM_OF_UNDYING,0)!=-1 ||
                        Logic.getEmptySlotInHotbar(0)==-1))){
            TotemUtilsClient.popped = false;
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        AbstractContainerMenu menu = minecraft.player.containerMenu;
        double scale = mc.getWindow().getGuiScale();

        Slot totemSlot = menu.slots.get(totemIndex);

        int left = (screen.width - 176) / 2;
        int top  = (screen.height - 166) / 2;

        double totem_mouseX = (left + totemSlot.x + 8) * scale;
        double totem_mouseY = (top + totemSlot.y + 8) * scale;

//        InputConstants.grabMouse(client.getWindow(), totem_mouseX, totem_mouseY);
//        InputConstants.releaseMouse(client.getWindow(), totem_mouseX, totem_mouseY);
        SDLMouse.SDL_WarpMouseInWindow(mc.getWindow().handle(), (float) totem_mouseX, (float) totem_mouseY);
    }
}
