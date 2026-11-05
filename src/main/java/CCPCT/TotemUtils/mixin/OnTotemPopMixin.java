package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Chat;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class OnTotemPopMixin {
    @Inject(at = @At("TAIL"), method = "displayItemActivation")
    private void onTotemPop(ItemStack itemStack, CallbackInfo ci) {
        if (!itemStack.is(Items.TOTEM_OF_UNDYING))
            return;

        Minecraft client = Minecraft.getInstance();

        LocalPlayer player = client.player;
        if (player == null)
            return;

        //confirm pop
        if (ModConfig.get().snapOnTotem) {
            TotemUtilsClient.popped = true;
        }

        // store last popped slot
        if (ModConfig.get().smartReplanishHotbar && (Logic.getMainhandStack().isEmpty()||Logic.getMainhandStack().getItem()==Items.TOTEM_OF_UNDYING)) {
            int slot = player.getInventory().getSelectedSlot();
            if (ModConfig.get().smartReplanishslot==-1 || ModConfig.get().smartReplanishslot==slot) {
                Logic.smartTotemSlot = slot;
            }
        }

        // auto totem
        if (ModConfig.get().autoTotem) {
            TotemUtilsClient.autoTotemDelay = ModConfig.get().autoTotemDelay;
        } else {
            Chat.send("§cPopped!", true);
        }

        // custom sound
        if (ModConfig.get().customSound) {
            Logic.stopTotemSound();
            Logic.playCustomSound();
        }

        // screen overlay
        if (ModConfig.get().totemPopScreen) {
            TotemUtilsClient.overlaytickleft = ModConfig.get().totemPopScreenDuration*20;
            Logic.overlayactive = true;
        }

        // totem count
        if (ModConfig.get().totemCountTime>0){
            Logic.totemCountActive = true;
            TotemUtilsClient.counttickleft = ModConfig.get().totemCountTime * 20;
            if (Logic.getSlotWithItemInHotbar(Items.TOTEM_OF_UNDYING, 0) == -1) {
                TotemUtilsClient.counttickleft = 20;
            }
        }
    }
}