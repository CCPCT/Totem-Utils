package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "setScreen", at = @At("TAIL"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (!(screen instanceof InventoryScreen && ModConfig.get().snapOnTotem)) return;
        if (!TotemUtilsClient.popped) return;
        // cancel popped
        // no totem in inv or (totem on offhand and totem on hotbar(if hotbar not full))
        if (Logic.getSlotWithSpareTotem(0)<=8 ||
                (Logic.totemOnOffhand() && (Logic.getSlotWithItemInHotbar(Items.TOTEM_OF_UNDYING,0)>=0 ||
                        Logic.getEmptySlotInHotbar(0)==-1))){
            TotemUtilsClient.popped = false;
            return;
        }
        TotemUtilsClient.moveMouseToTotem = true;
    }
}
