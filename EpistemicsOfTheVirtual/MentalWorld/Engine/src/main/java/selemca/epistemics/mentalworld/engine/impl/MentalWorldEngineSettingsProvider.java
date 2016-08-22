/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.impl;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class MentalWorldEngineSettingsProvider implements SettingConfigProvider {
    static final String MAXIMUM_TRAVERSALS = "engine.maximumNumberOfTraversals";

    private static SettingConfig MAXIMUM_TRAVERSALS_SETTING_CONFIG = new IntegerSettingConfig(MAXIMUM_TRAVERSALS, "Maximum number of category tries", "Maximum number of category tries", 1, 10);

    @Override
    public String getGroupLabel() {
        return "Engine";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(MAXIMUM_TRAVERSALS_SETTING_CONFIG);
    }
}
