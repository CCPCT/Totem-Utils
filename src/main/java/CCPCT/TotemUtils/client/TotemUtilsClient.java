package CCPCT.TotemUtils.client;

import CCPCT.TotemUtils.config.ModConfig;
import CCPCT.TotemUtils.config.configScreen;
import CCPCT.TotemUtils.util.Logic;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static CCPCT.TotemUtils.config.ModConfig.load;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyMapping swapTotemKey;
    public static KeyMapping configScreenKey;

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
        Window window = Minecraft.getInstance().getWindow();
        TotemUtilsClient.RenderHelper.width = window.getWidth();
        TotemUtilsClient.RenderHelper.height = window.getHeight();
        RenderHelper.argb = (ModConfig.get().totemPopScreenAlpha << 24) | ModConfig.get().totemPopScreenColour;
        RenderHelper.centerX = RenderHelper.width / 2;
        RenderHelper.centerY = RenderHelper.height / 2;
        RenderHelper.holeHeight = RenderHelper.height - ModConfig.get().totemPopScreenWidth;
        RenderHelper.holeWidth = RenderHelper.width - ModConfig.get().totemPopScreenWidth;
    }

    @Override
    public void onInitializeClient() {
        KeyMapping.Category keybindCat = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("totem_utils", "keys"));
        load();
        // Register the KeyMapping
        swapTotemKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.totemutils.swap_totem", // Matches the JSON key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                keybindCat
        ));

        configScreenKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.totemutils.config_screen", // Matches the JSON key
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                keybindCat
        ));

        // Register client tick listener
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (swapTotemKey.isDown()) {
                // swap totem
                System.out.println("pressed totem key");
                Logic.refillTotem();
            }

            if (configScreenKey.isDown()) {
                // open config
                Minecraft.getInstance().setScreen(configScreen.getConfigScreen(Minecraft.getInstance().screen));
            }

            if (client.player == null || client.player.isCreative() || client.player.isSpectator() || !client.player.isAlive()) {
                Logic.resetStatus();
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register((scr, key) -> {
                if (swapTotemKey.matches(key)) {
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
            if (entity == Minecraft.getInstance().player) {
                // You just arrived in a new world/hub
                Logic.resetStatus();
            }
        });
    }
}

