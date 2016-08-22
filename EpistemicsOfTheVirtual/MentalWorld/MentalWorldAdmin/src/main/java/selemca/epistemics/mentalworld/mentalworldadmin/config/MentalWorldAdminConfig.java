/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.mentalworldadmin.config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.apache.commons.configuration.reloading.ReloadingStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import selemca.epistemics.mentalworld.beliefsystem.config.BeliefSystemConfig;
import selemca.epistemics.mentalworld.engine.config.EngineConfig;
import selemca.epistemics.mentalworld.engine.deriver.config.EngineDeriverConfig;
import selemca.epistemics.mentalworld.engine.metaphorprocessing.config.MetaphorConfig;
import selemca.epistemics.mentalworld.registry.config.RegistryConfig;

import java.io.File;
import java.util.logging.Logger;

@Configuration
@Import({BeliefSystemConfig.class, EngineDeriverConfig.class, MetaphorConfig.class, EngineConfig.class, RegistryConfig.class})
@ComponentScan({"selemca.epistemics.mentalworld.mentalworldadmin.setting"})
public class MentalWorldAdminConfig {
    private static final String APPLICATION_SETTINGS_FILE = "ServerApplicationSettings.properties";

    @Bean
    public File selemcaHome() {
        return SelemcaHome.getSelemcaHome();
    }

    @Bean
    public org.apache.commons.configuration.Configuration applicationSettings(File selemcaHome) throws ConfigurationException {
        File applicationSettingsFile = getApplicationSettingsFile(selemcaHome);
        PropertiesConfiguration appicationConfiguration = new PropertiesConfiguration(applicationSettingsFile);
        ReloadingStrategy strategy = new FileChangedReloadingStrategy();
        appicationConfiguration.setReloadingStrategy(strategy);

        appicationConfiguration.setAutoSave(true);

        return appicationConfiguration;
    }

    private File getApplicationSettingsFile(File selemcaHome) {
        File applicationSettingsFile = new File(selemcaHome, APPLICATION_SETTINGS_FILE);
        if (applicationSettingsFile.canRead()) {
            return applicationSettingsFile;
        } else {
            String message = String.format("Application settings could not be read. Please make sure file exists and can be read at %s.", applicationSettingsFile.getAbsoluteFile());
            Logger.getLogger(getClass().getSimpleName()).warning(message);
            throw new IllegalStateException(message);
        }
    }
}
