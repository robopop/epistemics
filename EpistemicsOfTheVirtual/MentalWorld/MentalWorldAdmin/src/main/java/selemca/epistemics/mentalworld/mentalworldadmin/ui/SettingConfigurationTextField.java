/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.ui;

import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import org.apache.commons.configuration.Configuration;
import selemca.epistemics.mentalworld.engine.setting.DoubleSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.IntegerSettingConfig;
import selemca.epistemics.mentalworld.engine.setting.SettingConfig;

import java.util.logging.Logger;

/**
 * Created by henrizwols on 24-11-15.
 */
public class SettingConfigurationTextField extends TextField {
    private final Configuration applicationSettings;
    private final SettingConfig settingConfig;

    public SettingConfigurationTextField(Configuration applicationSettings, SettingConfig settingConfig) {
        super(settingConfig.getUiLabel());
        this.applicationSettings = applicationSettings;
        this.settingConfig = settingConfig;
        setDescription(getDescription(settingConfig));
        addValueChangeListener(e -> storeSetting());
    }

    private static String getDescription(SettingConfig settingConfig) {
        String description = settingConfig.getUiLabel();
        if (settingConfig instanceof DoubleSettingConfig) {
            DoubleSettingConfig doubleSettingConfig = (DoubleSettingConfig)settingConfig;
            description += String.format(" [%s - %s]", doubleSettingConfig.getMinValue(), doubleSettingConfig.getMaxValue());
        }
        if (settingConfig instanceof IntegerSettingConfig) {
            IntegerSettingConfig integerSettingConfig = (IntegerSettingConfig)settingConfig;
            description += String.format(" [%s - %s]", integerSettingConfig.getMinValue(), integerSettingConfig.getMaxValue());
        }
        return description;
    }

    public void reset() {
        String value = applicationSettings.getString(settingConfig.getSettingKey());
        setValue(value);
    }

    private void storeSetting() {
        String currentValue = applicationSettings.getString(settingConfig.getSettingKey());
        String newValue = getValue();
        if (newValue != null && !newValue.equals(currentValue)) {
            if (checkValid(newValue)) {
                applicationSettings.setProperty(settingConfig.getSettingKey(), newValue);
                Logger.getLogger(getClass().getSimpleName()).info(String.format("Wrote setting %s = %s", settingConfig.getSettingKey(), newValue));
            }
        }
    }

    public boolean checkValid(String value) {
        if (settingConfig instanceof IntegerSettingConfig) {
            IntegerSettingConfig integerSettingConfig = (IntegerSettingConfig)settingConfig;
            try {
                Integer number = Integer.valueOf(value);
                if (number < integerSettingConfig.getMinValue() || number > integerSettingConfig.getMaxValue()) {
                    Notification notification = new Notification(String.format("Number out of range [%s-%s]", integerSettingConfig.getMinValue(), integerSettingConfig.getMaxValue()));
                    notification.show(Page.getCurrent());
                    return false;
                }
            } catch (NumberFormatException e) {
                Notification invalidInput = new Notification("Invalid number");
                invalidInput.show(Page.getCurrent());
                return false;
            }
        }
        if (settingConfig instanceof DoubleSettingConfig) {
            DoubleSettingConfig doubleSettingConfig = (DoubleSettingConfig)settingConfig;
            try {
                Double number = Double.valueOf(value);
                if (number < doubleSettingConfig.getMinValue() || number > doubleSettingConfig.getMaxValue()) {
                    Notification notification = new Notification(String.format("Number out of range [%s-%s]", doubleSettingConfig.getMinValue(), doubleSettingConfig.getMaxValue()));
                    notification.show(Page.getCurrent());
                    return false;
                }
            } catch (NumberFormatException e) {
                Notification invalidInput = new Notification("Invalid number");
                invalidInput.show(Page.getCurrent());
                return false;
            }
        }
        return true;
    }
}
