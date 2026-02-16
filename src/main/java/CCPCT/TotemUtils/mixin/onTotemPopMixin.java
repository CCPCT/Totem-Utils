package CCPCT.TotemUtils.mixin;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.Chat;
import CCPCT.TotemUtils.util.Logic;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class onTotemPopMixin {

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
        }

        if (ModConfig.get().totemCountTime<0){
            Logic.totemCountActive = true;
            Logic.totemCountValue = Logic.getTotemCount();
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

    @Inject(at = @At("TAIL"), method = "showFloatingItem")
    private void onTotemPop(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING))
            return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);
        MinecraftClient client = gameRenderer.getClient();

        PlayerEntity player = client.player;
        if (player == null)
            return;

        //confirm pop
        TotemUtilsClient.popped = true;

        // store last popped slot
        if (ModConfig.get().smartReplanishHotbar && (player.getInventory().getSelectedStack().isEmpty()||player.getInventory().getSelectedStack().getItem()==Items.TOTEM_OF_UNDYING)) {
            int slot = ((PlayerInventoryMixin)player.getInventory()).getSelectedSlot();
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