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
public class IntegerSettingConfig extends SettingConfig {
    private final int minValue;
    private final int maxValue;

    public IntegerSettingConfig(String settingKey, String uiLabel, String description, int minValue, int maxValue) {
        super(settingKey, uiLabel, description);
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public static IntegerSettingConfig createPercentageSettingConfig(String settingKey, String uiLabel, String description) {
        return new IntegerSettingConfig(settingKey, uiLabel, description, 0, 100);
    }
}
