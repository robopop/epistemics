/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.ui;

import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by henrizwols on 24-11-15.
 */
public class SettingsFormComponent extends FormLayout {
    private final Configuration applicationSettings;
    private final SettingConfigProvider settingConfigProvider;
    private final List<SettingConfigurationTextField> settingConfigurationTextFields = new ArrayList<>();

    public SettingsFormComponent(Configuration applicationSettings, SettingConfigProvider settingConfigProvider) {
        this.applicationSettings = applicationSettings;
        this.settingConfigProvider = settingConfigProvider;

        for (SettingConfig settingConfig : settingConfigProvider.getSettingConfigs()) {
            SettingConfigurationTextField valueField = new SettingConfigurationTextField(applicationSettings, settingConfig);
            addComponent(new Label(settingConfig.getDescription()));
            addComponent(valueField);
            settingConfigurationTextFields.add(valueField);
            valueField.reset();
        }
    }

    public void reset() {
        for (SettingConfigurationTextField settingConfigurationTextField :settingConfigurationTextFields) {
            settingConfigurationTextField.reset();
        }
    }
}
