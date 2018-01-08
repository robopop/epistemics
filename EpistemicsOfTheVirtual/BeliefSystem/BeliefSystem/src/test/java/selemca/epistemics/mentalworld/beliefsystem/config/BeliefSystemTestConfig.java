/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.config;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class BeliefSystemTestConfig {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static final String SELEMCA_HOME_PATH = "selemca_home";

    @Bean
    public File selemcaHome() {
        Path resourceDirectory = Paths.get("src/test/resources", SELEMCA_HOME_PATH);
        LOG.info("Resource directory: {}", resourceDirectory.toAbsolutePath().toFile());
        return resourceDirectory.toFile();
    }
}
