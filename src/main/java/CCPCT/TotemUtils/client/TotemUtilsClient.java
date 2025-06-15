package CCPCT.TotemUtils.client;

import static CCPCT.TotemUtils.config.ModConfig.load;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import CCPCT.TotemUtils.util.*;
import CCPCT.TotemUtils.config.configScreen;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyBinding swapTotemKey;
    public static KeyBinding configScreenKey;

    @Override
    public void onInitializeClient() {

        load();
        // Register the keybinding
        swapTotemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap Totem", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,       // default key
                "Totem Utils"       // category in controls menu
        ));

        configScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Config screen", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,       // default key
                "Totem Utils"       // category in controls menu
        ));

        // Register client tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (swapTotemKey.wasPressed()) {
                // swap totem
                IngameChat.sendChat("Switching totem");
                totemlogic.refillTotem();
            }

            if (configScreenKey.wasPressed()) {
                // open config
                MinecraftClient.getInstance().setScreen(configScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen));
            }
        });
    }
}

