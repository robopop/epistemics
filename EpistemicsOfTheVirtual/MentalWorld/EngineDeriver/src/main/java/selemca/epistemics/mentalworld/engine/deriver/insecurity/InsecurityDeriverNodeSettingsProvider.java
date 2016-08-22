/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.insecurity;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class InsecurityDeriverNodeSettingsProvider implements SettingConfigProvider {
    static final String INSECURITY_DIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE = "engine.insecurityDirectAssociationModificationPercentage";
    static final String INSECURITY_CONVERSE_TO_VALUE = "engine.insecurityConverseToTarget";

    private static SettingConfig PERCENTAGE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(INSECURITY_DIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE, "Insecurity direct association modifier", "Insecurity relative push towards set value of contribution. The higher the value the faster the system will learn");
    private static SettingConfig CONVERSE_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(INSECURITY_CONVERSE_TO_VALUE, "Insecurity converse to value", "Target to aim for when weakening the relation. This value would typically be 0.5 or slightly lower.");

    @Override
    public String getGroupLabel() {
        return "Insecurity";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(PERCENTAGE_SETTING_CONFIG, CONVERSE_SETTING_CONFIG);
    }
}
