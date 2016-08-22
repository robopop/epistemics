/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.reassurance;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class ReassuranceDeriverNodeSettingsProvider implements SettingConfigProvider {
    static final String REASSURENCE_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE = "engine.reassurenceDirectAssociationModificationPercentage";
    static final String REASSURENCE_INDIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE = "engine.reassurenceIndirectAssociationsModificationPercentage";

    private static SettingConfig DIRECT_PERCENTAGE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(REASSURENCE_DIRECT_ASSOCIATION_MODIFICATION_PERCENTAGE, "Reassurance direct association modifier", "Reassurance relative push towards true of relation directly connected to match. The higher the value the faster the system will learn");
    private static SettingConfig INDIRECT_PERCENTAGE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(REASSURENCE_INDIRECT_ASSOCIATIONS_MODIFICATION_PERCENTAGE, "Reassurance indirect association modifier", "Reassurance relative push towards true of relation indirectly connected to match. The higher the value the faster the system will learn");

    @Override
    public String getGroupLabel() {
        return "Reassurance";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(DIRECT_PERCENTAGE_SETTING_CONFIG, INDIRECT_PERCENTAGE_SETTING_CONFIG);
    }
}
