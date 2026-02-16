package CCPCT.TotemUtils.util;

import CCPCT.TotemUtils.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class Chat {
    public static <T> void send(T message,boolean action) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§e[Totem Utils]§r "+message.toString()), action);
            }
        }
    }
}