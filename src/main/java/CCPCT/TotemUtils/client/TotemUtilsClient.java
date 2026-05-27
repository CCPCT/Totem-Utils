package CCPCT.TotemUtils.client;

import CCPCT.TotemUtils.config.ModConfig;
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
import net.minecraft.client.util.Window;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

import static CCPCT.TotemUtils.config.ModConfig.load;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyBinding swapTotemKey;
    public static KeyBinding configScreenKey;
    public static boolean checkedUpdate = false;

    // mixin var
    public static boolean moveMouseToTotem = false;
    public static boolean popped = false;

    public static class RenderHelper{
        public static int width = 0;
        public static int height;
        public static int argb;
        public static int centerX;
        public static int centerY;
        public static int holeHeight;
        public static int holeWidth;
    }

    public static void updateRenderCache(){
        Window window = MinecraftClient.getInstance().getWindow();
        TotemUtilsClient.RenderHelper.width = window.getScaledWidth();
        TotemUtilsClient.RenderHelper.height = window.getScaledHeight();
        RenderHelper.argb = (ModConfig.get().totemPopScreenAlpha << 24) | ModConfig.get().totemPopScreenColour;
        RenderHelper.centerX = RenderHelper.width / 2;
        RenderHelper.centerY = RenderHelper.height / 2;
        RenderHelper.holeHeight = RenderHelper.height - ModConfig.get().totemPopScreenWidth;
        RenderHelper.holeWidth = RenderHelper.width - ModConfig.get().totemPopScreenWidth;
    }

    @Override
    public void onInitializeClient() {
        KeyBinding.Category keybindCat = KeyBinding.Category.create(Identifier.of("totem_utils", "keys"));

        load();
        // Register the keybinding
        swapTotemKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemutils.swap_totem", // Matches the JSON key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                keybindCat
        ));

        configScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.totemutils.config_screen", // Matches the JSON key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                keybindCat
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
            ScreenKeyboardEvents.afterKeyPress(screen).register((scr, key) -> {
                if (swapTotemKey.matchesKey(key)) {
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

