/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.setting;

/**
 * Created by henrizwols on 24-11-15.
 */
public class DoubleSettingConfig extends SettingConfig {
    private final double minValue;
    private final double maxValue;

    public DoubleSettingConfig(String settingKey, String uiLabel, String description, double minValue, double maxValue) {
        super(settingKey, uiLabel, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public static DoubleSettingConfig createTruthValueSettingConfig(String settingKey, String uiLabel, String description) {
        return new DoubleSettingConfig(settingKey, uiLabel, description, 0.0, 1.0);
    }
}
