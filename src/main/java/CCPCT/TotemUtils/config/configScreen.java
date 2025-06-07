package CCPCT.TotemUtils.config;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

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

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Auto Totem toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Auto Totem"),ModConfig.get().autoTotem)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Don't use unless server allows"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().autoTotem = newValue;
                    ModConfig.save();
                })
                .build());

        // Custom Sound toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Custom Sound"),ModConfig.get().customSound)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable custom sound when your totem pops (other players unaffected)"))
                .setSaveConsumer(newValue -> {
                    ModConfig.get().customSound = newValue;
                    ModConfig.save();
                })
                .build());

        general.addEntry(entryBuilder.startStrField(Text.literal("Sound Event"), ModConfig.get().customSoundName)
            .setTooltip(Text.literal("Enter the sound ID (e.g., minecraft:entity.player.levelup)"))
            .setDefaultValue("minecraft:item.shield.break")
            .setSaveConsumer(newValue -> {
                ModConfig.get().customSoundName = newValue;
                ModConfig.save();
            })
            .build());

        general.addEntry(entryBuilder.startFloatField(Text.literal("Volume"), ModConfig.get().customSoundVolume)
            .setTooltip(Text.literal("Set the volume (1.5 = 150%)"))
            .setMin(0.0f)
            .setMax(5.0f)
            .setDefaultValue(1.0f)
            .setSaveConsumer(newValue -> {
                ModConfig.get().customSoundVolume = newValue;
                ModConfig.save();
            })
            .build());

        return builder.build();
    }
}
