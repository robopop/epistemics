/*
 * This source file is part of the Epistemics of the Virtual software.
 * It was created by:
 * Johan F. Hoorn - theoretical model and algorithms
 * Henri Zwols - software design and engineering
 */
package selemca.epistemics.mentalworld.beliefsystem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Configuration for BeliefSystem module.
 * Must be included in WebApplicationContext when using this module.
 */
@Configuration
@Import({PersistenceContext.class})
@ComponentScan({"selemca.epistemics.mentalworld.beliefsystem.graph", "selemca.epistemics.mentalworld.beliefsystem.service.impl"})
public class BeliefSystemConfig {
}
