/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.realitycheck;

import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.Arrays;
import java.util.List;

@Component
public class RealityCheckSettingsProvider implements SettingConfigProvider {
    static final String DISTRIBUTION_FICTION_MEAN = "engine.fiction.mean";
    static final String DISTRIBUTION_FICTION_DEVIATION = "engine.fiction.deviation";
    static final String DISTRIBUTION_FICTION_CUTOFF = "engine.fiction.cutoff";
    static final String DISTRIBUTION_REALITY_MEAN = "engine.reality.mean";
    static final String DISTRIBUTION_REALITY_DEVIATION = "engine.reality.deviation";
    static final String DISTRIBUTION_REALITY_CUTOFF = "engine.reality.cutoff";

    private static SettingConfig FICTION_MEAN_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(DISTRIBUTION_FICTION_MEAN, "Fiction mean", "Fiction distribution mean value");
    private static SettingConfig FICTION_DEVIATION_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(DISTRIBUTION_FICTION_DEVIATION, "Fiction deviation", "Fiction distribution deviation value");
    private static SettingConfig FICTION_CUTOFF_SETTING_CONFIG = new IntegerSettingConfig(DISTRIBUTION_FICTION_CUTOFF, "Fiction cutoff", "Number of deviation amounts for which a contribution score is still considered to fall under the distribution.", 1, 5);
    private static SettingConfig REALITY_MEAN_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(DISTRIBUTION_REALITY_MEAN, "Reality mean", "Fiction distribution mean value");
    private static SettingConfig REALITY_DEVIATION_SETTING_CONFIG = DoubleSettingConfig.createTruthValueSettingConfig(DISTRIBUTION_REALITY_DEVIATION, "Reality deviation", "Fiction distribution deviation value");
    private static SettingConfig REALITY_CUTOFF_SETTING_CONFIG = new IntegerSettingConfig(DISTRIBUTION_REALITY_CUTOFF, "Reality cutoff", "Number of deviation amounts for which a contribution score is still considered to fall under the distribution.", 1, 5);

    @Override
    public String getGroupLabel() {
        return "Reality check";
    }

    @Override
    public List<SettingConfig> getSettingConfigs() {
        return Arrays.asList(FICTION_MEAN_SETTING_CONFIG, FICTION_DEVIATION_SETTING_CONFIG, FICTION_CUTOFF_SETTING_CONFIG, REALITY_MEAN_SETTING_CONFIG, REALITY_DEVIATION_SETTING_CONFIG, REALITY_CUTOFF_SETTING_CONFIG);
    }
}
