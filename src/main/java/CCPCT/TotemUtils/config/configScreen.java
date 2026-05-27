package CCPCT.TotemUtils.config;

import CCPCT.TotemUtils.client.TotemUtilsClient;
import com.mojang.blaze3d.platform.InputConstants;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class configScreen extends Screen {

    protected configScreen() {
        super(Component.literal("Totem Utils Config"));
    }

    public static Screen getConfigScreen(Screen parent) {
        ModConfig.load();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("Totem Utils Config"))
                .setSavingRunnable(ModConfig::save);

        ConfigCategory generalTab = builder.getOrCreateCategory(Component.literal("General"));
        ConfigCategory soundTab = builder.getOrCreateCategory(Component.literal("Sound"));
        ConfigCategory screenTab = builder.getOrCreateCategory(Component.literal("Screen"));
        ConfigCategory countTab = builder.getOrCreateCategory(Component.literal("Totem Counter"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // General settings
        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Chat feedback"),ModConfig.get().chatfeedback)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Send feedback in chat (only u can see), e.g. u popped ur totem"))
                .setSaveConsumer(newValue -> ModConfig.get().chatfeedback = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Auto Totem"),ModConfig.get().autoTotem)
                .setDefaultValue(false)
                .setTooltip(Component.literal("Don't use unless server allows"))
                .setSaveConsumer(newValue -> ModConfig.get().autoTotem = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startIntField(Component.literal("Auto Totem Delay"), ModConfig.get().autoTotemDelay)
                .setTooltip(Component.literal("how long to wait before autototeming, in ticks"))
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> ModConfig.get().autoTotemDelay = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startStrField(Component.literal("Replenish totem hotkey"), TotemUtilsClient.swapTotemKey.getTranslatedKeyMessage().getString())
                .setTooltip(Component.literal("Recommended to modify this option in the option menu"))
                .setDefaultValue("F")
                .setSaveConsumer(newValue -> {
                    try {
                        String keyIdentifier = "key.keyboard." + newValue.toLowerCase();
                        TotemUtilsClient.swapTotemKey.setKey(InputConstants.getKey(keyIdentifier));
                    } catch (Exception e) {
                        TotemUtilsClient.swapTotemKey.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_F));
                    }
                })
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replenish main hand totem"),ModConfig.get().replenishMainHandTotem)
                .setDefaultValue(false)
                .setTooltip(Component.literal("also replace main hand totem if popped or use hotkey"))
                .setSaveConsumer(newValue -> ModConfig.get().replenishMainHandTotem = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Replenish other items"),ModConfig.get().replenishGeneralItem)
                .setDefaultValue(false)
                .setTooltip(Component.literal("also replenish main hand items"))
                .setSaveConsumer(newValue -> ModConfig.get().replenishGeneralItem = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Curser auto snap on totem"),ModConfig.get().snapOnTotem)
                .setDefaultValue(false)
                .setTooltip(Component.literal("curser auto moves to totem when open inventory after popped\nwill also snap when pressed hotkey"))
                .setSaveConsumer(newValue -> ModConfig.get().snapOnTotem = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Smart replenish totem in hotbar"),ModConfig.get().smartReplanishHotbar)
                .setDefaultValue(false)
                .setTooltip(Component.literal("replenish even when picked up item/ not holding empty slot\nreplanish last hotbar popped totem slot"))
                .setSaveConsumer(newValue -> ModConfig.get().smartReplanishHotbar = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startIntField(Component.literal("Smart replenish totem slot"),ModConfig.get().smartReplanishslot)
                .setDefaultValue(-1)
                .setMin(-1).setMax(8)
                .setTooltip(Component.literal("Manually select which slot (0-8) to replace. -1 to auto detect\nRequire Smart replenish enabled\nRecommend to manually input as auto detect may not be most accurate"))
                .setSaveConsumer(newValue -> ModConfig.get().smartReplanishslot = newValue)
                .build());

        generalTab.addEntry(entryBuilder.startStrField(Component.literal("Open config hotkey"), TotemUtilsClient.configScreenKey.getTranslatedKeyMessage().getString())
                .setTooltip(Component.literal("Recommended to modify this option in the option menu"))
                .setDefaultValue("H")
                .setSaveConsumer(newValue -> {
                    try {
                        String keyIdentifier = "key.keyboard." + newValue.toLowerCase();
                        TotemUtilsClient.configScreenKey.setKey(InputConstants.getKey(keyIdentifier));
                    } catch (Exception e) {
                        TotemUtilsClient.configScreenKey.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_H));
                    }
                })
                .build());


        // Custom Sound toggle
        soundTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Custom Sound"),ModConfig.get().customSound)
                .setDefaultValue(true)
                .setTooltip(Component.literal("Enable custom sound when your totem pops (other players unaffected)"))
                .setSaveConsumer(newValue -> ModConfig.get().customSound = newValue)
                .build());

        soundTab.addEntry(entryBuilder.startStrField(Component.literal("Sound Event"), ModConfig.get().customSoundName)
            .setTooltip(Component.literal("Enter the sound ID (e.g., minecraft:entity.player.levelup)"))
            .setDefaultValue("minecraft:item.shield.break")
            .setSaveConsumer(newValue -> ModConfig.get().customSoundName = newValue)
            .build());

        soundTab.addEntry(entryBuilder.startFloatField(Component.literal("Volume"), ModConfig.get().customSoundVolume)
            .setTooltip(Component.literal("Set the volume (1.5 = 150%)"))
            .setMin(0.0f)
            .setMax(10.0f)
            .setDefaultValue(1.0f)
            .setSaveConsumer(newValue -> ModConfig.get().customSoundVolume = newValue)
            .build());

        //screen tab
        screenTab.addEntry(entryBuilder.startBooleanToggle(Component.literal("Enable Overlay"),ModConfig.get().totemPopScreen)
            .setDefaultValue(false)
            .setTooltip(Component.literal("Render screen vintage overlay effect when popped totem"))
            .setSaveConsumer(newValue -> ModConfig.get().totemPopScreen = newValue)
            .build());

        screenTab.addEntry(builder.entryBuilder()
                .startColorField(Component.literal("Color"), ModConfig.get().totemPopScreenColour)
                .setDefaultValue(0xFFFF00)
                .setTooltip(Component.literal("Colour of overlay effect"))
                .setSaveConsumer(newValue -> ModConfig.get().totemPopScreenColour = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("Alpha"), ModConfig.get().totemPopScreenAlpha)
                .setTooltip(Component.literal("Alpha (non-transparency/Opacity) of overlay"))
                .setDefaultValue(255)
                .setMin(0).setMax(255)
                .setSaveConsumer(newValue -> ModConfig.get().totemPopScreenAlpha = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("Duration"), ModConfig.get().totemPopScreenDuration)
                .setTooltip(Component.literal("Overlay will end if equiped totem or after this value, in seconds"))
                .setDefaultValue(20)
                .setSaveConsumer(newValue -> ModConfig.get().totemPopScreenDuration = newValue)
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Component.literal("Width"), ModConfig.get().totemPopScreenWidth)
                .setTooltip(Component.literal("Width of the overlay from border of the screen, in pixels"))
                .setDefaultValue(100)
                .setSaveConsumer(newValue -> ModConfig.get().totemPopScreenWidth = newValue)
                .build());

        //enabled size pos colour
        countTab.addEntry(entryBuilder.startIntField(Component.literal("Seconds to display"), ModConfig.get().totemCountTime)
                .setTooltip(Component.literal("-1=always render; 0=disable, positive integer=time to display after pop, in seconds"))
                .setDefaultValue(0)
                .setSaveConsumer(newValue -> ModConfig.get().totemCountTime = newValue)
                .build());

        countTab.addEntry(builder.entryBuilder()
                .startColorField(Component.literal("Component Color"), ModConfig.get().totemCountColour)
                .setDefaultValue(0x000000)
                .setTooltip(Component.literal("Colour of totem count Component"))
                .setSaveConsumer(newValue -> ModConfig.get().totemCountColour = newValue)
                .build());

        countTab.addEntry(entryBuilder.startIntField(Component.literal("Alpha"), ModConfig.get().totemCountAlpha)
                .setTooltip(Component.literal("Alpha (non-transparency/Opacity) of totem count Component"))
                .setDefaultValue(255)
                .setMin(0).setMax(255)
                .setSaveConsumer(newValue -> ModConfig.get().totemCountAlpha = newValue)
                .build());

        countTab.addEntry(entryBuilder.startIntField(Component.literal("X position"), ModConfig.get().totemCountx)
                .setTooltip(Component.literal("how many pixels from left of screen to start of Component"))
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().totemCountx = newValue)
                .build());

        countTab.addEntry(entryBuilder.startIntField(Component.literal("Y position"), ModConfig.get().totemCounty)
                .setTooltip(Component.literal("how many pixels from top of screen to start of Component"))
                .setDefaultValue(10)
                .setSaveConsumer(newValue -> ModConfig.get().totemCounty = newValue)
                .build());

        return builder.build();
    }
}
