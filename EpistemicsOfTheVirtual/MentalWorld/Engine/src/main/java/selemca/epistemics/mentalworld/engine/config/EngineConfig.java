/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import selemca.epistemics.mentalworld.engine.MentalWorldEngine;
import selemca.epistemics.mentalworld.engine.impl.MentalWorldEngineImpl;

/**
 * Spring Configuration for Engine module.
 * Must be included in WebApplicationContext when using this module.
 */
@Configuration
@ComponentScan({"selemca.epistemics.mentalworld.engine"})
public class EngineConfig {
    public MentalWorldEngine mentalWorldEngine() {
        return new MentalWorldEngineImpl();
    }
}
