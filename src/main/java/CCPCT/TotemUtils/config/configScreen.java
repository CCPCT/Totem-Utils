package CCPCT.TotemUtils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class configScreen extends Screen {

    protected configScreen() {
        super(Text.literal("Totem Utils Config"));
    }

    public static Screen getConfigScreen(Screen parent) {
        ModConfig.load();
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Totem Utils Config"))
                .setSavingRunnable(ModConfig::save);

        ConfigCategory generalTab = builder.getOrCreateCategory(Text.literal("General"));
        ConfigCategory soundTab = builder.getOrCreateCategory(Text.literal("Sound"));
        ConfigCategory screenTab = builder.getOrCreateCategory(Text.literal("Screen"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Auto Totem toggle
        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Auto Totem"),ModConfig.get().autoTotem)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Don't use unless server allows"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().autoTotem = newValue;
                })
                .build());

        generalTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Chat feedback"),ModConfig.get().chatfeedback)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Send feedback in chat (only u can see), e.g. u popped ur totem"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().chatfeedback = newValue;
                })
                .build());


        // Custom Sound toggle
        soundTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Custom Sound"),ModConfig.get().customSound)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable custom sound when your totem pops (other players unaffected)"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().customSound = newValue;
                })
                .build());

        soundTab.addEntry(entryBuilder.startStrField(Text.literal("Sound Event"), ModConfig.get().customSoundName)
            .setTooltip(Text.literal("Enter the sound ID (e.g., minecraft:entity.player.levelup)"))
            .setDefaultValue("minecraft:item.shield.break")
            .setSaveConsumer(newValue -> {
                ModConfig.get().customSoundName = newValue;
            })
            .build());

        soundTab.addEntry(entryBuilder.startFloatField(Text.literal("Volume"), ModConfig.get().customSoundVolume)
            .setTooltip(Text.literal("Set the volume (1.5 = 150%)"))
            .setMin(0.0f)
            .setMax(10.0f)
            .setDefaultValue(1.0f)
            .setSaveConsumer(newValue -> {
                ModConfig.get().customSoundVolume = newValue;
            })
            .build());

        //screen tab
        screenTab.addEntry(entryBuilder.startBooleanToggle(Text.literal("Totem Pop Screen"),ModConfig.get().totemPopScreen)
            .setDefaultValue(false)
            .setTooltip(Text.literal("Render screen vintage overlay effect when popped totem"))
            .setSaveConsumer(newValue -> {
                ModConfig.get().totemPopScreen = newValue;
            })
            .build());

        screenTab.addEntry(builder.entryBuilder()
                .startColorField(Text.literal("Overlay Color"), ModConfig.get().totemPopScreenColour)
                .setDefaultValue(0xFFFF00)
                .setTooltip(Text.literal("Colour of overlay effect"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().totemPopScreenColour = newValue;
                })
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("Overlay Alpha"), ModConfig.get().totemPopScreenAlpha)
                .setTooltip(Text.literal("Opacity of overlay effect: 0 = fully transparent"))
                .setDefaultValue(255)
                .setMin(0).setMax(255)
                .setSaveConsumer(newValue -> {
                    ModConfig.get().totemPopScreenAlpha = newValue;
                })
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("Duration"), ModConfig.get().totemPopScreenDuration)
                .setTooltip(Text.literal("Overlay will end if equiped totem or after this value, in seconds"))
                .setDefaultValue(20)
                .setSaveConsumer(newValue -> {
                    ModConfig.get().totemPopScreenDuration = newValue;
                })
                .build());

        screenTab.addEntry(entryBuilder.startIntField(Text.literal("Width"), ModConfig.get().totemPopScreenWidth)
                .setTooltip(Text.literal("Width of the overlay from border of the screen, in pixels"))
                .setDefaultValue(100)
                .setSaveConsumer(newValue -> {
                    ModConfig.get().totemPopScreenWidth = newValue;
                })
                .build());

        return builder.build();
    }
}
