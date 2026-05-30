package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Chat;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class OnTotemPopMixin {

    @Unique
    public int overlaytickleft = 0;
    @Unique
    public int counttickleft = 0;
    @Unique
    private int autoTotemDelay = -1;

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        // timer
        if (Logic.overlayactive){
            if (overlaytickleft <= 0 || (Logic.totemOnOffhand() && 5<=ModConfig.get().totemPopScreenDuration*20-overlaytickleft)){
                Logic.overlayactive = false;
            } else {
                overlaytickleft--;
            }
        } else {
            overlaytickleft = 0;
        }

        if (ModConfig.get().totemCountTime<0){
            Logic.totemCountActive = true;
            Logic.totemCountValue = Logic.getTotemCount(false);
        } else if (Logic.totemCountActive){
            if (counttickleft <= 0){
                Logic.totemCountActive = false;
            } else {
                counttickleft--;
            }
        }

        // auto totem trigger
        if (autoTotemDelay == 0 && ModConfig.get().autoTotem) {
            Logic.refillTotem();
        }
        if (autoTotemDelay>=0){
            autoTotemDelay--;
        }

    }

    @Inject(at = @At("TAIL"), method = "displayItemActivation")
    private void onTotemPop(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.is(Items.TOTEM_OF_UNDYING))
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
            autoTotemDelay = ModConfig.get().autoTotemDelay;
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
            overlaytickleft = ModConfig.get().totemPopScreenDuration*20;
            Logic.overlayactive = true;
        }

        // totem count
        if (ModConfig.get().totemCountTime>0){
            Logic.totemCountActive = true;
            counttickleft = ModConfig.get().totemCountTime * 20;
        }
    }
}