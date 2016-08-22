/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.appraisal;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class EpistemicAppraisalDeriverNodeSettingsProvider implements SettingConfigProvider {
    static final String ACCEPT_AS_REALISTIC_CRITERION = "engine.epistemicAppraisal.criterion";

    private static SettingConfig CRITERION_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(ACCEPT_AS_REALISTIC_CRITERION, "Accept as realisting criterion", "Criterion for still accepting contribution as realistic. Higher values mean the system is less likely to accept a contribution as realistic.");

    @Override
    public String getGroupLabel() {
        return "Epistemic appraisal";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(CRITERION_SETTING_CONFIG);
    }
}
