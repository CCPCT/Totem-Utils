package CCPCT.TotemUtils.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerEntity;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.util.IngameChat;

import CCPCT.TotemUtils.util.totemlogic;

@Mixin(GameRenderer.class)
public class onTotemPopMixin {

    @Inject(at = @At("TAIL"), method = "tick")
    private void onTick(CallbackInfo ci) {
        ArrayList<Packet<?>> packetsToSend = totemlogic.getPacketsToSend();
        if (packetsToSend.isEmpty())
            return;

        ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
        if (networkHandler == null)
            return;

        networkHandler.sendPacket(packetsToSend.getFirst());
        totemlogic.popPacketsToSend();
    }

    @Inject(at = @At("TAIL"), method = "showFloatingItem")
    private void onTotemUse(ItemStack floatingItem, CallbackInfo ci) {
        if (!floatingItem.isOf(Items.TOTEM_OF_UNDYING))
            return;

        GameRenderer gameRenderer = (GameRenderer) ((Object) this);
        MinecraftClient client = gameRenderer.getClient();

        PlayerEntity player = client.player;
        if (player == null)
            return;

//        if (!player.hasStatusEffect(StatusEffects.FIRE_RESISTANCE))
//            return;
//        if (!player.hasStatusEffect(StatusEffects.REGENERATION))
//            return;

        // popped

        // auto totem
        if (ModConfig.get().autoTotem) {
            totemlogic.refillTotem(true);
        } else {
            IngameChat.sendColourChat("You Popped!", "red");
        }
        if (ModConfig.get().customSound) {
            totemlogic.stopTotemSound();
            totemlogic.playShieldBreakSound();
        }
    }
}