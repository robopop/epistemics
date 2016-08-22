/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.metaphorprocessing;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class MetaphorProcessingSettingsProvider implements SettingConfigProvider {
    static final String VICINITY_TRESHOLT = "engine.metaphor.vicinity";
    static final String INTERSECTION_MINIMUM_SIZE_ABSOLUTE = "engine.metaphor.intersectionMinimumSize.absolute";
    static final String INTERSECTION_MINIMUM_SIZE_RELATIVE = "engine.metaphor.intersectionMinimumSize.relative";
    static final String MIXED_INTERSECTION_MINIMUM_SIZE_ABSOLUTE = "engine.metaphor.intersectionMixedMinimumSize.absolute";
    static final String MIXED_INTERSECTION_MINIMUM_SIZE_RELATIVE = "engine.metaphor.intersectionMixedMinimumSize.relative";
    static final String DEFAULT_RELATION_TYPE = "engine.metaphor.relationTypeDefault";

    private static SettingConfig VICINITY_TRESHOLT_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(VICINITY_TRESHOLT, "Vicinity thresholt", "Minimum truth value for features to be included in methaphor processing");
    private static SettingConfig INTERSECTION_MINUMUM_SIZE_ABSOLUTE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(INTERSECTION_MINIMUM_SIZE_ABSOLUTE, "Intersection minimum size", "Minimum intersection between the features of compared concepts");
    private static SettingConfig INTERSECTION_MINUMUM_SIZE_RELATIVE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(INTERSECTION_MINIMUM_SIZE_RELATIVE, "Intersection relative minimum size", "Minimum percentage of intersecting features as compared to the total number of unique features");
    private static SettingConfig MIXED_INTERSECTION_MINUMUM_SIZE_ABSOLUTE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(MIXED_INTERSECTION_MINIMUM_SIZE_ABSOLUTE, "Mixed intersection minimum size", "Minimum mixed (f-l or l -f) intersection between the features of compared concepts");
    private static SettingConfig MIXED_INTERSECTION_MINUMUM_SIZE_RELATIVE_SETTING_CONFIG = IntegerSettingConfig.createPercentageSettingConfig(MIXED_INTERSECTION_MINIMUM_SIZE_RELATIVE, "Mixed intersection relative minimum size", "Minimum percentage of mixed (f-l or l -f) intersecting features as compared to all intersecting features");

    @Override
    public String getGroupLabel() {
        return "Metaphor processing";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(VICINITY_TRESHOLT_SETTING_CONFIG, INTERSECTION_MINUMUM_SIZE_ABSOLUTE_SETTING_CONFIG, INTERSECTION_MINUMUM_SIZE_RELATIVE_SETTING_CONFIG, MIXED_INTERSECTION_MINUMUM_SIZE_ABSOLUTE_SETTING_CONFIG, MIXED_INTERSECTION_MINUMUM_SIZE_RELATIVE_SETTING_CONFIG);
    }
}
