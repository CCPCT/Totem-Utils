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
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import static CCPCT.TotemUtils.config.ModConfig.load;

public class TotemUtilsClient implements ClientModInitializer {
    public static KeyMapping swapTotemKey;
    public static KeyMapping configScreenKey;
    public static final String MODID = "totemutils";

    // mixin var
    public static boolean moveMouseToTotem = false;
    public static boolean popped = false;
    int totemCount = 0;

    boolean totemKeyWasDown = false;

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
            if (client.level == null) return;
            if (swapTotemKey.isDown()) {
                if (!totemKeyWasDown) {
                    // swap totem... prevend holding = multiple
                    System.out.println("pressed totem key");
                    Logic.refillTotem();
                }
                totemKeyWasDown = true;
            } else {
                totemKeyWasDown = false;
            }

            if (configScreenKey.isDown()) {
                // open config, dont need extra logic as this only run in world...
                client.gui.setScreen(configScreen.getConfigScreen(client.gui.screen()));
            }

            if (client.player == null || client.player.isCreative() || client.player.isSpectator() || !client.player.isAlive()) {
                Logic.resetStatus();
            }

            // rendering
            if (Logic.totemCountActive) {
                totemCount = Logic.getTotemCount(true);
            }
        });

        ScreenEvents.AFTER_INIT.register((client, screen, width, height) -> {
            ScreenKeyboardEvents.afterKeyPress(screen).register((scr, key) -> {
                if (client.level == null) return;
                if (swapTotemKey.matches(key)) {
                    System.out.println("pressed totem key in inv");
                    moveMouseToTotem = true;
                }
            });
        });

        HudElementRegistry.attachElementBefore(VanillaHudElements.CROSSHAIR, Identifier.fromNamespaceAndPath(MODID, "overlay"),
                ((GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) -> {
                    var client = Minecraft.getInstance();
                    if (Logic.overlayactive) {
                        int width = client.getWindow().getGuiScaledWidth();
                        int height = client.getWindow().getGuiScaledHeight();
                        int centerX = width / 2;
                        int centerY = height / 2;
                        int holeHeight = height - ModConfig.get().totemPopScreenWidth;
                        int holeWidth = width - ModConfig.get().totemPopScreenWidth;

                        // top
                        graphics.fill(0, 0, width, centerY - holeHeight / 2, ModConfig.get().totemPopScreenColour);
                        // bottom
                        graphics.fill(0, centerY + holeHeight / 2, width, height, ModConfig.get().totemPopScreenColour);
                        // left
                        graphics.fill(0, centerY - holeHeight / 2, centerX - holeWidth / 2, centerY + holeHeight / 2, ModConfig.get().totemPopScreenColour);
                        // right
                        graphics.fill(centerX + holeWidth / 2, centerY - holeHeight / 2, width, centerY + holeHeight / 2, ModConfig.get().totemPopScreenColour);
                    }
                    if (Logic.totemCountActive || ModConfig.get().totemCountTime == -1){
                        graphics.text(client.font, String.valueOf(totemCount), ModConfig.get().totemCountx, ModConfig.get().totemCounty, ModConfig.get().totemCountColour, true);
                    }
                }));

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

