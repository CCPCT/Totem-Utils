package CCPCT.TotemUtils.client;
import static CCPCT.TotemUtils.config.ModConfig.load;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import CCPCT.TotemUtils.util.*;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyBinding Swap_totem_key;

    @Override
    public void onInitializeClient() {

        load();
        // Register the keybinding
        Swap_totem_key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Swap Totem", // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,       // default key
                "Totem Utils"       // category in controls menu
        ));

        // Register client tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (Swap_totem_key.wasPressed()) {
                // Action to perform when key is pressed
                IngameChat.sendChat("Switching totem");
                totemlogic.refillTotem(false);
                // Example: trigger your own function here
            }
        });
    }
}

