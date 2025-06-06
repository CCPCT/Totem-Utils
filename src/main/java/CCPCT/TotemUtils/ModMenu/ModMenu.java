package CCPCT.TotemUtils.ModMenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import CCPCT.TotemUtils.config.configScreen;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return configScreen::getConfigScreen;
    }
}
