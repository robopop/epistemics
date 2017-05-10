/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.repository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.logging.Logger;

@Configuration
public class BeliefSystemTestConfig {
    private static final String SELEMCA_LOCATION_ENV = "SELEMCA_HOME";

    @Bean
    public File selemcaHome() {
        return getSelemcaHome();
    }

    public static File getSelemcaHome() {
        String selemcaLocation = System.getenv(SELEMCA_LOCATION_ENV);
        if (selemcaLocation == null) {
            selemcaLocation = System.getProperty(SELEMCA_LOCATION_ENV);
        }
        if (selemcaLocation != null) {
            File selemcaHome = new File(selemcaLocation);
            if (selemcaHome.exists()) {
                return selemcaHome;
            } else {
                String message = String.format("Selemca home directory does not exist. Please create %s.", selemcaHome.getAbsoluteFile());
                Logger.getLogger("SelemcaHome").warning(message);
                throw new IllegalStateException(message);
            }
        } else {
            String message = String.format("Selemca home directory not set. Please set Selemca Home Environment variable %s.", SELEMCA_LOCATION_ENV);
            Logger.getLogger("SelemcaHome").warning(message);
            throw new IllegalStateException(message);
        }
    }
}
