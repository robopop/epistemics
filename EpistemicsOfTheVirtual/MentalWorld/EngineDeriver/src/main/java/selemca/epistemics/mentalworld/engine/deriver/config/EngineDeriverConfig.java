/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.engine.deriver.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Configuration for Engine module.
 * Must be included in WebApplicationContext when using this module.
 */
@Configuration
@ComponentScan({"selemca.epistemics.mentalworld.engine.deriver"})
public class EngineDeriverConfig {
}
