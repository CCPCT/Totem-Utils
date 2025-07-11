package CCPCT.TotemUtils.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import CCPCT.TotemUtils.config.ModConfig;

public class Chat {
    public static <T> void send(T message) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message.toString()), false);
            }
        }
    }

    public static void colour(String message, String color) {
        if (ModConfig.get().chatfeedback) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null) {
                client.player.sendMessage(Text.literal(message).formatted(Formatting.byName(color.toLowerCase())), false);
            }
        }
    }

    public static void link(String message, String link) {
        if (MinecraftClient.getInstance().player == null) return;

        Text messageToSend = Text.literal(message)
                .setStyle(Style.EMPTY
                        .withColor(Formatting.BLUE)
                        .withUnderline(true)
                        .withClickEvent(new ClickEvent(
                                ClickEvent.Action.OPEN_URL,
                                link
                        )));

        MinecraftClient.getInstance().player.sendMessage(messageToSend, false);
    }
}