package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class Chat {
    public static <T> void send(T message,boolean action) {
        if (ModConfig.get().chatfeedback) {
            var player = Minecraft.getInstance().player;
            if (player != null) {
                var sending = Component.literal("§e[Totem Utils]§r "+message.toString());
                if (action) {
                    player.sendOverlayMessage(sending);
                } else {
                    player.sendSystemMessage(sending);
                }
            }
        }
    }
}