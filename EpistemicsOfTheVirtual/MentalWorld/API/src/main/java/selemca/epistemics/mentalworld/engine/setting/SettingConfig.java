/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.setting;

/**
 * Created by henrizwols on 22-11-15.
 */
public abstract class SettingConfig {
    private final String settingKey;
    private final String uiLabel;
    private final String description;

    public SettingConfig(String settingKey, String uiLabel, String description) {
        this.settingKey = settingKey;
        this.uiLabel = uiLabel;
        this.description = description;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public String getUiLabel() {
        return uiLabel;
    }

    public String getDescription() {
        return description;
    }
}
