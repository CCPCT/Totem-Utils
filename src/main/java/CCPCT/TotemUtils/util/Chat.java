package CCPCT.TotemUtils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import CCPCT.TotemUtils.config.ModConfig;

import java.net.URI;

public class Chat {
    public static <T> void send(T message,boolean action) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal("§e[Totem Utils]§r "+message.toString()), action);
            }
        }
    }

    public static void link(String message, String link) {
        if (MinecraftClient.getInstance().player == null) return;

        Text messageToSend = Text.literal(message)
                .setStyle(Style.EMPTY
                        .withColor(Formatting.BLUE)
                        .withUnderline(true)
                        .withClickEvent(new ClickEvent.OpenUrl(URI.create(link))) // <-- use the record
                );

        MinecraftClient.getInstance().player.sendMessage(messageToSend, false);
    }
}