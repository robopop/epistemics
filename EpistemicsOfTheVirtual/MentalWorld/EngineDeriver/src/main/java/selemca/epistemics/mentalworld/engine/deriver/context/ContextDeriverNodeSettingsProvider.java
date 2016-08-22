/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.context;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class ContextDeriverNodeSettingsProvider implements SettingConfigProvider {
    public static final String CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE = "engine.contextAssociationMaximumDistance";


    private static SettingConfig CATEGORY_MATCH_SETTING_CONFIG = new DoubleSettingConfig(CONTEXT_ASSOCIATION_MAXIMUM_DISTANCE, "Association maximum distance", "Maximum distance between concept and context for category match. Lower values mean tunnel-vision: very narrow expectance, almost anything leads to context-shock. Higher values mean a more flexible personality: not easily surprised", 0.0, 10.0);

    @Override
    public String getGroupLabel() {
        return "Context";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(CATEGORY_MATCH_SETTING_CONFIG);
    }
}
