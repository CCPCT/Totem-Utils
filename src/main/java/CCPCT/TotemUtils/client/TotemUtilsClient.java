package CCPCT.TotemUtils.client;

import CCPCT.TotemUtils.config.configScreen;
import CCPCT.TotemUtils.util.Logic;
import CCPCT.TotemUtils.util.PacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;

import static CCPCT.TotemUtils.config.ModConfig.load;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyBinding swapTotemKey;
    public static KeyBinding configScreenKey;
    public static boolean checkedUpdate = false;

    // mixin var
    public static boolean moveMouseToTotem = false;
    public static boolean popped = false;

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
                System.out.println("pressed totem key");
                Logic.refillTotem();
            }

            if (configScreenKey.wasPressed()) {
                // open config
                MinecraftClient.getInstance().setScreen(configScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen));
            }

            if (client.player == null || client.player.isCreative() || client.player.isSpectator() || !client.player.isAlive()) {
                Logic.resetStatus();
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register((scr, key, scancode, modifiers) -> {
                if (swapTotemKey.matchesKey(key, scancode)) {
                    System.out.println("pressed totem key in inv");
                    moveMouseToTotem = true;
                }
            });
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            //join server/ world
            Logic.resetStatus();
        });

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity == MinecraftClient.getInstance().player) {
                // You just arrived in a new world/hub
                Logic.resetStatus();
            }
        });
    }
}

