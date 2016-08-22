/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.changeconcept;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class ChangeConceptDeriverNodeSettingsProvider implements SettingConfigProvider {
    static final String NEW_ASSOCIATION_TRUTH_VALUE = "engine.changeconcept.newAssociationTruthValue";

    private static SettingConfig CHANGE_CONCEPT_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(NEW_ASSOCIATION_TRUTH_VALUE, "New association truth value", "Initial truth value for newly inserted relations");

    @Override
    public String getGroupLabel() {
        return "Change concept";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(CHANGE_CONCEPT_SETTING_CONFIG);
    }
}
