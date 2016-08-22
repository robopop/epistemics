/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import selemca.epistemics.data.config.PersistenceConfig;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import static org.eclipse.persistence.config.PersistenceUnitProperties.CREATE_OR_EXTEND;
import static org.eclipse.persistence.config.PersistenceUnitProperties.DDL_GENERATION;

/**
 * Configuration for persistence.
 * Uses Spring JPA.
 */
@Configuration
@EnableJpaRepositories(basePackages="selemca.epistemics.mentalworld.beliefsystem.repository")
@EnableTransactionManagement
public class PersistenceContext {
    private static final String DATABASE_SETTINGS_FILE = "database.properties";

    @Bean
    EntityManagerFactory entityManagerFactory(Properties persistenceProperties) {
        Map<String, String> propertiesMap = new HashMap<String, String>();
        // Generate (or extent) database tables if needed
        propertiesMap.put(DDL_GENERATION, CREATE_OR_EXTEND);

        for (String key : persistenceProperties.stringPropertyNames()) {
            String value = persistenceProperties.getProperty(key);
            propertiesMap.put(key, value);
        }
        return Persistence.createEntityManagerFactory(PersistenceConfig.PERSISTENCE_UNIT_NAME, propertiesMap);
    }

    /**
     * Creates the transaction manager bean that integrates the used JPA provider with the
     * Spring transaction mechanism.
     * @param entityManagerFactory  The used JPA entity manager factory.
     * @return
     */
    @Bean
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public Properties persistenceProperties(File selemcaHome) throws IOException {
        File databaseSettingsFile = getDatabaseSettingsFile(selemcaHome);
        Properties properties = new Properties();
        properties.load(new FileInputStream(databaseSettingsFile));
        return properties;
    }

    private File getDatabaseSettingsFile(File selemcaHome) {
        File databaseSettingsFile = new File(selemcaHome, DATABASE_SETTINGS_FILE);
        if (databaseSettingsFile.canRead()) {
            return databaseSettingsFile;
        } else {
            String message = String.format("Database settings could not be read. Please make sure file exists and can be read at %s.", databaseSettingsFile.getAbsoluteFile());
            Logger.getLogger(getClass().getSimpleName()).warning(message);
            throw new IllegalStateException(message);
        }
    }
}
