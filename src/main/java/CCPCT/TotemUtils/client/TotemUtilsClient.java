package CCPCT.TotemUtils.client;

import static CCPCT.TotemUtils.config.ModConfig.load;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import org.lwjgl.glfw.GLFW;

import CCPCT.TotemUtils.util.*;
import CCPCT.TotemUtils.config.configScreen;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyBinding swapTotemKey;
    public static KeyBinding configScreenKey;
    public static boolean checkedUpdate = false;
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
            if (swapTotemKey.isPressed()) {
                // swap totem
                Logic.refillTotem();
            }

            if (configScreenKey.wasPressed()) {
                // open config
                MinecraftClient.getInstance().setScreen(configScreen.getConfigScreen(MinecraftClient.getInstance().currentScreen));
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (checkedUpdate) return;
            checkedUpdate = true;
            String[] mcVersion = FabricLoader.getInstance()
                    .getModContainer("totemutils").get()
                    .getMetadata().getVersion().getFriendlyString()
                    .split("-");
            String newestVersion = VersionChecker.getNewestVersion(mcVersion[1]);

            if (newestVersion == null){
                Chat.send("§cFailed to check for updates.", false);
                return;
            }

            if (newestVersion.isEmpty()){
                Chat.send("§cFound no compatible version", false);
                return;
            }

            if (VersionChecker.compareVersions(newestVersion, mcVersion[0]) > 0) {
                Chat.send("Mod update available: " + newestVersion + " for " + mcVersion[1],false);
                Chat.send("Links to download:",false);
                Chat.link("[Github]", "https://github.com/CCPCT/Totem-Utils/releases");
                Chat.link("[Modrinth]", "https://modrinth.com/mod/totemutils");
            } else {
                Chat.send("§aMod is up to date",true);
            }
        });
    }
}

