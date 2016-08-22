/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.believedeviation;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class BelieverDeviationDeriverNodeSettingsProvider implements SettingConfigProvider {
    static final String BELIEVE_DEVIATION_CRITERION = "engine.believeDeviation.criterion";

    private static SettingConfig CRITERION_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(BELIEVE_DEVIATION_CRITERION, "Criterion", "Threshold for being deviation tolerant. Lower values mean system is more likely to deviate from its beliefs. Higher values mean system is more likely to hold on to its beliefs");

    @Override
    public String getGroupLabel() {
        return "Believer deviation";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(CRITERION_SETTING_CONFIG);
    }
}
