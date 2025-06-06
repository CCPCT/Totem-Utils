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

        ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Auto Totem toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Auto Totem"),ModConfig.get().autoTotem)
                .setDefaultValue(false)
                .setSaveConsumer(newValue -> {
                    ModConfig.get().autoTotem = newValue;
                    ModConfig.save();
                })
                .build());

        // Custom Sound toggle
        general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Custom Sound"),ModConfig.get().customSound)
                .setDefaultValue(true)
                .setSaveConsumer(newValue -> {
                    ModConfig.get().customSound = newValue;
                    ModConfig.save();
                })
                .build());

        return builder.build();
    }
}
