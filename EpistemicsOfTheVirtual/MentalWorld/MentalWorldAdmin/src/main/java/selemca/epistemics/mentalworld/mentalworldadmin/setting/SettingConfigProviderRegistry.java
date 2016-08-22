/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.setting;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import selemca.epistemics.mentalworld.engine.setting.SettingConfigProvider;

import java.util.ArrayList;
import java.util.List;

@Component
public class SettingConfigProviderRegistry {
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private List<SettingConfigProvider> settingConfigProviders;

    public List<SettingConfigProvider> getSettingConfigProviders() {
        return new ArrayList<>(settingConfigProviders);
    }
}
